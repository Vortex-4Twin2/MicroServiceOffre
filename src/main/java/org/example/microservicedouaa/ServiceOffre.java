package org.example.microservicedouaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceOffre implements IOffre{

    private static final int PRIORITE_GLOBAL = 100;
    private static final int PRIORITE_CATEGORIE = 200;
    private static final int PRIORITE_PRODUIT = 300;


    @Autowired
    public OffreRepository offreRepository;

    @Autowired
    public ProduitClient produitClient;

    public Produit getProduitById( Long id){
        return produitClient.getProduitById(id);
    }

    public Boolean checkProductExists(Long id){
        return produitClient.checkProductExists(id);
    }

    public List<Produit> getProductsByCategory(String category) { return produitClient.getProductsByCategory(category);
    }
    public List<String> getAllCategories() {
        return produitClient.getAllCategories();
    }

    // Ajouter une offre pour UN produit
    @Override
    public Offre createOffreProduit(Long productId, Offre offre) {

        Boolean exists = produitClient.checkProductExists(productId);

        if (!Boolean.TRUE.equals(exists)) {
            throw new RuntimeException("Le produit " + productId + " n'existe pas !");
        }

        offre.setId(null);          // force insert
        offre.setProductId(productId);
        offre.setCategoryName(null);
        offre.setPriorite(resolvePriorite(offre.getPriorite(), productId, null));

        return offreRepository.save(offre);
    }

    //  Ajouter une offre pour une CATEGORIE
    @Override
    public Offre createOffreCategorie(String category, Offre offreBase) {

        List<Produit> produits = produitClient.getProductsByCategory(category);

        if (produits == null || produits.isEmpty()) {
            throw new RuntimeException("Aucun produit trouvé dans la catégorie " + category);
        }

        Offre offre = new Offre();
        offre.setNomOffre(offreBase.getNomOffre());
        offre.setTypeOffre(offreBase.getTypeOffre());
        offre.setValeurReduction(offreBase.getValeurReduction());
        offre.setDateDebut(offreBase.getDateDebut());
        offre.setDateFin(offreBase.getDateFin());
        offre.setStatut(offreBase.getStatut());
        offre.setPriorite(resolvePriorite(offreBase.getPriorite(), null, category));
        offre.setProductId(null);
        offre.setCategoryName(category);

        return offreRepository.save(offre);
    }

    @Override
    public List<Offre> getOffres() {
        return offreRepository.findAll();
    }

    @Override
    public Offre saveOffre(Offre offre) {
        offre.setPriorite(resolvePriorite(offre.getPriorite(), offre.getProductId(), offre.getCategoryName()));
        return offreRepository.save(offre);
    }



    @Override
    public Offre getOffreById(int id) {
        return offreRepository.findById(id).orElse(null);
    }


    @Override
    public Offre updateOffre(int id, Offre o) {
        Offre existing = offreRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setNomOffre(o.getNomOffre());
            existing.setTypeOffre(o.getTypeOffre());
            existing.setValeurReduction(o.getValeurReduction());
            existing.setDateDebut(o.getDateDebut());
            existing.setDateFin(o.getDateFin());
            existing.setStatut(o.getStatut());
            existing.setProductId(o.getProductId());
            existing.setCategoryName(o.getCategoryName());
            existing.setPriorite(resolvePriorite(o.getPriorite(), existing.getProductId(), existing.getCategoryName()));
            return offreRepository.save(existing);
        }
        return null;
    }

    @Override
    public void deleteOffre(int id) {
        offreRepository.deleteById(id);
    }

    // --- ACTIVATION AUTOMATIQUE (Toutes les minutes pour démo) ---
    @Scheduled(fixedRate = 60000)
    public void syncOffresStatut() {
        LocalDate today = LocalDate.now();
        List<Offre> allOffres = offreRepository.findAll();
        
        for (Offre o : allOffres) {
            boolean changed = false;
            
            // Si aujourd'hui est entre debut et fin => ACTIVE
            if (o.getDateDebut() != null && o.getDateFin() != null) {
                if (!today.isBefore(o.getDateDebut()) && !today.isAfter(o.getDateFin())) {
                    if (o.getStatut() != StatutOffre.ACTIVE) {
                        o.setStatut(StatutOffre.ACTIVE);
                        changed = true;
                    }
                } else {
                    // Sinon => INACTIVE
                    if (o.getStatut() != StatutOffre.INACTIVE) {
                        o.setStatut(StatutOffre.INACTIVE);
                        changed = true;
                    }
                }
            }
            if (changed) {
                offreRepository.save(o);
                System.out.println("LOG: Statut de l'offre [" + o.getNomOffre() + "] mis à jour à " + o.getStatut());
            }
        }
    }

    // --- LOGIQUE DE PRIORITE ---
    public Offre getBestOffreForProduct(Long productId) {
        List<Offre> candidates = new ArrayList<>();

        List<Offre> offresProduit = offreRepository.findByProductIdAndStatutOrderByPrioriteDesc(productId, StatutOffre.ACTIVE);
        candidates.addAll(offresProduit);

        Produit produit = null;
        try {
            produit = produitClient.getProduitById(productId);
        } catch (Exception ignored) {
            // If product service is temporarily unavailable, keep product/global offers.
        }

        String category = produit != null ? produit.getCategory() : null;
        if (category != null && !category.isBlank()) {
            String expectedCategory = normalizeCategory(category);
            List<Offre> offresCategorie = offreRepository.findByStatut(StatutOffre.ACTIVE)
                .stream()
                .filter(o -> o.getCategoryName() != null && !o.getCategoryName().isBlank())
                .filter(o -> normalizeCategory(o.getCategoryName()).equals(expectedCategory))
                .toList();
            candidates.addAll(offresCategorie);
        }

        List<Offre> offresGlobales = offreRepository
                .findByProductIdIsNullAndCategoryNameIsNullAndStatutOrderByPrioriteDesc(StatutOffre.ACTIVE);
        candidates.addAll(offresGlobales);

        LocalDate today = LocalDate.now();
        Offre best = null;
        for (Offre candidate : candidates) {
            if (!isApplicableToday(candidate, today)) {
                continue;
            }
            if (best == null || isHigherPriority(candidate, best)) {
                best = candidate;
            }
        }

        return best;
    }

    private boolean isApplicableToday(Offre offre, LocalDate today) {
        if (offre == null || offre.getStatut() != StatutOffre.ACTIVE) {
            return false;
        }
        if (offre.getDateDebut() != null && today.isBefore(offre.getDateDebut())) {
            return false;
        }
        return offre.getDateFin() == null || !today.isAfter(offre.getDateFin());
    }

    private boolean isHigherPriority(Offre a, Offre b) {
        double reductionA = a.getValeurReduction() == null ? 0d : a.getValeurReduction();
        double reductionB = b.getValeurReduction() == null ? 0d : b.getValeurReduction();
        if (Double.compare(reductionA, reductionB) != 0) {
            return reductionA > reductionB;
        }

        int scopeA = scopeRank(a);
        int scopeB = scopeRank(b);
        if (scopeA != scopeB) {
            return scopeA > scopeB;
        }

        int priorityA = a.getPriorite() == null ? 0 : a.getPriorite();
        int priorityB = b.getPriorite() == null ? 0 : b.getPriorite();
        if (priorityA != priorityB) {
            return priorityA > priorityB;
        }

        int idA = a.getId() == null ? 0 : a.getId();
        int idB = b.getId() == null ? 0 : b.getId();
        return idA > idB;
    }

    private int scopeRank(Offre offre) {
        if (offre.getProductId() != null) {
            return 3;
        }
        if (offre.getCategoryName() != null && !offre.getCategoryName().isBlank()) {
            return 2;
        }
        return 1;
    }

    private int resolvePriorite(Integer explicitPriorite, Long productId, String categoryName) {
        if (explicitPriorite != null) {
            return explicitPriorite;
        }
        if (productId != null) {
            return PRIORITE_PRODUIT;
        }
        if (categoryName != null && !categoryName.isBlank()) {
            return PRIORITE_CATEGORIE;
        }
        return PRIORITE_GLOBAL;
    }

    private String normalizeCategory(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.trim().toLowerCase();
    }
}

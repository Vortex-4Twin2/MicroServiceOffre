package org.example.microservicedouaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ServiceOffre implements IOffre{


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

    // Ajouter une offre pour UN produit
    @Override
    public Offre createOffreProduit(Long productId, Offre offre) {

        Boolean exists = produitClient.checkProductExists(productId);

        if (!Boolean.TRUE.equals(exists)) {
            throw new RuntimeException("Le produit " + productId + " n'existe pas !");
        }

        offre.setId(null);          // force insert
        offre.setProductId(productId);

        return offreRepository.save(offre);
    }

    //  Ajouter une offre pour une CATEGORIE
    @Override
    public List<Offre> createOffreCategorie(String category, Offre offreBase) {

        List<Produit> produits = produitClient.getProductsByCategory(category);

        if (produits == null || produits.isEmpty()) {
            throw new RuntimeException("Aucun produit trouvé dans la catégorie " + category);
        }

        List<Offre> offres = produits.stream().map(p -> {
            Offre o = new Offre();
            o.setNomOffre(offreBase.getNomOffre());
            o.setTypeOffre(offreBase.getTypeOffre());
            o.setValeurReduction(offreBase.getValeurReduction());
            o.setDateDebut(offreBase.getDateDebut());
            o.setDateFin(offreBase.getDateFin());
            o.setStatut(offreBase.getStatut());
            o.setProductId(p.getId());   // uniquement productId
            return o;
        }).toList();

        return offreRepository.saveAll(offres);
    }

    @Override
    public List<Offre> getOffres() {
        return offreRepository.findAll();
    }

    @Override
    public Offre saveOffre(Offre offre) {
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
            return offreRepository.save(existing);
        }
        return null;
    }

    @Override
    public void deleteOffre(int id) {
        offreRepository.deleteById(id);
    }
}

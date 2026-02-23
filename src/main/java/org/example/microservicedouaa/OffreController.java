package org.example.microservicedouaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offres")
public class OffreController {

    @Autowired
    public  IOffre offreInterface;

    @Autowired
    public ServiceOffre serviceOffre;

    @GetMapping("/products/{id}")
    public Produit getProduitById(@PathVariable("id") Long id){
        return serviceOffre.getProduitById(id);
    }

    @GetMapping("/products/exists/{id}")
    public Boolean checkProductExists(@PathVariable("id") Long id){
        return serviceOffre.checkProductExists(id);
    }

    @GetMapping("/products/search")
    public List<Produit> getProductsByCategory(@RequestParam String category){
        return serviceOffre.getProductsByCategory(category);
    }

    @GetMapping
    public List<Offre> getOffres() {
        return offreInterface.getOffres();
    }
    @PostMapping
    public Offre saveOffre(@RequestBody Offre offre) {
        return offreInterface.saveOffre(offre);
    }

    @GetMapping("/{id}")
    public Offre getOffreById(@PathVariable int id) {
        return offreInterface.getOffreById(id);
    }


    @PutMapping("/{id}")
    public Offre updateOffre(@PathVariable int id, @RequestBody Offre offre) {
        return offreInterface.updateOffre(id, offre);
    }


    @DeleteMapping("/{id}")
    public void deleteCandidat(@PathVariable int id) {
        offreInterface.deleteOffre(id);
    }

    @PostMapping("/produit/{productId}")
    public Offre addOffreProduit(@PathVariable Long productId, @RequestBody Offre offre) {
        return serviceOffre.createOffreProduit(productId, offre);
    }

    @PostMapping("/categorie/{category}")
    public List<Offre> addOffreCategorie(@PathVariable String category, @RequestBody Offre offre) {
        return serviceOffre.createOffreCategorie(category, offre);
    }
}

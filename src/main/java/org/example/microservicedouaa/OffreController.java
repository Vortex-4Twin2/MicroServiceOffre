package org.example.microservicedouaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offres")
public class OffreController {

    @Autowired
    public  IOffre offreInterface;

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
}

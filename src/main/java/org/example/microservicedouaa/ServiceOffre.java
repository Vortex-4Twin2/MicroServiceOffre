package org.example.microservicedouaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceOffre implements IOffre{


    @Autowired
    public OffreRepository offreRepository;
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

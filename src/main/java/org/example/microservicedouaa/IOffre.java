package org.example.microservicedouaa;

import java.util.List;

public interface IOffre {
    public List<Offre> getOffres();
    public Offre saveOffre(Offre offre);
    public  Offre getOffreById(int id);
    public  Offre updateOffre(int id, Offre c);
    public void deleteOffre(int id);
}

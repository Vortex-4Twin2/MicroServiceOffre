package org.example.microservicedouaa;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nomOffre;

    @Enumerated(EnumType.STRING)
    private TypeOffre typeOffre;

    private Double valeurReduction;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutOffre statut;

    private Long productId;


    public Offre() {
    }

    public Offre(String nomOffre, TypeOffre typeOffre, Double valeurReduction, LocalDate dateDebut, LocalDate dateFin, StatutOffre statut, Long productId) {
        this.nomOffre = nomOffre;
        this.typeOffre = typeOffre;
        this.valeurReduction = valeurReduction;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.productId = productId;
    }

    public StatutOffre getStatut() {
        return statut;
    }

    public void setStatut(StatutOffre statut) {
        this.statut = statut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Double getValeurReduction() {
        return valeurReduction;
    }

    public void setValeurReduction(Double valeurReduction) {
        this.valeurReduction = valeurReduction;
    }

    public TypeOffre getTypeOffre() {
        return typeOffre;
    }

    public void setTypeOffre(TypeOffre typeOffre) {
        this.typeOffre = typeOffre;
    }

    public String getNomOffre() {
        return nomOffre;
    }

    public void setNomOffre(String nomOffre) {
        this.nomOffre = nomOffre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

}

package org.example.microservicedouaa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OffreRepository extends JpaRepository<Offre, Integer> {
    Optional<Offre> findByProductIdAndStatut(Long productId, StatutOffre statut);

    List<Offre> findByStatut(StatutOffre statut);

    List<Offre> findByProductIdAndStatutOrderByPrioriteDesc(Long productId, StatutOffre statut);

    List<Offre> findByCategoryNameIgnoreCaseAndStatutOrderByPrioriteDesc(String categoryName, StatutOffre statut);

    List<Offre> findByProductIdIsNullAndCategoryNameIsNullAndStatutOrderByPrioriteDesc(StatutOffre statut);
}

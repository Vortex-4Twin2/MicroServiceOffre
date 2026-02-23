package org.example.microservicedouaa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OffreRepository extends JpaRepository<Offre, Integer> {
    Optional<Offre> findByProductIdAndStatut(Long productId, StatutOffre statut);


}

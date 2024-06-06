package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findByUserId(String userId);
}

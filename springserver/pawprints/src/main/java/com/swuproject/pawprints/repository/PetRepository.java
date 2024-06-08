package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findByUserId(String userId);

    @Query("SELECT p FROM Pet p JOIN LostReports lr ON p.id = lr.petId WHERE p.userId = :userId")
    List<Pet> findLostPetsByUserId(String userId);
}

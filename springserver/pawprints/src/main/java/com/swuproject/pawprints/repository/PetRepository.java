package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    // Pet
    List<Pet> findByUserId(String userId);

    @Query("SELECT p FROM Pet p JOIN LostReports lr ON p.id = lr.petId WHERE p.userId = :userId")
    List<Pet> findLostPetsByUserId(String userId);

    @Query("SELECT p FROM Pet p WHERE p.id = :id")
    List<Pet> findLostPetsByPetId(int id);

    @Query("SELECT p FROM Pet p WHERE p.id = :id")
    Optional<Pet> findPetsByPetId(int id);
}

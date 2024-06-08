package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.Pet;
import com.swuproject.pawprints.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> getPetsByUserId(String userId) {
        return petRepository.findByUserId(userId);
    }

    public List<Pet> getLostPetsByUserId(String userId) {
        return petRepository.findLostPetsByUserId(userId);
    }
}

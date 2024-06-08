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

    public void updatePet(int petId, Pet updatedPet) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));
        pet.setName(updatedPet.getName());
        pet.setType(updatedPet.getType());
        pet.setBreed(updatedPet.getBreed());
        pet.setAge(updatedPet.getAge());
        pet.setGender(updatedPet.getGender());
        pet.setColor(updatedPet.getColor());
        pet.setFeature(updatedPet.getFeature());
        petRepository.save(pet);
    }

    public void deletePet(int petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));
        petRepository.delete(pet);
    }

    public void addPet(Pet pet) {
        petRepository.save(pet);
    }
}
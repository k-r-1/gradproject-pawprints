package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.Pet;
import com.swuproject.pawprints.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    // Pet
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/{userId}")
    public List<Pet> getPetsByUserId(@PathVariable String userId) {
        return petService.getPetsByUserId(userId);
    }

    @GetMapping("/{userId}/lost")
    public List<Pet> getLostPetsByUserId(@PathVariable String userId) {
        return petService.getLostPetsByUserId(userId);
    }

    @PostMapping("/{petId}")
    public void updatePet(@PathVariable int petId, @RequestBody Pet pet) {
        petService.updatePet(petId, pet);
    }
}

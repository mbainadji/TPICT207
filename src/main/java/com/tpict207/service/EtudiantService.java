package com.tpict207.service;

import com.tpict207.model.Etudiant;
import com.tpict207.repository.EtudiantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EtudiantService {
    private final EtudiantRepository etudiantRepository;

    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public Optional<Etudiant> getEtudiantById(Long id) {
        return etudiantRepository.findById(id);
    }

    @Transactional
    public Etudiant addEtudiant(String nom, String matricule) {
        Etudiant etudiant = new Etudiant(null, nom, matricule);
        return etudiantRepository.save(etudiant);
    }
}

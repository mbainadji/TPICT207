package com.tpict207.service;

import com.tpict207.model.Cours;
import com.tpict207.repository.CoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoursService {
    private final CoursRepository coursRepository;

    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }

    public Optional<Cours> getCoursById(Long id) {
        return coursRepository.findById(id);
    }

    @Transactional
    public Cours addCours(String nom, String code) {
        Cours cours = new Cours(null, nom, code);
        return coursRepository.save(cours);
    }
}

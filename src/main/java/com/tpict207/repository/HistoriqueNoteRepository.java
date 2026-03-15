package com.tpict207.repository;

import com.tpict207.model.HistoriqueNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueNoteRepository extends JpaRepository<HistoriqueNote, Long> {
    List<HistoriqueNote> findByNote_IdOrderByDateModificationDesc(Long noteId);
}

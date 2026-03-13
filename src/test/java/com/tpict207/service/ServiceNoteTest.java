package com.tpict207.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceNoteTest {
    private ServiceNote serviceNote = new ServiceNote();

    @Test
    public void testValidationNote() {
        // Test note invalide (trop haute)
        assertFalse(serviceNote.ajouterNote(1, 1, 25.0, 1), "La note ne doit pas depasser 20.");
        
        // Test note invalide (negative)
        assertFalse(serviceNote.ajouterNote(1, 1, -5.0, 1), "La note ne doit pas etre negative.");
    }
}

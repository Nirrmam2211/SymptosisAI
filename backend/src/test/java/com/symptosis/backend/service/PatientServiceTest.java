package com.symptosis.backend.service;

import com.symptosis.backend.dto.CreatePatientRequest;
import com.symptosis.backend.dto.PatientResponse;
import com.symptosis.backend.model.Patient;
import com.symptosis.backend.model.Symptom;
import com.symptosis.backend.model.SymptomRecord;
import com.symptosis.backend.repository.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void createPatientSavesEntityAndMapsResponse() {
        CreatePatientRequest request = new CreatePatientRequest();
        request.setFullName("Aarav Shah");
        request.setPatientCode("PT-1001");
        request.setAge(24);
        request.setGender("Male");

        Patient saved = patientEntity(12L, "Aarav Shah", "PT-1001", 24, "Male");
        when(patientRepository.save(org.mockito.ArgumentMatchers.any(Patient.class))).thenReturn(saved);

        PatientResponse response = patientService.createPatient(request);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        assertEquals("Aarav Shah", captor.getValue().getFullName());
        assertEquals("PT-1001", captor.getValue().getPatientCode());
        assertEquals(12L, response.getId());
        assertEquals("Aarav Shah", response.getFullName());
        assertEquals("PT-1001", response.getPatientCode());
        assertEquals(24, response.getAge());
        assertEquals("Male", response.getGender());
    }

    @Test
    void mapSortsHistoryByTimestampAndCopiesFields() {
        Patient patient = patientEntity(5L, "Jane Smith", "PT-2002", 31, "Female");
        patient.setRegisteredDate(LocalDate.of(2026, 4, 20));
        patient.setSymptomRecords(new ArrayList<>(List.of(
                symptomRecord(2L, symptom("Fever", "General"), 7, 12, 45, LocalDateTime.of(2026, 4, 22, 10, 0), "Later"),
                symptomRecord(1L, symptom("Fever", "General"), 5, 8, 30, LocalDateTime.of(2026, 4, 21, 10, 0), "Earlier")
        )));

        PatientResponse response = patientService.map(patient);

        assertEquals(2, response.getSymptomHistory().size());
        assertEquals(1L, response.getSymptomHistory().get(0).getId());
        assertEquals("Earlier", response.getSymptomHistory().get(0).getNote());
        assertEquals(2L, response.getSymptomHistory().get(1).getId());
        assertEquals("Later", response.getSymptomHistory().get(1).getNote());
        assertEquals(LocalDate.of(2026, 4, 20), response.getRegisteredDate());
    }

    @Test
    void getPatientEntityThrowsWhenMissing() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> patientService.getPatientEntity(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
    }

    private Patient patientEntity(Long id, String name, String code, int age, String gender) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFullName(name);
        patient.setPatientCode(code);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setRegisteredDate(LocalDate.now());
        patient.setSymptomRecords(new ArrayList<>());
        return patient;
    }

    private Symptom symptom(String name, String category) {
        Symptom symptom = new Symptom();
        symptom.setName(name);
        symptom.setCategory(category);
        return symptom;
    }

    private SymptomRecord symptomRecord(Long id, Symptom symptom, int severity, int frequency, int duration,
                                        LocalDateTime timestamp, String note) {
        SymptomRecord record = new SymptomRecord();
        record.setId(id);
        record.setSymptom(symptom);
        record.setSeverity(severity);
        record.setFrequency(frequency);
        record.setDurationMinutes(duration);
        record.setTimestamp(timestamp);
        record.setNote(note);
        return record;
    }
}

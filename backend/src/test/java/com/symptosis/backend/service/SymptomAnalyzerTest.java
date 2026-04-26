package com.symptosis.backend.service;

import com.symptosis.backend.dto.AnalyticsSnapshot;
import com.symptosis.backend.model.Symptom;
import com.symptosis.backend.model.SymptomRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SymptomAnalyzerTest {

    private final SymptomAnalyzer symptomAnalyzer = new SymptomAnalyzer();

    @Test
    void analyzeCalculatesAveragesAndSeverityTrend() {
        List<SymptomRecord> records = List.of(
                record("Fever", 4, 6, 30, LocalDateTime.of(2026, 4, 20, 10, 0)),
                record("Fever", 6, 8, 45, LocalDateTime.of(2026, 4, 21, 10, 0)),
                record("Fever", 9, 10, 60, LocalDateTime.of(2026, 4, 22, 10, 0))
        );

        AnalyticsSnapshot snapshot = symptomAnalyzer.analyze(records);

        assertEquals(6.333333333333333, snapshot.getAverageSeverity(), 0.0001);
        assertEquals(8.0, snapshot.getAverageFrequency(), 0.0001);
        assertEquals(45.0, snapshot.getAverageDuration(), 0.0001);
        assertEquals(5.0, snapshot.getSeverityTrend(), 0.0001);
        assertEquals(3, snapshot.getRecordCount());
    }

    @Test
    void analyzeReturnsEmptySnapshotForNoData() {
        AnalyticsSnapshot snapshot = symptomAnalyzer.analyze(List.of());

        assertEquals(0.0, snapshot.getAverageSeverity(), 0.0001);
        assertEquals(0.0, snapshot.getAverageFrequency(), 0.0001);
        assertEquals(0.0, snapshot.getAverageDuration(), 0.0001);
        assertEquals(0.0, snapshot.getSeverityTrend(), 0.0001);
        assertEquals(0, snapshot.getRecordCount());
    }

    private SymptomRecord record(String name, int severity, int frequency, int duration, LocalDateTime timestamp) {
        Symptom symptom = new Symptom();
        symptom.setName(name);
        symptom.setCategory("General");

        SymptomRecord record = new SymptomRecord();
        record.setSymptom(symptom);
        record.setSeverity(severity);
        record.setFrequency(frequency);
        record.setDurationMinutes(duration);
        record.setTimestamp(timestamp);
        return record;
    }
}

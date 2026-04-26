package com.symptosis.backend.service;

import com.symptosis.backend.config.AppProperties;
import com.symptosis.backend.dto.AnalyticsSnapshot;
import com.symptosis.backend.model.RiskLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskEvaluatorTest {

    @Test
    void calculateRuleScoreUsesConfiguredWeights() {
        AppProperties properties = new AppProperties();
        properties.getWeights().setSeverity(0.5);
        properties.getWeights().setFrequency(0.3);
        properties.getWeights().setDuration(0.2);

        RiskEvaluator evaluator = new RiskEvaluator(properties);
        AnalyticsSnapshot snapshot = new AnalyticsSnapshot();
        snapshot.setSeverityTrend(4.0);
        snapshot.setAverageFrequency(6.0);
        snapshot.setAverageDuration(30.0);

        assertEquals(4.4, evaluator.calculateRuleScore(snapshot), 0.0001);
    }

    @Test
    void classifyUsesExpectedThresholds() {
        AppProperties properties = new AppProperties();
        RiskEvaluator evaluator = new RiskEvaluator(properties);

        assertEquals(RiskLevel.LOW, evaluator.classify(3.9));
        assertEquals(RiskLevel.MEDIUM, evaluator.classify(4.0));
        assertEquals(RiskLevel.MEDIUM, evaluator.classify(7.49));
        assertEquals(RiskLevel.HIGH, evaluator.classify(7.5));
    }
}

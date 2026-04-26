package com.symptosis.backend.service;

import com.symptosis.backend.config.AppProperties;
import com.symptosis.backend.dto.RiskResponse;
import com.symptosis.backend.model.RiskLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataManagerTest {

    @Test
    void cacheRiskStoresAndReturnsResponse() {
        AppProperties properties = new AppProperties();
        properties.getFeatures().setEnableLogging(false);

        DataManager dataManager = new DataManager(properties);

        RiskResponse response = new RiskResponse();
        response.setPatientId(7L);
        response.setRiskLevel(RiskLevel.HIGH);
        response.setRuleScore(8.75);
        response.setDecisionSource("RULE_BASED");

        dataManager.cacheRisk(response);

        RiskResponse cached = dataManager.getCachedRisk(7L).orElseThrow();
        assertEquals(7L, cached.getPatientId());
        assertEquals(RiskLevel.HIGH, cached.getRiskLevel());
        assertEquals(8.75, cached.getRuleScore(), 0.0001);
    }
}

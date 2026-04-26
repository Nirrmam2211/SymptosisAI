package com.symptosis.backend.controller;

import com.symptosis.backend.dto.DatabaseProbeResponse;
import com.symptosis.backend.service.DatabaseProbeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiagnosticsController {

    private final DatabaseProbeService databaseProbeService;

    public DiagnosticsController(DatabaseProbeService databaseProbeService) {
        this.databaseProbeService = databaseProbeService;
    }

    @GetMapping("/diagnostics/database")
    public DatabaseProbeResponse probeDatabase() {
        return databaseProbeService.probe();
    }
}

package com.symptosis.backend.service;

import com.symptosis.backend.dto.DatabaseProbeResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseProbeServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DatabaseProbeService databaseProbeService;

    @Test
    void probeReturnsSuccessWhenSelectOneSucceeds() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("SELECT 1")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        DatabaseProbeResponse response = databaseProbeService.probe();

        assertTrue(response.isConnected());
        assertTrue(response.getMessage().contains("successful"));
    }

    @Test
    void probeReturnsFailureWhenConnectionFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new java.sql.SQLException("boom"));

        DatabaseProbeResponse response = databaseProbeService.probe();

        assertFalse(response.isConnected());
        assertTrue(response.getMessage().contains("failed"));
    }
}

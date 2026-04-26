package com.symptosis.backend.service;

import com.symptosis.backend.dto.DatabaseProbeResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;

@Service
public class DatabaseProbeService {

    private final DataSource dataSource;

    public DatabaseProbeService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DatabaseProbeResponse probe() {
        DatabaseProbeResponse response = new DatabaseProbeResponse();
        response.setTestedAt(LocalDateTime.now());

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {

            boolean success = resultSet.next() && resultSet.getInt(1) == 1;
            response.setConnected(success);
            response.setMessage(success ? "JDBC connection successful" : "JDBC connection returned an unexpected result");
        } catch (SQLException ex) {
            response.setConnected(false);
            response.setMessage("JDBC connection failed: " + ex.getMessage());
        }

        return response;
    }
}

package com.symptosis.frontend;

import com.symptosis.frontend.client.BackendApiClient;
import com.symptosis.frontend.model.ExplanationView;
import com.symptosis.frontend.model.PatientView;
import com.symptosis.frontend.model.RiskView;
import com.symptosis.frontend.model.SymptomRecordView;
import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SymptosisDashboardApp extends Application {

    private final BackendApiClient apiClient = new BackendApiClient();
    private final TextField patientNameField = new TextField();
    private final TextField patientCodeField = new TextField();
    private final Spinner<Integer> ageSpinner = createEditableSpinner(1, 100, 25);
    private final ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Male", "Female", "Other"));
    private final TextField patientIdField = new TextField();
    private final TextField symptomNameField = new TextField();
    private final TextField symptomCategoryField = new TextField("General");
    private final Spinner<Integer> severitySpinner = createEditableSpinner(1, 10, 5);
    private final Spinner<Integer> frequencySpinner = createEditableSpinner(1, 20, 3);
    private final Spinner<Integer> durationSpinner = createEditableSpinner(1, 1440, 15);
    private final TextArea notesArea = new TextArea();
    private final Label riskBadge = new Label("Risk: N/A");
    private final Label metricsLabel = new Label("Metrics will appear here.");
    private final Label patientContextLabel = new Label("No patient loaded.");
    private final Label actionStatusLabel = new Label("Ready.");
    private final TextArea explanationArea = new TextArea();
    private final ListView<String> historyList = new ListView<>();
    private final Label recordCountLabel = createMetricLabel("Records", "0");
    private final Label averageSeverityLabel = createMetricLabel("Avg Severity", "0.0");
    private final Label averageFrequencyLabel = createMetricLabel("Avg Frequency", "0.0");
    private final Label averageDurationLabel = createMetricLabel("Avg Duration", "0 min");
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis(0, 10, 1);
    private final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);

    @Override
    public void start(Stage stage) {
        configureControls();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f4f8fb, #ebf2f6);");
        SplitPane splitPane = new SplitPane(buildFormPane(), buildAnalyticsPane());
        splitPane.setDividerPositions(0.42);
        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1440, 860);
        stage.setTitle("Symptosis AI Dashboard");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(760);
        stage.show();
    }

    private void configureControls() {
        genderBox.getSelectionModel().selectFirst();
        patientNameField.setPromptText("Example: Aarav Shah");
        patientCodeField.setPromptText("Example: PT-HIGH-01");
        patientIdField.setPromptText("Enter existing Patient ID or auto-fill after Add Patient");
        symptomNameField.setPromptText("Example: Fever");
        symptomCategoryField.setPromptText("Example: General");
        notesArea.setPromptText("Optional symptom progression note");
        notesArea.setPrefRowCount(4);
        explanationArea.setWrapText(true);
        explanationArea.setEditable(false);
        explanationArea.setPrefRowCount(7);
        explanationArea.setStyle(panelBodyStyle());
        riskBadge.setStyle(riskStyle("N/A"));
        patientContextLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        actionStatusLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #124c7c; -fx-font-weight: bold;");
        metricsLabel.setStyle("-fx-text-fill: #4a5d70; -fx-font-size: 13px;");
        historyList.setPlaceholder(new Label("No symptom records loaded yet."));
        historyList.setStyle(panelBodyStyle());

        xAxis.setLabel("Record Index");
        yAxis.setLabel("Severity");
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(false);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setCreateSymbols(true);
        chart.setMinHeight(320);
        chart.setPrefHeight(360);
        chart.setStyle("-fx-background-color: transparent;");
    }

    private Spinner<Integer> createEditableSpinner(int min, int max, int initialValue) {
        Spinner<Integer> spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue));
        spinner.setEditable(true);
        spinner.focusedProperty().addListener((obs, oldValue, focused) -> {
            if (!focused) {
                spinner.increment(0);
            }
        });
        spinner.getEditor().setOnAction(event -> spinner.increment(0));
        return spinner;
    }

    private Node buildFormPane() {
        GridPane patientForm = new GridPane();
        patientForm.setHgap(12);
        patientForm.setVgap(12);
        addRow(patientForm, 0, "Patient Name", patientNameField);
        addRow(patientForm, 1, "Patient Code", patientCodeField);
        addRow(patientForm, 2, "Age", ageSpinner);
        addRow(patientForm, 3, "Gender", genderBox);

        Button createPatientButton = new Button("Add Patient");
        createPatientButton.setDefaultButton(true);
        stylePrimaryButton(createPatientButton);
        createPatientButton.setOnAction(event -> createPatient());
        patientForm.add(createPatientButton, 1, 4);

        GridPane symptomForm = new GridPane();
        symptomForm.setHgap(12);
        symptomForm.setVgap(12);
        addRow(symptomForm, 0, "Patient ID", patientIdField);
        addRow(symptomForm, 1, "Symptom", symptomNameField);
        addRow(symptomForm, 2, "Category", symptomCategoryField);
        addRow(symptomForm, 3, "Severity", severitySpinner);
        addRow(symptomForm, 4, "Frequency", frequencySpinner);
        addRow(symptomForm, 5, "Duration (min)", durationSpinner);
        addRow(symptomForm, 6, "Notes", notesArea);

        Button loadPatientButton = new Button("Load Existing Patient");
        styleSecondaryButton(loadPatientButton);
        loadPatientButton.setOnAction(event -> loadExistingPatient());
        Button addSymptomButton = new Button("Add Symptom Record");
        stylePrimaryButton(addSymptomButton);
        addSymptomButton.setOnAction(event -> addSymptom());
        Button refreshButton = new Button("Refresh Analytics");
        styleSecondaryButton(refreshButton);
        refreshButton.setOnAction(event -> refreshAll());
        HBox actions = new HBox(10, loadPatientButton, addSymptomButton, refreshButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(18,
                createCard(createSectionTitle("Patient Registration"), patientForm),
                createCard(createSectionTitle("Symptom Progression Entry"), symptomForm),
                actions,
                actionStatusLabel);
        left.setPadding(new Insets(10));
        left.setFillWidth(true);
        VBox.setVgrow(notesArea, Priority.ALWAYS);
        return left;
    }

    private Node buildAnalyticsPane() {
        HBox metricRow = new HBox(12,
                recordCountLabel,
                averageSeverityLabel,
                averageFrequencyLabel,
                averageDurationLabel);
        metricRow.setAlignment(Pos.CENTER_LEFT);

        VBox right = new VBox(14,
                riskBadge,
                patientContextLabel,
                metricsLabel,
                metricRow,
                createSectionTitle("Severity vs Time"),
                chart,
                createSectionTitle("History"),
                historyList,
                createSectionTitle("Explanation"),
                explanationArea);
        right.setPadding(new Insets(10));
        VBox.setVgrow(chart, Priority.ALWAYS);
        VBox.setVgrow(historyList, Priority.ALWAYS);
        return createCard(right);
    }

    private void addRow(GridPane grid, int row, String title, Node input) {
        Label label = new Label(title);
        label.setMinWidth(130);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #274055;");
        grid.add(label, 0, row);
        grid.add(input, 1, row);
        if (input instanceof TextField textField) {
            textField.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(textField, Priority.ALWAYS);
            textField.setStyle(inputStyle());
        } else if (input instanceof TextArea textArea) {
            textArea.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            textArea.setStyle(inputStyle());
        } else if (input instanceof ComboBox<?> comboBox) {
            comboBox.setPrefWidth(220);
            comboBox.setStyle(inputStyle());
        } else if (input instanceof Spinner<?> spinner) {
            spinner.setEditable(true);
            spinner.setPrefWidth(180);
            spinner.getEditor().setPrefColumnCount(6);
            spinner.getEditor().setStyle(inputStyle());
        }
    }

    private void createPatient() {
        try {
            PatientView patient = apiClient.createPatient(Map.of(
                    "fullName", patientNameField.getText().trim(),
                    "patientCode", patientCodeField.getText().trim(),
                    "age", ageSpinner.getValue(),
                    "gender", genderBox.getValue()
            ));
            patientIdField.setText(String.valueOf(patient.id));
            patientContextLabel.setText("Current patient: " + patient.fullName + " (ID " + patient.id + ")");
            actionStatusLabel.setText("Patient created successfully. You can now add symptom records.");
            explanationArea.setText("Patient created successfully. Add symptom records or load this patient later using the Patient ID field.");
            clearAnalyticsView();
        } catch (Exception ex) {
            actionStatusLabel.setText("Create patient failed.");
            explanationArea.setText("Create patient failed: " + friendlyMessage(ex));
        }
    }

    private void loadExistingPatient() {
        try {
            long patientId = requirePatientId();
            PatientView patient = apiClient.getPatient(patientId);
            hydratePatientForm(patient);
            updateHistory(patient);
            patientContextLabel.setText("Loaded patient: " + patient.fullName + " (ID " + patient.id + ")");
            actionStatusLabel.setText("Existing patient loaded. You can add more symptoms now.");
            loadRiskAndExplanation(patientId);
        } catch (Exception ex) {
            actionStatusLabel.setText("Load patient failed.");
            explanationArea.setText("Load patient failed: " + friendlyMessage(ex));
        }
    }

    private void addSymptom() {
        try {
            long patientId = requirePatientId();
            SymptomRecordView record = apiClient.addSymptom(Map.of(
                    "patientId", patientId,
                    "symptomName", symptomNameField.getText().trim(),
                    "symptomCategory", symptomCategoryField.getText().trim(),
                    "severity", severitySpinner.getValue(),
                    "frequency", frequencySpinner.getValue(),
                    "durationMinutes", durationSpinner.getValue(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    "note", notesArea.getText().trim()
            ));
            actionStatusLabel.setText("Symptom added: " + record.symptomName + " at severity " + record.severity + ".");
            explanationArea.setText("Symptom record saved successfully. Refreshing patient state and analytics.");
            refreshAll();
        } catch (Exception ex) {
            actionStatusLabel.setText("Add symptom failed.");
            explanationArea.setText("Add symptom failed: " + friendlyMessage(ex));
        }
    }

    private void refreshAll() {
        try {
            long patientId = requirePatientId();
            PatientView patient = apiClient.getPatient(patientId);
            hydratePatientForm(patient);
            updateHistory(patient);
            patientContextLabel.setText("Current patient: " + patient.fullName + " (ID " + patient.id + ")");
            actionStatusLabel.setText("Patient history refreshed.");
            loadRiskAndExplanation(patientId);
        } catch (Exception ex) {
            actionStatusLabel.setText("Refresh failed.");
            explanationArea.setText("Refresh failed: " + friendlyMessage(ex));
        }
    }

    private void loadRiskAndExplanation(long patientId) {
        try {
            RiskView risk = apiClient.getRisk(patientId);
            updateRisk(risk);
        } catch (Exception ex) {
            riskBadge.setText("Risk: N/A");
            riskBadge.setStyle(riskStyle("N/A"));
            metricsLabel.setText("Risk service unavailable: " + friendlyMessage(ex));
        }

        try {
            ExplanationView explanation = apiClient.getExplanation(patientId);
            explanationArea.setText(explanation.explanation);
        } catch (Exception ex) {
            explanationArea.setText("Explanation service unavailable: " + friendlyMessage(ex));
        }
    }

    private void hydratePatientForm(PatientView patient) {
        patientNameField.setText(patient.fullName);
        patientCodeField.setText(patient.patientCode);
        ageSpinner.getValueFactory().setValue(patient.age);
        genderBox.setValue(patient.gender);
        patientIdField.setText(String.valueOf(patient.id));
    }

    private void updateHistory(PatientView patient) {
        historyList.getItems().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        int index = 1;
        for (SymptomRecordView record : patient.symptomHistory) {
            historyList.getItems().add(String.format(
                    "%s | %s | severity=%d | frequency=%d | duration=%d min | %s",
                    record.timestamp,
                    record.symptomName,
                    record.severity,
                    record.frequency,
                    record.durationMinutes,
                    record.note == null || record.note.isBlank() ? "No note" : record.note
            ));
            series.getData().add(new XYChart.Data<>(index++, record.severity));
        }

        recordCountLabel.setText(metricValue("Records", String.valueOf(patient.symptomHistory.size())));

        chart.getData().clear();
        if (!series.getData().isEmpty()) {
            chart.getData().add(series);
        }

        if (patient.symptomHistory.isEmpty()) {
            historyList.getItems().add("No symptom records found for this patient yet.");
        }
    }

    private void clearAnalyticsView() {
        chart.getData().clear();
        historyList.getItems().clear();
        historyList.getItems().add("No symptom records loaded yet.");
        riskBadge.setText("Risk: N/A");
        riskBadge.setStyle(riskStyle("N/A"));
        metricsLabel.setText("Metrics will appear here.");
        recordCountLabel.setText(metricValue("Records", "0"));
        averageSeverityLabel.setText(metricValue("Avg Severity", "0.0"));
        averageFrequencyLabel.setText(metricValue("Avg Frequency", "0.0"));
        averageDurationLabel.setText(metricValue("Avg Duration", "0 min"));
    }

    private void updateRisk(RiskView risk) {
        riskBadge.setText("Risk: " + risk.riskLevel + " | Source: " + risk.decisionSource);
        riskBadge.setStyle(riskStyle(risk.riskLevel));
        metricsLabel.setText(String.format(
                "Score %.2f | Severity Trend %.2f | Avg Severity %.2f | ML %s",
                risk.ruleScore,
                risk.analytics.severityTrend,
                risk.analytics.averageSeverity,
                risk.mlPrediction
        ));
        averageSeverityLabel.setText(metricValue("Avg Severity", String.format("%.1f", risk.analytics.averageSeverity)));
        averageFrequencyLabel.setText(metricValue("Avg Frequency", String.format("%.1f", risk.analytics.averageFrequency)));
        averageDurationLabel.setText(metricValue("Avg Duration", String.format("%.1f min", risk.analytics.averageDuration)));
    }

    private Label createMetricLabel(String title, String value) {
        Label label = new Label(metricValue(title, value));
        label.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16; -fx-padding: 10 14; "
                + "-fx-border-color: #d7e3ec; -fx-border-radius: 16; -fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-text-fill: #183247;");
        return label;
    }

    private String metricValue(String title, String value) {
        return title + ": " + value;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #17324a;");
        return label;
    }

    private VBox createCard(Node... children) {
        VBox card = new VBox(12, children);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 24; "
                + "-fx-border-color: #dce7ef; -fx-border-radius: 24;");
        return card;
    }

    private void stylePrimaryButton(Button button) {
        button.setStyle("-fx-background-color: #0f6c8d; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 14; -fx-padding: 10 16;");
    }

    private void styleSecondaryButton(Button button) {
        button.setStyle("-fx-background-color: #e8f0f5; -fx-text-fill: #17324a; -fx-font-weight: bold; "
                + "-fx-background-radius: 14; -fx-padding: 10 16;");
    }

    private String inputStyle() {
        return "-fx-background-color: #f8fbfd; -fx-background-radius: 12; -fx-border-color: #d7e3ec; "
                + "-fx-border-radius: 12; -fx-padding: 8 10;";
    }

    private String panelBodyStyle() {
        return "-fx-background-color: #f8fbfd; -fx-background-radius: 18; -fx-border-color: #d7e3ec; "
                + "-fx-border-radius: 18;";
    }

    private long requirePatientId() {
        String value = patientIdField.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Enter a Patient ID or create/load a patient first.");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Patient ID must be numeric. Current value: " + value);
        }
    }

    private String friendlyMessage(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return ex.getMessage();
        }
        if (ex instanceof HttpTimeoutException) {
            return "request timed out. If ML API or Ollama is not running, disable it in .env or start those services.";
        }
        if (ex instanceof IOException && ex.getMessage() != null && !ex.getMessage().isBlank()) {
            return ex.getMessage();
        }
        return ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
    }

    private String riskStyle(String riskLevel) {
        return switch (riskLevel) {
            case "HIGH" -> "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 16; -fx-background-color: #ffe3e1; "
                    + "-fx-text-fill: #8f2016; -fx-background-radius: 20;";
            case "MEDIUM" -> "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 16; -fx-background-color: #fff1d0; "
                    + "-fx-text-fill: #956200; -fx-background-radius: 20;";
            case "LOW" -> "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 16; -fx-background-color: #dff7e5; "
                    + "-fx-text-fill: #176b2c; -fx-background-radius: 20;";
            default -> "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 16; -fx-background-color: #e8edf1; "
                    + "-fx-text-fill: #334250; -fx-background-radius: 20;";
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}

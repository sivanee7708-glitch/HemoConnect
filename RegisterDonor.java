
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegisterDonor extends Application {

    private final String FILE_PATH = "Data/BloodDonation.xlsx";

    private TextField nameField;
    private TextField ageField;
    private TextField locationField;
    private TextField contactField;

    private ComboBox<String> bloodGroupBox;
    private ComboBox<String> genderBox;
    private ComboBox<String> availabilityBox;

    private Stage currentStage;

    @Override
    public void start(Stage stage) {

        currentStage = stage;

        Label heading = new Label("🩸 Register Donor");
        heading.setFont(Font.font("Georgia", FontWeight.BOLD, 34));
        heading.setTextFill(Color.web("#800000"));

        nameField = createTextField("Enter donor name");
        ageField = createTextField("Enter age");
        locationField = createTextField("Enter location");
        contactField = createTextField("Enter contact number");

        bloodGroupBox = new ComboBox<>();
        bloodGroupBox.getItems().addAll(
                "A+", "A-", "B+", "B-",
                "AB+", "AB-", "O+", "O-"
        );
        bloodGroupBox.setPromptText("Select blood group");

        genderBox = new ComboBox<>();
        genderBox.getItems().addAll(
                "Male", "Female", "Other"
        );
        genderBox.setPromptText("Select gender");

        availabilityBox = new ComboBox<>();
        availabilityBox.getItems().addAll(
                "Available", "Not Available"
        );
        availabilityBox.setPromptText("Select availability");

        styleComboBox(bloodGroupBox);
        styleComboBox(genderBox);
        styleComboBox(availabilityBox);

        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(25);
        form.setVgap(20);
        form.setPadding(new Insets(25));

        addField(form, 0, "Donor Name", nameField);
        addField(form, 1, "Age", ageField);
        addField(form, 2, "Blood Group", bloodGroupBox);
        addField(form, 3, "Gender", genderBox);
        addField(form, 4, "Location", locationField);
        addField(form, 5, "Contact Number", contactField);
        addField(form, 6, "Availability", availabilityBox);

        Button saveButton = createButton("Save Donor");
        Button clearButton = createButton("Clear");
        Button backButton = createButton("Back");

        saveButton.setOnAction(e -> saveDonor());
        clearButton.setOnAction(e -> clearForm());

        backButton.setOnAction(e -> {
            currentStage.close();
            MainPage.showMainWindow();
        });

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(
                saveButton, clearButton, backButton
        );

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        content.getChildren().addAll(
                heading, form, buttons
        );

        content.setStyle(
                "-fx-background-color: #FFF5F5;"
        );

        Scene scene = new Scene(content, 950, 750);

        stage.setTitle("HemoConnect - Register Donor");
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(700);
        stage.show();
    }

    private TextField createTextField(String prompt) {

        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(320);
        field.setPrefHeight(40);

        field.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #A00000;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 15;"
        );

        return field;
    }

    private void styleComboBox(ComboBox<String> box) {

        box.setPrefWidth(320);
        box.setPrefHeight(40);

        box.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #A00000;" +
                "-fx-font-size: 15;"
        );
    }

    private void addField(
            GridPane grid,
            int row,
            String labelText,
            Control control) {

        Label label = new Label(labelText);

        label.setFont(
                Font.font("Arial", FontWeight.BOLD, 17)
        );

        label.setTextFill(Color.web("#800000"));

        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private Button createButton(String text) {

        Button button = new Button(text);

        button.setPrefWidth(160);
        button.setPrefHeight(45);

        button.setStyle(
                "-fx-background-color: #A00000;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    private void saveDonor() {

        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();

        String bloodGroup = bloodGroupBox.getValue();
        String gender = genderBox.getValue();
        String availability = availabilityBox.getValue();

        if (name.isEmpty() || ageText.isEmpty()
                || location.isEmpty() || contact.isEmpty()
                || bloodGroup == null || gender == null
                || availability == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Missing Information",
                    "Please fill in all donor details."
            );

            return;
        }

        int age;

        try {
            age = Integer.parseInt(ageText);

            if (age < 18 || age > 65) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Age",
                        "Donor age must be between 18 and 65."
                );

                return;
            }

        } catch (NumberFormatException ex) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Age",
                    "Please enter a valid age."
            );

            return;
        }

        if (!contact.matches("[0-9]{10}")) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Invalid Contact",
                    "Please enter a valid 10-digit contact number."
            );

            return;
        }

        try {

            saveToExcel(
                    name,
                    age,
                    bloodGroup,
                    gender,
                    location,
                    contact,
                    availability
            );

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Registration Successful",
                    "Donor information saved successfully!\n"
                    + "Registration date and time recorded."
            );

            clearForm();

        } catch (Exception ex) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Excel Error",
                    "Could not save donor information.\n"
                    + ex.getMessage()
            );
        }
    }

    private void saveToExcel(
            String name,
            int age,
            String bloodGroup,
            String gender,
            String location,
            String contact,
            String availability) throws IOException {

        File file = new File(FILE_PATH);

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Workbook workbook;

        // Open an existing valid workbook.
        // If the file is empty, create a new workbook.
        if (file.exists() && file.length() > 0) {

            try (FileInputStream input =
                         new FileInputStream(file)) {

                workbook = new XSSFWorkbook(input);
            }

        } else {

            workbook = new XSSFWorkbook();
        }

        try (workbook) {

            Sheet sheet = workbook.getSheet("Donors");

            if (sheet == null) {
                sheet = workbook.createSheet("Donors");
            }

            String[] headings = {
                    "Donor Name",
                    "Age",
                    "Blood Group",
                    "Gender",
                    "Location",
                    "Contact Number",
                    "Availability",
                    "Registration Date",
                    "Registration Time"
            };

            Row header = sheet.getRow(0);

            if (header == null) {
                header = sheet.createRow(0);
            }

            // Create or update all nine headings.
            for (int i = 0; i < headings.length; i++) {

                org.apache.poi.ss.usermodel.Cell cell =
                        header.getCell(i);

                if (cell == null) {
                    cell = header.createCell(i);
                }

                cell.setCellValue(headings[i]);
            }

            // Automatically capture registration date and time.
            LocalDateTime now = LocalDateTime.now();

            String registrationDate = now.format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
            );

            String registrationTime = now.format(
                    DateTimeFormatter.ofPattern("hh:mm:ss a")
            );

            int nextRow = Math.max(
                    1, sheet.getLastRowNum() + 1
            );

            Row row = sheet.createRow(nextRow);

            row.createCell(0).setCellValue(name);
            row.createCell(1).setCellValue(age);
            row.createCell(2).setCellValue(bloodGroup);
            row.createCell(3).setCellValue(gender);
            row.createCell(4).setCellValue(location);
            row.createCell(5).setCellValue(contact);
            row.createCell(6).setCellValue(availability);
            row.createCell(7).setCellValue(registrationDate);
            row.createCell(8).setCellValue(registrationTime);

            for (int i = 0; i < headings.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Save workbook.
            try (FileOutputStream output =
                         new FileOutputStream(file)) {

                workbook.write(output);
            }
        }
    }

    private void clearForm() {

        nameField.clear();
        ageField.clear();
        locationField.clear();
        contactField.clear();

        bloodGroupBox.setValue(null);
        genderBox.setValue(null);
        availabilityBox.setValue(null);
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}

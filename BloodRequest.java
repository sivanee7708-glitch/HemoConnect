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

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BloodRequest extends Application {

    private static final String FILE_PATH =
            "Data/BloodDonation.xlsx";

    private TextField requesterField;
    private TextField unitsField;
    private TextField contactField;
    private TextField hospitalField;
    private TextField locationField;

    private ComboBox<String> bloodGroupBox;
    private ListView<Donor> donorList;

    private int savedRow = -1;

    private final Color darkRed =
            Color.web("#800000");

    @Override
    public void start(Stage stage) {

        Label heading = new Label("🩸 BLOOD REQUEST");

        heading.setFont(
                Font.font("Georgia",
                        FontWeight.BOLD, 34)
        );

        heading.setTextFill(darkRed);

        Label instruction = new Label(
                "Find matching registered blood donors"
        );

        instruction.setFont(Font.font("Arial", 16));

        requesterField =
                createField("Enter requester name");

        unitsField =
                createField("Enter units required");

        contactField =
                createField("Enter 10-digit contact");

        hospitalField =
                createField("Enter hospital name");

        locationField =
                createField("Enter city/location");

        bloodGroupBox = new ComboBox<>();

        bloodGroupBox.getItems().addAll(
                "A+", "A-", "B+", "B-",
                "AB+", "AB-", "O+", "O-"
        );

        bloodGroupBox.setPromptText(
                "Select blood group"
        );

        bloodGroupBox.setPrefWidth(300);

        GridPane form = new GridPane();

        form.setAlignment(Pos.CENTER);
        form.setHgap(20);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        addField(form, 0, "Requester Name",
                requesterField);

        addField(form, 1, "Blood Group",
                bloodGroupBox);

        addField(form, 2, "Units Required",
                unitsField);

        addField(form, 3, "Contact Number",
                contactField);

        addField(form, 4, "Hospital Name",
                hospitalField);

        addField(form, 5, "Location",
                locationField);

        Button searchButton =
                createButton("🩸 Search Donors");

        Button submitButton =
                createButton("Submit Request");

        Button availableButton =
                createButton("Available");

        Button unavailableButton =
                createButton("Not Available");

        Button receivedButton =
                createButton("Blood Received: Yes");

        Button notReceivedButton =
                createButton("Blood Received: No");

        Button backButton =
                createButton("Back");

        donorList = new ListView<>();

        donorList.setPrefHeight(150);

        donorList.setMaxWidth(650);

        donorList.setPlaceholder(
                new Label("Search for matching donors")
        );

        Label donorHeading =
                createHeading("Matching Registered Donors");

        Label responseHeading =
                createHeading("Donor Availability Response");

        Label receivedHeading =
                createHeading("Blood Received Status");

        searchButton.setOnAction(e ->
                searchDonors());

        submitButton.setOnAction(e ->
                submitRequest());

        availableButton.setOnAction(e ->
                updateRequestCell(
                        8, "Available",
                        "Donor status saved: Available"
                ));

        unavailableButton.setOnAction(e ->
                updateRequestCell(
                        8, "Not Available",
                        "Donor status saved: Not Available"
                ));

        receivedButton.setOnAction(e ->
                updateRequestCell(
                        9, "Yes",
                        "Blood received status saved."
                ));

        notReceivedButton.setOnAction(e ->
                updateRequestCell(
                        9, "No",
                        "Blood not received status saved."
                ));

        backButton.setOnAction(e -> {

            stage.close();

            MainPage.showMainWindow();
        });

        HBox searchButtons =
                new HBox(15, searchButton, submitButton);

        searchButtons.setAlignment(Pos.CENTER);

        HBox donorButtons =
                new HBox(15,
                        availableButton,
                        unavailableButton);

        donorButtons.setAlignment(Pos.CENTER);

        HBox receivedButtons =
                new HBox(15,
                        receivedButton,
                        notReceivedButton);

        receivedButtons.setAlignment(Pos.CENTER);

        VBox content = new VBox(18);

        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));

        content.getChildren().addAll(
                heading,
                instruction,
                form,
                searchButtons,
                donorHeading,
                donorList,
                responseHeading,
                donorButtons,
                receivedHeading,
                receivedButtons,
                backButton
        );

        content.setStyle(
                "-fx-background-color: #FFF5F5;"
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        Scene scene =
                new Scene(scrollPane, 1000, 850);

        stage.setTitle(
                "HemoConnect - Blood Request"
        );

        stage.setScene(scene);

        stage.setMinWidth(850);
        stage.setMinHeight(700);

        stage.show();
    }

    private TextField createField(String prompt) {

        TextField field = new TextField();

        field.setPromptText(prompt);

        field.setPrefWidth(300);
        field.setPrefHeight(38);

        field.setStyle(
                "-fx-font-size: 15;" +
                "-fx-border-color: #800000;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        return field;
    }

    private Button createButton(String name) {

        Button button = new Button(name);

        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color: #A00000;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    private Label createHeading(String name) {

        Label label = new Label(name);

        label.setFont(
                Font.font("Arial",
                        FontWeight.BOLD, 19)
        );

        label.setTextFill(darkRed);

        return label;
    }

    private void addField(
            GridPane grid,
            int row,
            String name,
            Control control) {

        Label label = new Label(name);

        label.setFont(
                Font.font("Arial",
                        FontWeight.BOLD, 16)
        );

        label.setTextFill(darkRed);

        grid.add(label, 0, row);

        grid.add(control, 1, row);
    }

    private static class Donor {

        String name;
        String bloodGroup;
        String contact;
        String location;
        String availability;

        Donor(String name, String bloodGroup,
              String contact, String location,
              String availability) {

            this.name = name;
            this.bloodGroup = bloodGroup;
            this.contact = contact;
            this.location = location;
            this.availability = availability;
        }

        @Override
        public String toString() {

            return name + " | " + bloodGroup
                    + " | " + location
                    + " | " + contact
                    + " | " + availability;
        }
    }

    // Search donors by blood group and location
    private void searchDonors() {

        String group = bloodGroupBox.getValue();

        String location =
                locationField.getText().trim();

        if (group == null || location.isEmpty()) {

            showMessage(
                    "Select blood group and enter location."
            );

            return;
        }

        donorList.getItems().clear();

        File file = new File(FILE_PATH);

        if (!file.exists()) {

            showMessage(
                    "No donor Excel file found."
            );

            return;
        }

        try (FileInputStream input =
                     new FileInputStream(file);
             Workbook workbook =
                     new XSSFWorkbook(input)) {

            Sheet sheet =
                    workbook.getSheet("Donors");

            if (sheet == null) {

                showMessage("Donors sheet not found.");

                return;
            }

            DataFormatter formatter =
                    new DataFormatter();

            for (int i = 1;
                 i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String name =
                        formatter.formatCellValue(
                                row.getCell(0)).trim();

                String blood =
                        formatter.formatCellValue(
                                row.getCell(2)).trim();

                String donorLocation =
                        formatter.formatCellValue(
                                row.getCell(4)).trim();

                String contact =
                        formatter.formatCellValue(
                                row.getCell(5)).trim();

                String availability =
                        formatter.formatCellValue(
                                row.getCell(6)).trim();

                boolean matchingBlood =
                        blood.equalsIgnoreCase(group);

                boolean matchingLocation =
                        donorLocation.equalsIgnoreCase(location);

                boolean available =
                        availability.equalsIgnoreCase("Available");

                if (matchingBlood
                        && matchingLocation
                        && available) {

                    donorList.getItems().add(
                            new Donor(
                                    name,
                                    blood,
                                    contact,
                                    donorLocation,
                                    availability
                            )
                    );
                }
            }

            if (donorList.getItems().isEmpty()) {

                showMessage(
                        "No available donors found for "
                                + group + " in " + location
                );
            }

        } catch (IOException ex) {

            showMessage(
                    "Excel reading error: "
                            + ex.getMessage()
            );
        }
    }

    // Submit request
    private void submitRequest() {

        String requester =
                requesterField.getText().trim();

        String group = bloodGroupBox.getValue();

        String unitsText =
                unitsField.getText().trim();

        String contact =
                contactField.getText().trim();

        String hospital =
                hospitalField.getText().trim();

        String location =
                locationField.getText().trim();

        if (requester.isEmpty()
                || group == null
                || unitsText.isEmpty()
                || contact.isEmpty()
                || hospital.isEmpty()
                || location.isEmpty()) {

            showMessage(
                    "Please fill all requester details."
            );

            return;
        }

        int units;

        try {

            units = Integer.parseInt(unitsText);

            if (units <= 0) {

                showMessage(
                        "Units must be greater than zero."
                );

                return;
            }

        } catch (NumberFormatException ex) {

            showMessage("Enter valid units.");

            return;
        }

        if (!contact.matches("[0-9]{10}")) {

            showMessage(
                    "Enter a valid 10-digit contact number."
            );

            return;
        }

        Donor selected =
                donorList.getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            showMessage(
                    "Search and select a matching donor first."
            );

            return;
        }

        File file = new File(FILE_PATH);

        if (!file.exists()) {

            showMessage(
                    "Register a donor first."
            );

            return;
        }

        try (FileInputStream input =
                     new FileInputStream(file);
             Workbook workbook =
                     new XSSFWorkbook(input)) {

            Sheet sheet =
                    workbook.getSheet("Requests");

            if (sheet == null) {

                sheet = workbook.createSheet("Requests");
            }

            if (sheet.getPhysicalNumberOfRows() == 0) {

                Row header = sheet.createRow(0);

                String[] headings = {
                        "Requester Name",
                        "Donor Name",
                        "Blood Group",
                        "Units",
                        "Requester Contact",
                        "Donor Contact",
                        "Hospital",
                        "Location",
                        "Donor Status",
                        "Blood Received",
                        "Requested Date Time"
                };

                for (int i = 0;
                     i < headings.length; i++) {

                    header.createCell(i)
                            .setCellValue(headings[i]);
                }
            }

            savedRow = sheet.getLastRowNum() + 1;

            Row row = sheet.createRow(savedRow);

            row.createCell(0).setCellValue(requester);
            row.createCell(1).setCellValue(selected.name);
            row.createCell(2).setCellValue(group);
            row.createCell(3).setCellValue(units);
            row.createCell(4).setCellValue(contact);
            row.createCell(5).setCellValue(selected.contact);
            row.createCell(6).setCellValue(hospital);
            row.createCell(7).setCellValue(location);
            row.createCell(8).setCellValue("Pending");
            row.createCell(9).setCellValue("No");

            String dateTime =
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "dd-MM-yyyy HH:mm:ss"
                            )
                    );

            row.createCell(10).setCellValue(dateTime);

            for (int i = 0; i <= 10; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream output =
                         new FileOutputStream(file)) {

                workbook.write(output);
            }

            showMessage(
                    "Blood request saved successfully!\n"
                            + "Donor response is pending."
            );

        } catch (IOException ex) {

            savedRow = -1;

            showMessage(
                    "Excel saving error: "
                            + ex.getMessage()
            );
        }
    }

    // Update donor response or blood received status
    private void updateRequestCell(
            int column,
            String value,
            String successMessage) {

        if (savedRow < 1) {

            showMessage(
                    "Submit a blood request first."
            );

            return;
        }

        File file = new File(FILE_PATH);

        if (!file.exists()) {

            showMessage("Excel file not found.");

            return;
        }

        try (FileInputStream input =
                     new FileInputStream(file);
             Workbook workbook =
                     new XSSFWorkbook(input)) {

            Sheet sheet =
                    workbook.getSheet("Requests");

            if (sheet == null
                    || sheet.getRow(savedRow) == null) {

                showMessage("Request record not found.");

                return;
            }

            Row row = sheet.getRow(savedRow);

            org.apache.poi.ss.usermodel.Cell cell =
                    row.getCell(column);

            if (cell == null) {
                cell = row.createCell(column);
            }

            cell.setCellValue(value);

            try (FileOutputStream output =
                         new FileOutputStream(file)) {

                workbook.write(output);
            }

            showMessage(successMessage);

        } catch (IOException ex) {

            showMessage(
                    "Excel update error: "
                            + ex.getMessage()
            );
        }
    }

    private void showMessage(String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("HemoConnect");

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}
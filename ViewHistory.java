import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ViewHistory extends Application {

    private static final String FILE_PATH = "Data/BloodDonation.xlsx";
    private static final String ADMIN_PASSWORD = "megha07";

    private final String DARK_RED = "#800000";
    private final String LIGHT_RED = "#FFF0F0";

    private Stage window;
    private TabPane tabPane;
    private Label statusLabel;

    @Override
    public void start(Stage stage) {

        window = stage;

        if (showAdminLogin()) {
            showHistoryPage();
        } else {
            window.close();
        }
    }

    private boolean showAdminLogin() {

        Dialog<String> dialog = new Dialog<>();

        dialog.setTitle("HemoConnect Admin Login");
        dialog.setHeaderText("Administrator Access");

        ButtonType loginButton =
                new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                loginButton, ButtonType.CANCEL
        );

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter admin password");

        VBox content = new VBox(12);
        content.setPadding(new Insets(15));
        content.getChildren().addAll(
                new Label("Enter administrator password:"),
                passwordField
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == loginButton) {
                return passwordField.getText();
            }
            return null;
        });

        String enteredPassword = dialog.showAndWait().orElse(null);

        if (ADMIN_PASSWORD.equals(enteredPassword)) {
            return true;
        }

        if (enteredPassword != null) {
            showAlert(
                    "Access Denied",
                    "Incorrect admin password."
            );
        }

        return false;
    }

    private void showHistoryPage() {

        Label heading = new Label("HemoConnect - View History");
        heading.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + DARK_RED + ";"
        );

        Label subtitle = new Label(
                "Administrator Panel - Donation Records"
        );

        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #555555;"
        );

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(
                TabPane.TabClosingPolicy.UNAVAILABLE
        );

        statusLabel = new Label("Loading records...");
        statusLabel.setStyle("-fx-text-fill: #555555;");

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle(buttonStyle());

        refreshButton.setOnAction(event -> loadExcelData());

        Button backButton = new Button("Back");
        backButton.setStyle(buttonStyle());

        backButton.setOnAction(event -> window.close());

        HBox buttons = new HBox(12, refreshButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(
                15,
                heading,
                subtitle,
                tabPane,
                statusLabel,
                buttons
        );

        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: " + LIGHT_RED + ";");

        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 950, 650);

        window.setTitle("HemoConnect - View History");
        window.setScene(scene);
        window.show();

        loadExcelData();
    }

    private void loadExcelData() {

        tabPane.getTabs().clear();

        File file = new File(FILE_PATH);

        if (!file.exists() || file.length() == 0) {

            statusLabel.setText(
                    "No Excel records found. Register a donor first."
            );

            Label message = new Label(
                    "No records are available yet."
            );

            Tab emptyTab = new Tab("History", message);
            tabPane.getTabs().add(emptyTab);

            return;
        }

        int sheetCount = 0;

        try (FileInputStream input = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(input)) {

            DataFormatter formatter = new DataFormatter();

            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {

                Sheet sheet = workbook.getSheetAt(s);

                if (sheet == null) {
                    continue;
                }

                Tab tab = new Tab(sheet.getSheetName());

                VBox sheetLayout = new VBox(10);
                sheetLayout.setPadding(new Insets(12));

                TableView<Map<String, String>> table =
                        new TableView<>();

                Row headerRow = sheet.getRow(sheet.getFirstRowNum());

                if (headerRow == null ||
                    headerRow.getLastCellNum() <= 0) {

                    tab.setContent(
                            new Label("This sheet has no records.")
                    );

                    tabPane.getTabs().add(tab);
                    sheetCount++;
                    continue;
                }

                int columnCount = headerRow.getLastCellNum();

                List<String> headers = new ArrayList<>();

                for (int c = 0; c < columnCount; c++) {

                    org.apache.poi.ss.usermodel.Cell cell =
                            headerRow.getCell(c);

                    String header = formatter.formatCellValue(cell);

                    if (header == null || header.trim().isEmpty()) {
                        header = "Column " + (c + 1);
                    }

                    headers.add(header);

                    final int columnIndex = c;

                    TableColumn<Map<String, String>, String> column =
                            new TableColumn<>(header);

                    column.setCellValueFactory(data ->
                            new ReadOnlyStringWrapper(
                                    data.getValue().getOrDefault(
                                            headers.get(columnIndex), ""
                                    )
                            )
                    );

                    column.setMinWidth(130);
                    table.getColumns().add(column);
                }

                ObservableList<Map<String, String>> rows =
                        FXCollections.observableArrayList();

                for (int r = sheet.getFirstRowNum() + 1;
                     r <= sheet.getLastRowNum(); r++) {

                    Row excelRow = sheet.getRow(r);

                    if (excelRow == null) {
                        continue;
                    }

                    Map<String, String> rowData =
                            new LinkedHashMap<>();

                    boolean hasData = false;

                    for (int c = 0; c < columnCount; c++) {

                        org.apache.poi.ss.usermodel.Cell cell =
                                excelRow.getCell(c);

                        String value =
                                formatter.formatCellValue(cell);

                        if (value != null && !value.trim().isEmpty()) {
                            hasData = true;
                        }

                        rowData.put(headers.get(c), value);
                    }

                    if (hasData) {
                        rows.add(rowData);
                    }
                }

                table.setItems(rows);
                table.setColumnResizePolicy(
                        TableView.UNCONSTRAINED_RESIZE_POLICY
                );

                Label countLabel = new Label(
                        "Total records: " + rows.size()
                );

                countLabel.setStyle(
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + DARK_RED + ";"
                );

                sheetLayout.getChildren().addAll(
                        countLabel,
                        table
                );

                VBox.setVgrow(table, Priority.ALWAYS);

                tab.setContent(sheetLayout);
                tabPane.getTabs().add(tab);

                sheetCount++;
            }

            statusLabel.setText(
                    "Excel records loaded successfully. Sheets: "
                    + sheetCount
            );

        } catch (Exception e) {

            statusLabel.setText("Could not load Excel records.");

            showAlert(
                    "Excel Error",
                    "Unable to read the Excel file.\n\n"
                    + e.getMessage()
            );
        }
    }

    private String buttonStyle() {

        return
                "-fx-background-color: " + DARK_RED + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 9 22 9 22;" +
                "-fx-background-radius: 8;";
    }

    private void showAlert(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
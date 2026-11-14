import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainPage extends Application {

    private static Stage mainStage;

    private final Color darkRed = Color.web("#800000");

    @Override
    public void start(Stage stage) {

        mainStage = stage;

        StackPane root = new StackPane();

        Canvas background = new Canvas(1500, 850);

        BorderPane layout = new BorderPane();

        // Main heading
        Text title = new Text("HemoConnect");

        title.setFont(Font.font(
                "Georgia",
                FontWeight.BOLD,
                76
        ));

        title.setFill(darkRed);

        // Original caption
        Text caption = new Text(
                "Connecting Donors...Bridging"
        );

        caption.setFont(Font.font(
                "Georgia",
                FontWeight.BOLD,
                34
        ));

        caption.setStyle("-fx-font-style: italic;");
        caption.setFill(Color.web("#900000"));

        // Blood drop logo
        Canvas logo = new Canvas(75, 85);

        drawBloodDrop(
                logo.getGraphicsContext2D(),
                37, 40, 34
        );

        VBox heading = new VBox(12);

        heading.setAlignment(Pos.CENTER);

        heading.getChildren().addAll(
                title,
                createHeartbeat(),
                caption,
                logo
        );

        // Five main buttons
        Button registerButton =
                createButton("♙", "Register Donor");

        Button requestButton =
                createButton("Blood Drop", "Blood Request");

        Button historyButton =
                createButton("◷", "View History");

        Button feedbackButton =
                createButton("✉", "Feedback");

        Button exitButton =
                createButton("⇥", "Exit Application");

        // Navigation
        registerButton.setOnAction(e ->
                openScreen("RegisterDonor"));

        requestButton.setOnAction(e ->
                openScreen("BloodRequest"));

        historyButton.setOnAction(e ->
                openScreen("ViewHistory"));

        feedbackButton.setOnAction(e ->
                openScreen("Feedback"));

        exitButton.setOnAction(e -> stage.close());

        // First row
        HBox firstRow = new HBox(
                22,
                registerButton,
                requestButton,
                historyButton
        );

        firstRow.setAlignment(Pos.CENTER);

        // Second row
        HBox secondRow = new HBox(
                22,
                feedbackButton,
                exitButton
        );

        secondRow.setAlignment(Pos.CENTER);

        VBox buttonLayout = new VBox(
                22,
                firstRow,
                secondRow
        );

        buttonLayout.setAlignment(Pos.CENTER);

        VBox content = new VBox(
                30,
                heading,
                buttonLayout
        );

        content.setAlignment(Pos.CENTER);

        content.setPadding(
                new Insets(35, 20, 70, 20)
        );

        layout.setCenter(content);

        root.getChildren().addAll(
                background,
                layout
        );

        background.widthProperty().bind(
                root.widthProperty()
        );

        background.heightProperty().bind(
                root.heightProperty()
        );

        drawBackground(background);

        background.widthProperty().addListener(
                (obs, oldValue, newValue) ->
                        drawBackground(background)
        );

        background.heightProperty().addListener(
                (obs, oldValue, newValue) ->
                        drawBackground(background)
        );

        Scene scene = new Scene(root, 1500, 850);

        stage.setTitle("HemoConnect");

        stage.setScene(scene);

        stage.setMinWidth(1100);
        stage.setMinHeight(750);

        stage.show();
    }

    // Button creation
    private Button createButton(
            String symbol,
            String name) {

        Button button = new Button();

        VBox box = new VBox(12);

        box.setAlignment(Pos.CENTER);

        if (symbol.equals("Blood Drop")) {

            Canvas drop = new Canvas(45, 55);

            drawBloodDrop(
                    drop.getGraphicsContext2D(),
                    22, 26, 22
            );

            box.getChildren().add(drop);

        } else {

            Text icon = new Text(symbol);

            icon.setFont(Font.font(
                    "Segoe UI Symbol",
                    FontWeight.BOLD,
                    42
            ));

            icon.setFill(darkRed);

            box.getChildren().add(icon);
        }

        Text label = new Text(name);

        label.setFont(Font.font(
                "Arial",
                FontWeight.BOLD,
                20
        ));

        label.setFill(darkRed);

        box.getChildren().add(label);

        button.setGraphic(box);

        button.setPrefWidth(240);
        button.setPrefHeight(140);

        button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #FFF0F0, #F4BDBD);" +
                "-fx-border-color: #800000;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 22;" +
                "-fx-background-radius: 22;" +
                "-fx-effect: dropshadow(gaussian, rgba(100,0,0,0.35), 6, 0.3, 2, 4);" +
                "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #EFAAAA;" +
                        "-fx-border-color: #800000;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 22;" +
                        "-fx-background-radius: 22;" +
                        "-fx-cursor: hand;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #FFF0F0, #F4BDBD);" +
                        "-fx-border-color: #800000;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 22;" +
                        "-fx-background-radius: 22;" +
                        "-fx-effect: dropshadow(gaussian, rgba(100,0,0,0.35), 6, 0.3, 2, 4);" +
                        "-fx-cursor: hand;"
                )
        );

        return button;
    }

    // Actual blood drop symbol
    private void drawBloodDrop(
            GraphicsContext gc,
            double x,
            double y,
            double size) {

        gc.clearRect(0, 0, 75, 85);

        gc.setFill(Color.web("#A00000"));
        gc.setStroke(Color.web("#800000"));
        gc.setLineWidth(2);

        gc.beginPath();

        gc.moveTo(x, y - size);

        gc.bezierCurveTo(
                x - size * 0.4, y - size * 0.2,
                x - size, y + size * 0.3,
                x - size, y + size * 0.55
        );

        gc.bezierCurveTo(
                x - size, y + size * 1.3,
                x + size, y + size * 1.3,
                x + size, y + size * 0.55
        );

        gc.bezierCurveTo(
                x + size, y + size * 0.3,
                x + size * 0.4, y - size * 0.2,
                x, y - size
        );

        gc.closePath();
        gc.fill();
        gc.stroke();

        gc.setStroke(Color.web("#FCE4E4"));
        gc.setLineWidth(3);

        gc.beginPath();

        gc.moveTo(
                x - size * 0.35,
                y + size * 0.25
        );

        gc.quadraticCurveTo(
                x - size * 0.45,
                y + size * 0.55,
                x - size * 0.2,
                y + size * 0.7
        );

        gc.stroke();
    }

    // Heartbeat decoration
    private HBox createHeartbeat() {

        Canvas pulse = new Canvas(300, 45);

        GraphicsContext gc =
                pulse.getGraphicsContext2D();

        gc.setStroke(darkRed);
        gc.setLineWidth(3);

        gc.beginPath();

        gc.moveTo(0, 22);
        gc.lineTo(85, 22);
        gc.lineTo(105, 22);
        gc.lineTo(120, 5);
        gc.lineTo(137, 40);
        gc.lineTo(153, 2);
        gc.lineTo(170, 22);
        gc.lineTo(300, 22);

        gc.stroke();

        HBox result = new HBox(pulse);

        result.setAlignment(Pos.CENTER);

        return result;
    }

    // Background design
    private void drawBackground(Canvas canvas) {

        GraphicsContext gc =
                canvas.getGraphicsContext2D();

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.setFill(Color.web("#FFF5F5"));

        gc.fillRect(0, 0, width, height);

        // Top wave
        gc.setFill(Color.web("#F4BABA"));

        gc.beginPath();

        gc.moveTo(0, 0);
        gc.lineTo(width, 0);
        gc.lineTo(width, 100);

        gc.quadraticCurveTo(
                width / 2, 30, 0, 100
        );

        gc.closePath();
        gc.fill();

        // Decorative blood drops
        drawDecorativeDrop(gc, 100, 230, 24);
        drawDecorativeDrop(gc, width - 100, 230, 24);
        drawDecorativeDrop(gc, 65, 430, 17);
        drawDecorativeDrop(gc, width - 65, 430, 17);

        // Bottom red wave
        gc.setFill(Color.web("#E60012"));

        gc.beginPath();

        gc.moveTo(0, height - 85);

        gc.quadraticCurveTo(
                width * 0.3, height - 150,
                width * 0.5, height - 80
        );

        gc.quadraticCurveTo(
                width * 0.8, height - 20,
                width, height - 85
        );

        gc.lineTo(width, height);
        gc.lineTo(0, height);

        gc.closePath();
        gc.fill();

        gc.setFill(Color.web("#800000"));

        gc.fillRect(
                0,
                height - 15,
                width,
                15
        );
    }

    // Decorative drops
    private void drawDecorativeDrop(
            GraphicsContext gc,
            double x,
            double y,
            double size) {

        gc.setFill(Color.web("#B00000"));

        gc.beginPath();

        gc.moveTo(x, y - size);

        gc.bezierCurveTo(
                x - size, y + size * 0.3,
                x - size, y + size,
                x, y + size
        );

        gc.bezierCurveTo(
                x + size, y + size,
                x + size, y + size * 0.3,
                x, y - size
        );

        gc.closePath();
        gc.fill();
    }

    // Open other screens
    private void openScreen(String className) {

        try {

            Class<?> screenClass =
                    Class.forName(className);

            Application screen =
                    (Application) screenClass
                            .getDeclaredConstructor()
                            .newInstance();

            Stage newStage = new Stage();

            newStage.setOnHidden(event -> {
                mainStage.show();
            });

            screen.start(newStage);

            mainStage.hide();

        } catch (Exception ex) {

            showMessage(
                    "Page Not Ready",
                    "Could not open " + className +
                    ". Please check the Java file.\n\n"
                    + ex.getMessage()
            );
        }
    }

    // Return to main page
    public static void showMainWindow() {

        if (mainStage != null) {
            mainStage.show();
        }
    }

    // Alert messages
    private void showMessage(
            String title,
            String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
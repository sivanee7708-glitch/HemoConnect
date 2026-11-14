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

public class Feedback extends Application {

    private TextArea feedbackArea;
    private int selectedRating = 0;
    private Label ratingMessage;
    private HBox ratingBox;

    private final String DARK_RED = "#800000";
    private final String LIGHT_RED = "#FFF0F0";

    @Override
    public void start(Stage stage) {

        Label heading = new Label("🩸 Share Your Feedback");
        heading.setFont(
            Font.font("Georgia", FontWeight.BOLD, 32)
        );
        heading.setTextFill(Color.web(DARK_RED));

        Label instruction = new Label(
            "Your feedback helps us improve HemoConnect."
        );
        instruction.setFont(Font.font("Arial", 16));

        Label feedbackLabel = new Label("Feedback");
        feedbackLabel.setFont(
            Font.font("Arial", FontWeight.BOLD, 18)
        );
        feedbackLabel.setTextFill(Color.web(DARK_RED));

        feedbackArea = new TextArea();
        feedbackArea.setPromptText(
            "Type your feedback here...\n\n"
            + "Tell us about your experience or any problems you faced."
        );
        feedbackArea.setWrapText(true);
        feedbackArea.setPrefRowCount(6);
        feedbackArea.setMaxWidth(550);
        feedbackArea.setStyle(
            "-fx-font-size: 15;" +
            "-fx-border-color: #800000;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        Label ratingLabel = new Label(
            "Rate Your Experience"
        );
        ratingLabel.setFont(
            Font.font("Arial", FontWeight.BOLD, 19)
        );
        ratingLabel.setTextFill(Color.web(DARK_RED));

        ratingBox = new HBox(12);
        ratingBox.setAlignment(Pos.CENTER);

        ratingMessage = new Label(
            "Select your blood-drop rating"
        );
        ratingMessage.setFont(Font.font("Arial", 15));
        ratingMessage.setTextFill(Color.web(DARK_RED));

        for (int i = 1; i <= 5; i++) {

            final int rating = i;

            Button drop = new Button("🩸");

            drop.setFont(Font.font(29));
            drop.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-cursor: hand;"
            );

            drop.setOnAction(e -> selectRating(rating));

            ratingBox.getChildren().add(drop);
        }

        Button submitButton = createButton("Submit Feedback");
        Button clearButton = createButton("Clear");
        Button backButton = createButton("Back");

        submitButton.setOnAction(e -> submitFeedback());

        clearButton.setOnAction(e -> clearFeedback());

        backButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(18);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(
            submitButton, clearButton, backButton
        );

        VBox root = new VBox(20);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(35));

        root.getChildren().addAll(
            heading,
            instruction,
            feedbackLabel,
            feedbackArea,
            ratingLabel,
            ratingBox,
            ratingMessage,
            buttons
        );

        root.setStyle(
            "-fx-background-color: " + LIGHT_RED + ";"
        );

        Scene scene = new Scene(root, 850, 700);

        stage.setTitle("HemoConnect - Feedback");
        stage.setScene(scene);
        stage.show();
    }

    private void selectRating(int rating) {

        selectedRating = rating;

        String[] messages = {
            "Poor",
            "Fair",
            "Good",
            "Very Good",
            "Excellent"
        };

        ratingMessage.setText(
            "Your Rating: " + rating + "/5 - "
            + messages[rating - 1]
        );

        for (int i = 0; i < 5; i++) {

            Button drop = (Button) ratingBox.getChildren().get(i);

            if (i < rating) {
                drop.setOpacity(1.0);
            } else {
                drop.setOpacity(0.25);
            }
        }
    }

    private void submitFeedback() {

        String feedback = feedbackArea.getText().trim();

        if (feedback.isEmpty()) {

            showMessage(
                Alert.AlertType.WARNING,
                "Missing Feedback",
                "Please type your feedback."
            );

            return;
        }

        if (selectedRating == 0) {

            showMessage(
                Alert.AlertType.WARNING,
                "Missing Rating",
                "Please select a blood-drop rating."
            );

            return;
        }

        showMessage(
            Alert.AlertType.INFORMATION,
            "Thank You!",
            "Your feedback has been submitted successfully!\n"
            + "Rating: " + selectedRating + "/5"
        );

        clearFeedback();
    }

    private void clearFeedback() {

        feedbackArea.clear();
        selectedRating = 0;

        ratingMessage.setText(
            "Select your blood-drop rating"
        );

        for (int i = 0; i < 5; i++) {

            Button drop = (Button) ratingBox.getChildren().get(i);
            drop.setOpacity(1.0);
        }
    }

    private Button createButton(String text) {

        Button button = new Button(text);

        button.setPrefWidth(165);
        button.setPrefHeight(43);

        button.setStyle(
            "-fx-background-color: #800000;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
        );

        return button;
    }

    private void showMessage(
        Alert.AlertType type,
        String title,
        String message
    ) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
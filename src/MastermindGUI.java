import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MastermindGUI extends Application {

    private MastermindGame game;
    private VBox mainLayout;
    private HBox colorButtonsLayout;
    private Label feedbackLabel;
    private Label currentGuessLabel;
    private Label attemptsLabel;
    private List<String> currentGuess;
    private int attempt;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mastermind Game");
        primaryStage.setResizable(true);


        Scene introScene = createIntroScene(primaryStage);
        primaryStage.setScene(introScene);
        primaryStage.setWidth(600);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    private Scene createIntroScene(Stage primaryStage) {
        VBox introLayout = new VBox(20);
        introLayout.setAlignment(Pos.CENTER);
        introLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298); -fx-padding: 20;");

        Label titleLabel = new Label("Welcome to Mastermind");
        titleLabel.setFont(Font.font("Monospaced", 32));
        titleLabel.setTextFill(Color.WHITE);

        Button startButton = new Button("Start");
        startButton.setFont(Font.font("Monospaced", 20));
        startButton.setOnAction(e -> {
            Scene modeSelectionScene = createModeSelectionScene(primaryStage);
            primaryStage.setScene(modeSelectionScene);
        });

        introLayout.getChildren().addAll(titleLabel, startButton);
        Scene scene = new Scene(introLayout, 400, 300);

        introLayout.prefWidthProperty().bind(scene.widthProperty());
        introLayout.prefHeightProperty().bind(scene.heightProperty());

        return scene;
    }

    private Scene createModeSelectionScene(Stage primaryStage) {
        VBox modeLayout = new VBox(10);
        modeLayout.setAlignment(Pos.CENTER);
        modeLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298); -fx-padding: 20;");

        Label modeLabel = new Label("Select Game Mode:");
        modeLabel.setFont(Font.font("Monospaced", 24));
        modeLabel.setTextFill(Color.WHITE);

        Button numbersOnlyButton = new Button("Numbers Only");
        Button colorsOnlyButton = new Button("Colors Only");
        Button mixedButton = new Button("Mixed (Numbers and Colors)");

        numbersOnlyButton.setFont(Font.font("Monospaced", 18));
        colorsOnlyButton.setFont(Font.font("Monospaced", 18));
        mixedButton.setFont(Font.font("Monospaced", 18));

        numbersOnlyButton.setOnAction(e -> startGame(primaryStage, false, false));
        colorsOnlyButton.setOnAction(e -> startGame(primaryStage, true, false));
        mixedButton.setOnAction(e -> startGame(primaryStage, true, true));

        modeLayout.getChildren().addAll(modeLabel, numbersOnlyButton, colorsOnlyButton, mixedButton);

        Scene scene = new Scene(modeLayout, 400, 300);

        modeLayout.prefWidthProperty().bind(scene.widthProperty());
        modeLayout.prefHeightProperty().bind(scene.heightProperty());

        return scene;
    }

    private void startGame(Stage primaryStage, boolean useColors, boolean mixed) {
        game = new MastermindGame(4, 10, useColors, mixed);
        attempt = 0;
        currentGuess = new ArrayList<>();

        mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298); -fx-padding: 20;");

        Label instructions = new Label(useColors ?
                "Click the colors or enter numbers to make a guess:" :
                "Enter your guess (Numbers 1-6):");
        instructions.setFont(Font.font("Monospaced", 20));
        instructions.setTextFill(Color.WHITE);

        currentGuessLabel = new Label("Current Guess: ");
        currentGuessLabel.setFont(Font.font("Monospaced", 20));
        currentGuessLabel.setTextFill(Color.WHITE);

        feedbackLabel = new Label("You have " + game.maxAttempts + " attempts. Good luck!");
        feedbackLabel.setFont(Font.font("Monospaced", 20));
        feedbackLabel.setTextFill(Color.WHITE);

        attemptsLabel = new Label("Attempts Left: " + (game.maxAttempts - attempt));
        attemptsLabel.setFont(Font.font("Monospaced", 20));
        attemptsLabel.setTextFill(Color.WHITE);

        TextField guessInput = new TextField();
        guessInput.setFont(Font.font("Monospaced", 18));
        guessInput.setPromptText(mixed ? "Enter numbers here (optional)" : "Enter your guess here");

        Button submitButton = new Button("Submit");
        submitButton.setFont(Font.font("Monospaced", 18));
        submitButton.setOnAction(e -> {
            if (useColors) {
                if (mixed) {
                    handleMixedGuess(guessInput);
                } else {
                    handleGuess(primaryStage);
                }
            } else {
                handleTextGuess(guessInput, primaryStage);
            }
        });

        Button undoButton = new Button("Undo");
        undoButton.setFont(Font.font("Monospaced", 18));
        undoButton.setOnAction(e -> undoLastGuess());

        if (useColors) {
            colorButtonsLayout = createColorButtons();
            if (mixed) {
                mainLayout.getChildren().addAll(instructions, currentGuessLabel, colorButtonsLayout, guessInput, submitButton, undoButton);
            } else {
                mainLayout.getChildren().addAll(instructions, currentGuessLabel, colorButtonsLayout, submitButton, undoButton);
            }
        } else {
            mainLayout.getChildren().addAll(instructions, guessInput, submitButton, undoButton);
        }

        Button restartButton = new Button("Restart Game");
        restartButton.setFont(Font.font("Monospaced", 18));
        restartButton.setOnAction(e -> startGame(primaryStage, game.useColors, game.mixed));

        Button exitButton = new Button("Exit Game");
        exitButton.setFont(Font.font("Monospaced", 18));
        exitButton.setOnAction(e -> primaryStage.close());

        Button backToDefaultSizeButton = new Button("Default Size");
        backToDefaultSizeButton.setFont(Font.font("Monospaced", 18));
        backToDefaultSizeButton.setOnAction(e -> {
            primaryStage.setWidth(600);
            primaryStage.setHeight(600);
        });

        Button backToSelectModeButton = new Button("Back to Select Mode");
        backToSelectModeButton.setFont(Font.font("Monospaced", 18));
        backToSelectModeButton.setOnAction(e -> primaryStage.setScene(createModeSelectionScene(primaryStage)));

        HBox buttonLayout = new HBox(10, restartButton, exitButton, backToDefaultSizeButton, backToSelectModeButton);
        buttonLayout.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(feedbackLabel, attemptsLabel, buttonLayout);

        Scene gameScene = new Scene(mainLayout, 600, 600);
        mainLayout.prefWidthProperty().bind(gameScene.widthProperty());
        mainLayout.prefHeightProperty().bind(gameScene.heightProperty());

        primaryStage.setScene(gameScene);
    }

    private HBox createColorButtons() {
        HBox colorButtons = new HBox(10);
        colorButtons.setAlignment(Pos.CENTER);

        String[] colors = {"Red", "Green", "Blue", "Yellow", "Purple", "Orange"};
        Color[] javafxColors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE, Color.ORANGE};

        for (int i = 0; i < colors.length; i++) {
            Rectangle colorButton = new Rectangle(40, 40, javafxColors[i]);
            colorButton.setArcWidth(10);
            colorButton.setArcHeight(10);
            colorButton.widthProperty().bind(mainLayout.widthProperty().divide(12));
            colorButton.heightProperty().bind(mainLayout.heightProperty().divide(12));
            int index = i;
            colorButton.setOnMouseClicked(e -> addColorToGuess(colors[index]));

            colorButtons.getChildren().add(colorButton);
        }

        return colorButtons;
    }

    private void addColorToGuess(String color) {
        if (currentGuess.size() < game.secretCode.getCode().length) {
            currentGuess.add(color.substring(0, 1));
            updateCurrentGuessLabel();
        }
    }

    private void updateCurrentGuessLabel() {
        currentGuessLabel.setText("Current Guess: " + String.join(" ", currentGuess));
    }

    private void undoLastGuess() {
        if (!currentGuess.isEmpty()) {
            currentGuess.remove(currentGuess.size() - 1);
            updateCurrentGuessLabel();
        }
    }

    private void handleGuess(Stage primaryStage) {
        if (currentGuess.size() == game.secretCode.getCode().length) {
            int[] feedback = game.checkGuess(game.secretCode.getCode(), currentGuess.toArray(new String[0]));
            feedbackLabel.setText("Correct: " + feedback[0] + ", Close: " + feedback[1]);
            attempt++;
            attemptsLabel.setText("Attempts Left: " + (game.maxAttempts - attempt));

            if (feedback[0] == game.secretCode.getCode().length) {
                feedbackLabel.setText("Congratulations! You've cracked the code!");
                showSecretCode(primaryStage);
                return;
            }

            if (attempt >= game.maxAttempts) {
                feedbackLabel.setText("Game Over! The secret code was: " + String.join(" ", game.secretCode.getCode()));
                showGameOverScreen(primaryStage);
                return;
            }

            currentGuess.clear();
            updateCurrentGuessLabel();
        } else {
            feedbackLabel.setText("Complete your guess!");
        }
    }

    private void handleTextGuess(TextField guessInput, Stage primaryStage) {
        String input = guessInput.getText().trim();
        if (!input.isEmpty()) {

            if (input.length() == 4) {
                try {

                    for (char c : input.toCharArray()) {
                        int number = Character.getNumericValue(c);
                        if (number < 1 || number > 6) {
                            feedbackLabel.setText("Please enter numbers between 1 and 6 only.");
                            return;
                        }
                    }

                    currentGuess.clear();
                    for (char c : input.toCharArray()) {
                        currentGuess.add(String.valueOf(c));
                    }

                    handleGuess(primaryStage);

                } catch (NumberFormatException ex) {
                    feedbackLabel.setText("Invalid input. Enter numbers only.");
                }
            } else {
                feedbackLabel.setText("Please enter exactly 4 digits.");
            }
        }
    }

    private void handleMixedGuess(TextField guessInput) {
        String input = guessInput.getText().trim();
        if (!input.isEmpty()) {
            for (char c : input.toCharArray()) {
                if (Character.isDigit(c) && currentGuess.size() < game.secretCode.getCode().length) {
                    int number = Character.getNumericValue(c);
                    if (number >= 1 && number <= 6) {
                        currentGuess.add(String.valueOf(number));
                    } else {
                        feedbackLabel.setText("Only numbers between 1 and 6 are allowed.");
                        return;
                    }
                }
            }
            guessInput.clear();
            updateCurrentGuessLabel();
        }

        if (currentGuess.size() == game.secretCode.getCode().length) {
            handleGuess(null);
        } else {
            feedbackLabel.setText("Complete your guess!");
        }
    }

    private void showGameOverScreen(Stage primaryStage) {
        VBox gameOverLayout = new VBox(20);
        gameOverLayout.setAlignment(Pos.CENTER);
        gameOverLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298); -fx-padding: 20;");

        Label gameOverLabel = new Label("Game Over! The secret code was: " + String.join(" ", game.secretCode.getCode()));
        gameOverLabel.setFont(Font.font("Monospaced", 28));
        gameOverLabel.setTextFill(Color.WHITE);

        Button restartButton = new Button("Restart Game");
        restartButton.setFont(Font.font("Monospaced", 18));
        restartButton.setOnAction(e -> startGame(primaryStage, game.useColors, game.mixed));

        Button exitButton = new Button("Exit Game");
        exitButton.setFont(Font.font("Monospaced", 18));
        exitButton.setOnAction(e -> primaryStage.close());

        Button backToSelectModeButton = new Button("Back to Select Mode");
        backToSelectModeButton.setFont(Font.font("Monospaced", 18));
        backToSelectModeButton.setOnAction(e -> primaryStage.setScene(createModeSelectionScene(primaryStage)));

        gameOverLayout.getChildren().addAll(gameOverLabel, restartButton, backToSelectModeButton, exitButton);

        Scene gameOverScene = new Scene(gameOverLayout, 600, 600);
        primaryStage.setScene(gameOverScene);
    }

    private void showSecretCode(Stage primaryStage) {
        VBox codeLayout = new VBox(20);
        codeLayout.setAlignment(Pos.CENTER);
        codeLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298); -fx-padding: 20;");

        Label codeLabel = new Label("Congratulations! You've cracked the code: " + String.join(" ", game.secretCode.getCode()));
        codeLabel.setFont(Font.font("Monospaced", 28));
        codeLabel.setTextFill(Color.WHITE);

        Button restartButton = new Button("Restart Game");
        restartButton.setFont(Font.font("Monospaced", 18));
        restartButton.setOnAction(e -> startGame(primaryStage, game.useColors, game.mixed));

        Button exitButton = new Button("Exit Game");
        exitButton.setFont(Font.font("Monospaced", 18));
        exitButton.setOnAction(e -> primaryStage.close());

        Button backToSelectModeButton = new Button("Back to Select Mode");
        backToSelectModeButton.setFont(Font.font("Monospaced", 18));
        backToSelectModeButton.setOnAction(e -> primaryStage.setScene(createModeSelectionScene(primaryStage)));

        codeLayout.getChildren().addAll(codeLabel, restartButton, backToSelectModeButton, exitButton);

        Scene codeScene = new Scene(codeLayout, 600, 600);
        primaryStage.setScene(codeScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
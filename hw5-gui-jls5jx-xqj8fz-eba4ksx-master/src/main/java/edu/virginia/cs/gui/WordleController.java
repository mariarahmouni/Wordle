package edu.virginia.cs.gui;

import edu.virginia.cs.wordle.IllegalWordException;
import edu.virginia.cs.wordle.LetterResult;
import edu.virginia.cs.wordle.Wordle;
import edu.virginia.cs.wordle.WordleImplementation;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;


public class WordleController {
    @FXML
    public Label gameOverText;
    public Wordle wordle;  //= new WordleImplementation();
    @FXML
    public Label errorLabel;
    @FXML
    private Label playAgainText;
    @FXML
    private Button yesAgain;
    @FXML
    private Button noAgain;
    @FXML
    private GridPane gridPane;
    private String guess = "";
    public LetterResult[] letterResult = new LetterResult[5];


    public void initialize() {
        wordle = new WordleImplementation();
        for (int row = 0; row < 6; row++) {
            for (int column = 0; column < 5; column++) {
                ObservableList<Node> gridChildren = gridPane.getChildren();
                gridChildren.forEach(field -> {
                    field.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
                    field.setOnKeyPressed(this::keyEventDirector);
                    field.setOnKeyTyped(this::keyEventDirector);
                    field.setStyle("-fx-display-caret: false;");

                });
            }
        }
        initializeEndOfGameButtons();
    }

    public void initializeEndOfGameButtons() {
        yesAgain.setVisible(false);
        noAgain.setVisible(false);
        noAgain.setDisable(true);
        yesAgain.setDisable(true);
        playAgainText.setText("");
        gameOverText.setText("");
        yesAgain.setOnMouseClicked(this::restartGame);
        noAgain.setOnMouseClicked(this::close);
    }



    protected void keyEventDirector(KeyEvent event) {
        TextField textField = (TextField) event.getSource();
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getCode() == KeyCode.ENTER) {
                handleEnterPressed(textField);
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                handleBackSpacePressed(textField);
            }
        } else if (event.getEventType() == KeyEvent.KEY_TYPED && !event.getCharacter().equals("\r")
                    && !event.getCharacter().equals("\n") && (!event.getCharacter().isEmpty())) {
            if (validateInput(event.getCharacter())) {
                handleLetterInput(event);
            } else {
                textField.clear();
                setErrorInvalidInput();
            }
        }
    }

    @FXML
    private void restartGame(MouseEvent mouseEvent) {
        clearAllFields();
        wordle = new WordleImplementation();
        initializeEndOfGameButtons();

    }

    @FXML
    private void close(MouseEvent mouseEvent) {
        Platform.exit();
    }

    @FXML
    public boolean isGuessEnteredValid(TextField textField) {
        int index;
        try {
            letterResult = wordle.submitGuess(guess);
            index = getIndexOfTextField(textField);
            colorChangeBox(letterResult, index);
            guess = "";
            setRowUneditable(index);
            return true;
        } catch (IllegalWordException e) {
            setErrorInvalidGuess(e.getMessage());
            return false;
        } finally {
            gameStatusDisplay();
        }
    }

    public void setRowUneditable(int indexOfLastInRow) {
        for (int i = indexOfLastInRow - 4; i <= indexOfLastInRow; i++) {
            TextField textField = (TextField) gridPane.getChildren().get(i);
            textField.setDisable(true);
        }
    }

    public void colorChangeBox(LetterResult[] letterResult, int endIndex) {
        int i = 0;
        for (int field = (endIndex - 4); field <= endIndex; field++) {
            if (letterResult[i] == LetterResult.GRAY) {
                gridPane.getChildren().get(field).setStyle("-fx-opacity: 1.0; -fx-text-fill: white; -fx-background-color: #787c7f");
            } else if (letterResult[i] == LetterResult.GREEN) {
                gridPane.getChildren().get(field).setStyle("-fx-opacity: 1.0; -fx-text-fill: white;-fx-background-color: #6ca965");
            } else if (letterResult[i] == LetterResult.YELLOW) {
                gridPane.getChildren().get(field).setStyle("-fx-opacity: 1.0; -fx-text-fill: white;-fx-background-color: #c8b653");
            }
            i++;
        }
    }
    public void handleLetterInput(KeyEvent event) {
        errorLabel.setText("");
        TextField textField = (TextField) event.getSource();
        String input = event.getCharacter();
        if(textField.getLength() > 1){
            textField.setText(deleteLastChar(textField.getText()));
            guess = deleteLastChar(guess);
        }
        if (validateInput(input)) {
            guess += input;
            textField.setText(input.toUpperCase());
            if (!isEndOfRow(textField)) {
                TextField nextTextField = (TextField) gridPane.getChildren().get(getIndexOfTextField(textField) + 1);
                nextTextField.requestFocus();
            }
        } else {
            textField.clear();
            setErrorInvalidInput();
        }
    }
    public void handleEnterPressed(TextField textField) {
        errorLabel.setText("");
        if (!isEndOfRow(textField)) {
            setErrorEarlyEnter();
        } else {
            boolean isValidGuess = isGuessEnteredValid(textField);
            if (getIndexOfTextField(textField) != 29 && isValidGuess) {
                TextField nextTextField = (TextField) gridPane.getChildren().get(getIndexOfTextField(textField) + 1);
                nextTextField.requestFocus();
            } else if (getIndexOfTextField(textField) == 29 && isValidGuess) {
                gameStatusDisplay();
            }
        }
    }

    public void handleBackSpacePressed(TextField textField) {
        errorLabel.setText("");
        if (GridPane.getColumnIndex(textField) != 0) {
            if (textField.getLength() == 0) {
                TextField previousTextField = (TextField) gridPane.getChildren().get(getIndexOfTextField(textField) - 1);
                previousTextField.requestFocus();
                previousTextField.clear();
            }
            textField.clear();
            guess = deleteLastChar(guess);
        }
    }

    // citation https://stackoverflow.com/questions/7438612/how-to-remove-the-last-character-from-a-string
    public String deleteLastChar(String guess) {
        if (!guess.isEmpty()) {
            return guess.substring(0, guess.length() - 1);
        }
        return guess;
    }

    public int getIndexOfTextField(TextField textField) {
        int rowNumber = GridPane.getRowIndex(textField);
        int colNumber = GridPane.getColumnIndex(textField);
        return (rowNumber * 5) + colNumber;
    }

    public void freezeAllFields() {
        for (int i = 0; i <= 29; i++) {
            TextField textField = (TextField) gridPane.getChildren().get(i);
            textField.setDisable(true);
        }
    }
    public void clearAllFields() {
        for (int i = 0; i <= 29; i++) {
            TextField textField = (TextField) gridPane.getChildren().get(i);
            textField.setDisable(false);
            textField.setStyle("-fx-display-caret: false; -fx-opacity: 1.0; -fx-text-fill: black;-fx-background-color: #FFFFFF");
            textField.setText("");
        }
    }
    public boolean validateInput(String input) {
        boolean result;
        try {
            result = Character.isLetter(input.charAt(0));
        } catch (IndexOutOfBoundsException e) {
            return false; // ??
        }
        return result;
    }

    public void gameStatusDisplay() {
        if (wordle.isGameOver()) {
            freezeAllFields();
            if (wordle.isWin()) {
                gameOverText.setText("You won!");
            } else {
                gameOverText.setText("You lost. \n The correct answer was: " + wordle.getAnswer());
            }
            playAgainDisplay();
        }
    }

    public void playAgainDisplay() {
        playAgainText.setText("Play again?");
        noAgain.setVisible(true);
        yesAgain.setVisible(true);
        noAgain.setDisable(false);
        yesAgain.setDisable(false);
    }

    public boolean isEndOfRow(TextField textField) {
        int colNumber = GridPane.getColumnIndex(textField);
        return colNumber == 4;
    }

    public void setErrorInvalidInput() {
        errorLabel.setText("");
        errorLabel.setText("Error: you can only enter letters a-z/A-Z. Try again.");
    }

    public void setErrorEarlyEnter() {
        errorLabel.setText("");
        errorLabel.setText("Error: you cannot submit a guess with less than 5 letters.");
    }

    public void setErrorInvalidGuess(String errorMessage) {
        errorLabel.setText("");
        errorLabel.setText(errorMessage);
    }

}




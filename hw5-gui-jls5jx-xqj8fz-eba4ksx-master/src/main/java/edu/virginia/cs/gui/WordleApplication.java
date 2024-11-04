package edu.virginia.cs.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class WordleApplication extends Application {

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) throws IOException {
        URL view = WordleApplication.class.getResource("wordle-view.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(view);
        Scene scene = new Scene(fxmlLoader.load(), 400,  614+150);
        stage.setTitle("Wordle!");
        stage.setScene(scene);
        stage.show();

    }


}
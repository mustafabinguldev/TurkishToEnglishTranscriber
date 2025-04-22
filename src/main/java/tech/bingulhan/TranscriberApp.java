package tech.bingulhan;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tech.bingulhan.components.ListenerComponent;


public class TranscriberApp extends Application {
    private volatile boolean isListening = false;

    private Thread listeningThread;

    private Text listeningText;
    private Text translatedText;
    private Button listenButton;

    public void load(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        listeningText = new Text("Türkçe: Bekleniyor...");
        listeningText.setFont(Font.font("Arial", 18));
        listeningText.setFill(Color.DARKBLUE);

        translatedText = new Text("İngilizce: Waiting...");
        translatedText.setFont(Font.font("Arial", 18));
        translatedText.setFill(Color.DARKGREEN);

        listenButton = new Button("🔊 Dinlemeyi Başlat");
        listenButton.setFont(Font.font("Arial", 16));
        listenButton.setPrefWidth(220);
        listenButton.setPrefHeight(50);

        listenButton.setOnAction(e -> toggleListening());

        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(listeningText, translatedText, listenButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setTitle("Sesli Çeviri Uygulaması");
        stage.setScene(scene);
        stage.show();
    }

    private void toggleListening() {
        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
        isListening = !isListening;
    }

    private void startListening() {
        listenButton.setText("🛑 Dinlemeyi Durdur");
        listeningText.setText("Türkçe: Dinleniyor...");
        listeningText.setFill(Color.FIREBRICK);

        listeningThread = new Thread(() -> {
            ListenerComponent listenerComponent = new ListenerComponent(listeningText, translatedText);
            listenerComponent.start();
        });
        listeningThread.start();
    }

    private void stopListening() {
        listenButton.setText("🔊 Dinlemeyi Başlat");
        listeningText.setText("Türkçe: Kapalı");
        listeningText.setFill(Color.DARKGRAY);

        if (listeningThread != null && listeningThread.isAlive()) {
            listeningThread.stop();
        }
    }



}

package tech.bingulhan.components;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.scene.text.Text;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import tech.bingulhan.util.TranslateUtil;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ListenerComponent {

    private Text listeningText;
    private Text translatedText;

    public ListenerComponent(Text listeningText, Text translatedText) {

        this.listeningText = listeningText;
        this.translatedText = translatedText;

    }

    public void start() {
        LibVosk.setLogLevel(LogLevel.DEBUG);
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 60000, 16, 2, 4, 44100, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine microphone = null;
        SourceDataLine speakers;
        Platform.runLater(() -> {
            listeningText.setText("Türkçe: Şuan seni dinliyorum!");
        });
        try (Model model = new Model("C:\\TurkishToEnglishTranscriber\\src\\main\\resources\\vosk-model-small-tr-0.3");
             Recognizer recognizer = new Recognizer(model, 120000)) {
            try {
                microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int numBytesRead;
                int CHUNK_SIZE = 1024;
                int bytesRead = 0;
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                speakers.open(format);
                speakers.start();
                byte[] b = new byte[4096];
                while (bytesRead <= 100000000) {
                    numBytesRead = microphone.read(b, 0, CHUNK_SIZE);
                    bytesRead += numBytesRead;
                    out.write(b, 0, numBytesRead);
                    if (recognizer.acceptWaveForm(b, numBytesRead)) {
                        JsonObject object = JsonParser.parseString(recognizer.getResult()).getAsJsonObject();
                        if (!object.get("text").getAsString().equals("")) {

                            String result = object.get("text").getAsString();

                            Platform.runLater(() -> {
                                listeningText.setText("Türkçe: "+result);
                            });

                            Platform.runLater(() -> {
                                translatedText.setText("English: "+".............");
                            });

                            String translatedResult= TranslateUtil.translate("tr", "en", result);

                            Platform.runLater(() -> {
                                translatedText.setText("English: "+translatedResult);
                            });
                        }
                    }
                }
                speakers.drain();
                speakers.close();
                microphone.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (microphone != null && microphone.isOpen()) {
                microphone.stop();
                microphone.close();
            }
        }
    }


}

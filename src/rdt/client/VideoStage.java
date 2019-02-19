package rdt.client;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import rdt.util.Logger;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class VideoStage {

    private File mediaFile;
    private Stage videoStage;
    private double k;
    private MediaView mediaView;
    private String title;

    VideoStage(File mediaFile){
        this.mediaFile = mediaFile;
        videoStage = new Stage();
        title = mediaFile.getName();
    }

    VideoStage(File mediaFile, String name){
        this.mediaFile = mediaFile;
        videoStage = new Stage();
        title = name;
    }

    void play(){

        Slider timeSlider = new Slider();

        Slider volumeSlider = new Slider();

        Button volumeButton = new Button("♫");
        volumeButton.setStyle("-fx-font-size: 2em ;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-background-color: rgba(255, 255, 255, 0)");

        volumeButton.setOnMouseEntered(event -> {
            volumeSlider.setVisible(true);
        });
        volumeButton.setOnAction(event -> {

        });

        Button fullScreenButton = new Button("↕");
        fullScreenButton.setStyle("-fx-font-size: 2em ;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-background-color: rgba(255, 255, 255, 0)");
        fullScreenButton.setRotate(45);

        Button playButton = new Button("| |");
        playButton.setStyle("-fx-font-size: 2em;" +
                "-fx-text-fill: #ffffff;" +
                "-fx-background-color: rgba(255, 255, 255, 0)");

        StackPane toolsStackPane = new StackPane();
        StackPane.setAlignment(fullScreenButton, Pos.CENTER_RIGHT);
        StackPane.setAlignment(volumeButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(volumeSlider, Pos.CENTER_LEFT);
        StackPane.setMargin(volumeSlider, new Insets(0, 0, 0, 40));
        StackPane.setMargin(fullScreenButton, new Insets(0, 15, 0, 0));
        toolsStackPane.getChildren().addAll(playButton, fullScreenButton, volumeButton, volumeSlider);

        toolsStackPane.setOnMouseExited(event -> {
            volumeSlider.setVisible(false);
        });

        Label startTimeLabel = new Label("0.0");
        startTimeLabel.setStyle("-fx-text-fill: white");
        Label endTimeLabel = new Label();
        endTimeLabel.setStyle("-fx-text-fill: white");

        StackPane timeStackPane = new StackPane();
        StackPane.setAlignment(startTimeLabel, Pos.CENTER_LEFT);
        StackPane.setAlignment(endTimeLabel, Pos.CENTER_RIGHT);
        StackPane.setMargin(startTimeLabel, new Insets(0, 0, 0, 10));
        StackPane.setMargin(endTimeLabel, new Insets(0, 10, 0, 0));
        timeStackPane.getChildren().addAll(startTimeLabel, endTimeLabel);

        VBox vBox = new VBox();
        vBox.setVisible(false);
        vBox.setPadding(new Insets(0, 0, 10, 0));
        vBox.getChildren().addAll(timeSlider, timeStackPane, toolsStackPane);

        Media media = new Media(mediaFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView = new MediaView(mediaPlayer);
        mediaPlayer.play();

        mediaPlayer.setOnPaused(() -> {
            playButton.setText(">");
        });

        mediaPlayer.setOnPlaying(() -> {
            playButton.setText("| |");
        });

        fullScreenButton.setOnAction(event -> {
            if (videoStage.isFullScreen())
                videoStage.setFullScreen(false);
            else
                videoStage.setFullScreen(true);
        });

        playButton.setOnAction(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            }
            else{
                mediaPlayer.play();
            }
        });

        volumeButton.setOnAction(event -> {
            if (mediaPlayer.isMute()){
                volumeButton.setText("♫");
                mediaPlayer.setMute(false);
            }
            else{
                volumeButton.setText("♪");
                mediaPlayer.setMute(true);
            }
        });

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #000000");

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {

                double w = mediaPlayer.getMedia().getWidth();
                double h = mediaPlayer.getMedia().getHeight();
                k = w / h;

                double width = 1000;
                double height = width * h / w;

                mediaView.setFitWidth(width);
                StackPane.setAlignment(mediaView, Pos.TOP_CENTER);
                vBox.setAlignment(Pos.BOTTOM_CENTER);
                root.getChildren().addAll(mediaView, vBox);

                videoStage.setMinWidth(width + 15);
                videoStage.setMinHeight(height + 130);
                videoStage.setWidth(videoStage.getMinWidth());
                videoStage.setHeight(videoStage.getMinHeight());
                videoStage.setTitle(title);

                vBox.setMinSize(width, 100);

                timeSlider.setMin(0.0);
                timeSlider.setValue(0.0);
                timeSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());

                volumeSlider.setMin(0.0);
                volumeSlider.setValue(mediaPlayer.getVolume());
                volumeSlider.setMax(1.0);
                volumeSlider.setMaxWidth(100);
                volumeSlider.setVisible(false);

                startTimeLabel.setText(getTimeStr(0));
                endTimeLabel.setText(getTimeStr(mediaPlayer.getTotalDuration().toSeconds()));

                videoStage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                        double stageWidth = videoStage.getWidth();
                        double stageHeight = videoStage.getHeight();
                        if (stageWidth / stageHeight > k) {
                            mediaView.setFitWidth(stageHeight * k);
                        }
                        else {
                            mediaView.setFitWidth(stageWidth);
                        }
                    }
                });

                videoStage.widthProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        changeStageSize();
                    }
                });

                videoStage.heightProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        changeStageSize();
                    }
                });

            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                double currentTime = newValue.toSeconds();
                timeSlider.setValue(currentTime);
                if ((int)oldValue.toSeconds() < (int)newValue.toSeconds()) {
                    startTimeLabel.setText(getTimeStr(currentTime));
                    endTimeLabel.setText(getTimeStr(timeSlider.getMax() - currentTime));
                }
            }
        });

        mediaPlayer.volumeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                volumeSlider.setValue(mediaPlayer.getVolume());
            }
        });

        timeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (timeSlider.isValueChanging() || timeSlider.isPressed()) {
                    mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                }
            }
        });

        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (volumeSlider.isValueChanging() || volumeSlider.isPressed()) {
                    mediaPlayer.setVolume(volumeSlider.getValue());
                }
            }
        });

        Timer[] timer = {new Timer()};

        Scene scene = new Scene(root);

        videoStage.setScene(scene);
        videoStage.show();
        videoStage.setOnCloseRequest(event -> {
            mediaPlayer.stop();
            timer[0].cancel();
            Logger.log("Close videoStage : " + mediaFile.getPath());
        });

        scene.setOnKeyReleased(event -> {
            vBox.setVisible(true);
            timerVBox(vBox, timer);
            if (event.getCode() == KeyCode.SPACE){
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                }
                else{
                    mediaPlayer.play();
                }
            }
        });

        scene.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                int clickCount = event.getClickCount();
                if (clickCount == 1){
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        mediaPlayer.pause();
                    }
                    else{
                        mediaPlayer.play();
                    }
                }
                if (clickCount == 2){
                    if (videoStage.isFullScreen())
                        videoStage.setFullScreen(false);
                    else
                        videoStage.setFullScreen(true);
                }
            }
        });

        scene.setOnMouseMoved(event -> {
            vBox.setVisible(true);
            timerVBox(vBox, timer);
        });

        vBox.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    scene.setCursor(Cursor.DEFAULT);
                }
                else {
                    scene.setCursor(Cursor.NONE);
                }
            }
        });
    }

    private String getTimeStr(double s){
        String result = "";
        result += (int)s/3600;
        s = s % 3600;
        result += ":";
        result += s/60 >= 10 ? (int)s/60 : "0" + (int)s/60;
        s = s % 60;
        result += ":";
        result += s >= 10 ? (int)s : "0" + (int)s;
        return result;
    }

    private void timerVBox(VBox vBox, Timer[] timer){
        if (vBox.isVisible()){
            timer[0].cancel();
            timer[0] = new Timer();
            timer[0].purge();
            timer[0].schedule(new TimerTask() {
                @Override
                public void run() {
                    vBox.setVisible(false);
                }
            }, 3000);

        }


    }

    private void changeStageSize(){
        double stageWidth = videoStage.getWidth() - 20;
        double stageHeight = videoStage.getHeight() - 50;
        if (stageWidth / stageHeight > k) {
            mediaView.setFitWidth(stageHeight * k);
        }
        else {
            mediaView.setFitWidth(stageWidth);
        }
    }

}

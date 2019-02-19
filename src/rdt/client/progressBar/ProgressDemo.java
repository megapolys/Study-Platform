package rdt.client.progressBar;


import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import rdt.client.MainFrame;

import java.util.Collections;

public class ProgressDemo {

    private String title = "Загрузка";
    private BarType barType = BarType.BAR_TYPE;

    public static enum BarType{
        BAR_TYPE,
        INDICATOR_TYPE
    }

    public void setTitle(String title){
        this.title = title;
    }

    public ProgressDemo(){}

    public ProgressDemo(ProgressDemo.BarType barType){
        this.barType = barType;
    }


    public void start(Task<Byte> task, Window primeryStage){

        Task<Integer> task1 = new Task<Integer>() {
            @Override protected Integer call() throws Exception {
                int iterations;
                for (iterations = 0; iterations < 10000; iterations++) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
//                    updateMessage("Iteration " + iterations);
                    updateProgress(iterations, 10000);
                    Thread.sleep(1);
                }
                updateProgress(iterations, 10000);
                return iterations;
            }
        };


        VBox root = new VBox(10);
        root.setStyle("-fx-background-radius: 10;-fx-background-color: rgba(0,50,50,0.1);");
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER_LEFT);

        Button okButton = new Button("ОК");
        okButton.setDisable(true);
        okButton.setDefaultButton(true);
        okButton.setMinWidth(200);

        Label infoLabel = new Label("Прогресс");
        infoLabel.setTextFill(Color.BLUE);
        infoLabel.setWrapText(true);
        infoLabel.textProperty().bind(task.messageProperty());

        switch (barType) {
            case BAR_TYPE:
                ProgressBar progressBar = new ProgressBar(0);
                progressBar.setPrefWidth(primeryStage.getWidth() - 200);
                progressBar.progressProperty().bind(task.progressProperty());

                Label leftTime = new Label("0:00:00");
                Label rightTime = new Label("0:00:00");

                HBox hBox = new HBox(1000);

                root.getChildren().addAll(progressBar, hBox, infoLabel, okButton);
                break;
            case INDICATOR_TYPE:
                ProgressIndicator progressIndicator = new ProgressIndicator(0);
                progressIndicator.progressProperty().bind(task.progressProperty());
                root.getChildren().addAll(progressIndicator);
                break;
        }

        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initOwner(primeryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setWidth(primeryStage.getWidth() - 100);
        stage.setHeight(200);
        stage.show();
        new Thread(task).start();

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
            okButton.setDisable(false);
        });

        task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, event -> {
            okButton.setDisable(false);
        });

        stage.setOnCloseRequest(event -> {
            event.consume();
        });

        okButton.setOnAction(event -> {
            if (task.getValue() == (byte)1){
                stage.close();
//                MainFrame.close();
                MainFrame.setMainScene();
                MainFrame.setMainScene();
            }
            else {
                stage.close();
            }
        });


    }
}

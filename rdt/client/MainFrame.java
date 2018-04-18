package rdt.client;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainFrame extends Application {

    public static void launch(){
        launch(new String[0]);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        setFullScreen(primaryStage);

        FlowPane mainPane = getMainPane();
        Scene mainScene = new Scene(mainPane);



        primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    private void setFullScreen(Stage stage){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }

    private FlowPane getMainPane(){
        FlowPane pane = new FlowPane(10, 50);
        pane.setAlignment(Pos.CENTER);
        pane.setOrientation(Orientation.VERTICAL);

        Label mainLabel = new Label("Выберите предмет");

        Button firstButton = new Button("Тактика");

        pane.getChildren().addAll(mainLabel, firstButton);

        return pane;
    }
}

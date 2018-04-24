package rdt.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import rdt.client.fileSystem.Class;
import rdt.client.fileSystem.FileSystem;
import rdt.client.fileSystem.Subject;

import java.util.ArrayList;

public class MainFrame extends Application {

    private static Stage stage;

    private static Scene mainScene;

    static void launch(){
        launch(new String[0]);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage;

        setScreenSize();

        setMainScene();

        stage.setScene(mainScene);
        stage.show();
        stage.setTitle("JavaFX Application");

    }

    private void setScreenSize(){
//        Screen screen = Screen.getPrimary();
//        Rectangle2D bounds = screen.getVisualBounds();
//
//        stage.setX(bounds.getMinX());
//        stage.setY(bounds.getMinY());
        stage.setWidth(1250.0);
        stage.setHeight(850.0);
        stage.setFullScreenExitHint("");
    }

    private void setMainScene(){

        int countOfSubject = FileSystem.getCountOfSubject();
        Subject[] subjects = FileSystem.getSubjects();

        FlowPane flowPane = new FlowPane(10, 20);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setOrientation(Orientation.VERTICAL);

        VBox vBox = new VBox();
        vBox.setPrefSize(600,300);

        Label mainLabel = new Label("Предметы");
        mainLabel.setFont(new Font(30));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(vBox.getPrefWidth() + 2, vBox.getPrefHeight());
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(vBox);

        Button[] buttons = new Button[countOfSubject];
        for (int i = 0; i < countOfSubject; i++) {
            final int i1 = i;
            buttons[i1] = new Button(subjects[i1].getNameOfSubject());
            buttons[i1].setPrefWidth(vBox.getPrefWidth());
            buttons[i1].setMinHeight(90);
            buttons[i1].setOnAction(event -> showContent(subjects[i1]));
        }

//        Button button = new Button("getSize");
//        button.setOnAction(event -> {
//            System.out.println(stage.getWidth() + "  " + stage.getHeight());
//        });

        vBox.getChildren().addAll(buttons);

        flowPane.getChildren().addAll(mainLabel, scrollPane);

        mainScene = new Scene(flowPane);

    }

    private void showContent(Subject subject){

        String[] nameOfChapter = subject.getNameOfChapter();

        TreeItem<Class> treeItem = getTreeItem(new Class(subject.getNameOfSubject(), new int[0]), subject);
        treeItem.setExpanded(true);

        TreeView<Class> tree = new TreeView<>(treeItem);
        tree.setMinSize(stage.getWidth() - 20, stage.getHeight() - 200);
        tree.setMaxSize(tree.getMaxWidth(), tree.getMinHeight());
        tree.setOnMouseClicked(event -> {
            TreeItem<Class> selected = tree.getSelectionModel().getSelectedItem();
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2 && selected != null){
                    showClassContent(selected.getValue());
                }
            }
        });

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(tree);

        Scene scene = new Scene(flowPane);
        stage.setScene(scene);

    }

    private TreeItem getTreeItem(Class backClass, Subject subject){

        TreeItem<Class> item = new TreeItem<>(backClass);

        ArrayList<Class> underClasses = subject.getUnderClasses(backClass.getClassPath());

        for (Class cl  : underClasses) {
            item.getChildren().add(getTreeItem(cl, subject));
        }

        return item;
    }

    private void showClassContent(Class cl){
        Scene backScene = stage.getScene();

        Button goBackButton = new Button("Go back");
        goBackButton.setOnAction(event -> {
            stage.setScene(backScene);
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll();

        ScrollPane scrollPane = new ScrollPane();

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(goBackButton, scrollPane);
        flowPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(flowPane);
        stage.setScene(scene);
    }

}

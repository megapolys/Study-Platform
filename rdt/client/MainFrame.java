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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import rdt.client.fileSystem.Class;
import rdt.client.fileSystem.File;
import rdt.client.fileSystem.FileSystem;
import rdt.client.fileSystem.Subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainFrame extends Application {

    private static Stage stage;

    private static Scene mainScene;

    static void launch(){
        launch(new String[0]);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FileSystem.init();

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

        Class backClass = new Class(subject.getNameOfSubject(), new int[0]);
        backClass.setParentSubject(subject);
        TreeItem<Class> treeItem = getTreeItem(backClass);
        treeItem.setExpanded(true);

        TreeView<Class> tree = new TreeView<>(treeItem);
        tree.setMinSize(stage.getWidth() - 20, stage.getHeight() - 200);
        tree.setMaxSize(tree.getMaxWidth(), tree.getMinHeight());
        tree.setOnMouseClicked(event -> {
            TreeItem<Class> selected = tree.getSelectionModel().getSelectedItem();
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2 && selected != null){
                    selected.setExpanded(!selected.isExpanded());
                    showClassContent(selected.getValue());
                }
            }
        });

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(tree);

        Scene scene = new Scene(flowPane);
        stage.setScene(scene);

    }

    private TreeItem getTreeItem(Class backClass){

        Subject subject = backClass.getParentSubject();

        TreeItem<Class> item = new TreeItem<>(backClass);

        ArrayList<Class> underClasses = subject.getUnderClasses(backClass.getClassPath());

        for (Class cl  : underClasses) {
            item.getChildren().add(getTreeItem(cl));
        }

        return item;
    }

    private void showClassContent(Class cl){

        Scene backScene = stage.getScene();

        Button goBackButton = new Button("Go back");
        goBackButton.setOnAction(event -> {
            stage.setScene(backScene);
        });

        ArrayList<File> files = cl.getParentSubject().getFiles(cl);
        HashSet<Integer> types = new HashSet<>();
        for (int i = 0; i <files.size(); i++) {
            types.add(files.get(i).getTypeOfFile());
        }
        files = sort(files, types);

        CheckBox[] checkBoxes = new CheckBox[types.size()];
        for (int i = 0; i < types.size(); i++) {
            checkBoxes[i] = new CheckBox();
        }

        ScrollPane scrollPane = createScrollPane(files);

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(goBackButton, scrollPane);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setOrientation(Orientation.VERTICAL);

        Scene scene = new Scene(flowPane);
        stage.setScene(scene);
    }

    private ScrollPane createScrollPane(ArrayList<File> files){

        VBox vBox = new VBox();
        for (int i = 0; i < files.size(); i++) {
            Label label = new Label(files.get(i).getNameOfFile());
            int finalI = i;
            ArrayList<File> finalFiles = files;
            label.setOnMouseClicked(event -> {
                openFile(finalFiles.get(finalI));
            });
            vBox.getChildren().add(label);
        }

        VBox vBox1 = new VBox();
        for (int i = 0; i < files.size(); i++) {
            Label label = new Label(FileSystem.alterType(files.get(i).getTypeOfFile()));
            int finalI = i;
            label.setOnMouseClicked(event -> {

            });
            vBox1.getChildren().add(label);
        }

        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(vBox1, vBox);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(hBox);
        scrollPane.setPrefSize(stage.getWidth() / 1.5, stage.getHeight() / 1.2);
        scrollPane.setMaxSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());

        return scrollPane;
    }

    private ArrayList<File> sort(ArrayList<File> files, HashSet<Integer> types){

        ArrayList<Integer> arrayList = new ArrayList<>(types);
        Collections.sort(arrayList);

        ArrayList<File> res = new ArrayList<>();

        for (Integer i : arrayList) {
            for (int j = 0; j < files.size(); j++) {
                if (i == files.get(j).getTypeOfFile())
                    res.add(files.get(j));
            }
        }
        return res;
    }

    private void openFile(File file){
        System.out.println(file.getNameOfFile());
    }

}

package rdt.client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import rdt.client.fileSystem.Class;
import rdt.client.fileSystem.File;
import rdt.client.fileSystem.FileSystem;
import rdt.client.fileSystem.Subject;
import rdt.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainFrame extends Application {

    private static Stage stage;

    private static Scene mainScene;

    private static ScrollPane staticScrollPane;

    private static ArrayList<Integer> staticTypesSort;

    private static ArrayList<File> staticFiles;
    private static ArrayList<File> staticAllFiles;

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
//        stage.show();
        stage.setTitle("JavaFX Application");
        openFile(new File(1,null,null));

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
        ArrayList<Subject> subjects = FileSystem.getSubjects();

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
            buttons[i1] = new Button(subjects.get(i1).getNameOfSubject());
            buttons[i1].setPrefWidth(vBox.getPrefWidth());
            buttons[i1].setMinHeight(90);
            buttons[i1].setOnAction(event -> showContent(subjects.get(i1)));
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

        Button goBackButton = new Button("Вернуться к предметам");
        goBackButton.setOnAction(event -> {
            stage.setScene(mainScene);
        });

        Class backClass = new Class(subject.getNameOfSubject(), new int[0]);
        backClass.setParentSubject(subject);
        TreeItem<Class> treeItem = getTreeItem(backClass);
        treeItem.setExpanded(true);

        TreeView<Class> tree = new TreeView<>(treeItem);
        tree.setMinSize(stage.getWidth() / 1.2, stage.getHeight() / 1.2);
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

        HBox hBox = new HBox();
        hBox.getChildren().add(goBackButton);
        hBox.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setTop(hBox);
        root.setCenter(tree);

        Scene scene = new Scene(root);
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

        Button goBackButton = new Button("Назад в оглавление");
        goBackButton.setOnAction(event -> {
            stage.setScene(backScene);
        });

        staticAllFiles = cl.getParentSubject().getFiles(cl);
        HashSet<Integer> types = new HashSet<>();
        for (int i = 0; i < staticAllFiles.size(); i++) {
            types.add(staticAllFiles.get(i).getTypeOfFile());
        }
        staticAllFiles = sort(types);
        staticFiles = staticAllFiles;


        staticScrollPane = createScrollPane(staticFiles);

        CheckBox[] checkBoxes = new CheckBox[types.size()];
        staticTypesSort = new ArrayList<>(types);
        Collections.sort(staticTypesSort);
        for (int i = 0; i < types.size(); i++) {
            checkBoxes[i] = new CheckBox(FileSystem.alterType(staticTypesSort.get(i)));
            checkBoxes[i].setSelected(true);
            int finalI = i;
            checkBoxes[i].setOnAction(event -> {
                if (checkBoxes[finalI].isSelected()){    //on
                    types.add(FileSystem.getIntType(checkBoxes[finalI].getText()));
                }
                else {                                   //off
                    types.remove(FileSystem.getIntType(checkBoxes[finalI].getText()));
                }

                staticTypesSort = new ArrayList<>(types);
                Collections.sort(staticTypesSort);
                staticFiles = sort(types);
                staticScrollPane.setContent(createScrollPane(staticFiles).getContent());

            });
        }

        VBox vBox = new VBox();
        vBox.getChildren().addAll(checkBoxes);

        FlowPane flowPane = new FlowPane(10, 40);
        flowPane.getChildren().addAll(vBox, staticScrollPane);
        flowPane.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        hBox.getChildren().add(goBackButton);
        hBox.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setCenter(flowPane);
        root.setTop(hBox);

        Scene scene = new Scene(root);
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
            ArrayList<File> finalFiles = files;
            label.setOnMouseClicked(event -> {
                openFile(finalFiles.get(finalI));
            });
            vBox1.getChildren().add(label);
        }

        HBox hBox = new HBox(20);
        hBox.getChildren().addAll(vBox1, vBox);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(hBox);
        scrollPane.setPrefSize(stage.getWidth() / 1.2, stage.getHeight() / 1.2);
        scrollPane.setMaxSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());

        return scrollPane;
    }

    private ArrayList<File> sort(HashSet<Integer> types){

        ArrayList<Integer> arrayList = new ArrayList<>(types);
        Collections.sort(arrayList);

        ArrayList<File> res = new ArrayList<>();

        for (Integer i : arrayList) {
            for (int j = 0; j < staticAllFiles.size(); j++) {
                if (i == staticAllFiles.get(j).getTypeOfFile())
                    res.add(staticAllFiles.get(j));
            }
        }
        return res;
    }

    private void openFile(File myFile){
        if (myFile.getTypeOfFile() == 1){     //Video

            Stage videoStage = new Stage();

//            java.io.File file = new java.io.File(myFile.getFilePathString());

            java.io.File file = new java.io.File("D:\\videos\\Юмор\\test.mp4");

            Slider slider = new Slider();

            VBox vBox = new VBox();
            vBox.getChildren().add(slider);

            Media media = new Media(file.toURI().toString());
            System.out.println(media.heightProperty()+" "+ media.getWidth());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            MediaView mediaView = new MediaView(mediaPlayer);
//            mediaView.setFitWidth(1500);
//            mediaView.setFitHeight(1000);
            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    int w = mediaPlayer.getMedia().getWidth();
                    int h = mediaPlayer.getMedia().getHeight();

                    videoStage.setMinWidth(w);
                    videoStage.setMinHeight(h);

                    vBox.setMinSize(w, 100);
                    vBox.setTranslateY(h - 100);

                    slider.setMin(0.0);
                    slider.setValue(0.0);
                    slider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                }
            });
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    slider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                }
            });

            Group root = new Group();
            root.getChildren().addAll(mediaView, vBox);

            Scene scene = new Scene(root);

            videoStage.setScene(scene);
            videoStage.show();
            videoStage.setOnCloseRequest(event -> {
                mediaPlayer.stop();
            });
        }
    }

}

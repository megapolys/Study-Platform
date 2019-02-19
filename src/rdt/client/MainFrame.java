package rdt.client;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import rdt.client.fileSystem.HeadClass;
import rdt.client.fileSystem.StudyFile;
import rdt.client.fileSystem.FileSystem;
import rdt.client.fileSystem.Subject;
import rdt.util.FileUtils;
import rdt.util.Logger;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class MainFrame extends Application {

    private static boolean godMode = true;

    private static Stage stage;

    private static Scene mainScene;

    private static PrintStream printStream;

    public static Window getStage(){
        return stage;
    }

    static void launch(){
        launch(new String[0]);
    }

    @Override
    public void init() throws Exception {
        File logFile = new File("log.txt");
        logFile.createNewFile();
        printStream = new PrintStream(logFile);
        Logger.setOutputStream(printStream);
        Logger.setErrorStream(printStream);
        Logger.log("---Start the application program---");
        FileSystem.loadConfig();
    }

    @Override
    public void start(Stage primaryStage){

        

        FileSystem.init();

        stage = primaryStage;

        stage.setMinWidth(1250.0);
        stage.setMinHeight(850.0);
        stage.setFullScreenExitHint("");

        setMainScene();

        stage.show();
        stage.setTitle("JavaFX Application");

        stage.setOnCloseRequest(event -> {
            FileSystem.saveConfig();
            Logger.log("---Stop  the application program---");
            printStream.close();
        });

        stage.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                double width = oldValue.getWidth();
                stage.setWidth(width + 18);
                double height = oldValue.getHeight();
                stage.setHeight(height + 48);
//                Logger.log("width = " + width + "  height = " + height);
            }
        });

//        try {
//            openFile(new StudyFile(1,null,null));
//        } catch (FileNotFoundException e) {
//            Logger.logError(e);
//        }

//        ProgressDemo progressDemo = new ProgressDemo(ProgressDemo.BarType.BAR_TYPE);
//        progressDemo.setTitle("loading");
//        progressDemo.start();

    }

    @Override
    public void stop(){
    }

    public static void close(){
        stage.close();
    }

    public static void setMainScene(){

//        Image image = null;
//        try {
//            image = new Image(new FileInputStream("client_resources\\tank.jpg"));
//        } catch (FileNotFoundException e) {
//            Logger.logError(e);
//        }
//
//        BackgroundImage backgroundImage = new BackgroundImage(
//                image,
//                BackgroundRepeat.SPACE,
//                BackgroundRepeat.SPACE,
//                BackgroundPosition.CENTER,
//                BackgroundSize.DEFAULT
//                );

        int countOfSubject = FileSystem.getCountOfSubject();
        ArrayList<Subject> subjects = FileSystem.getSubjects();

        Button exportButton = new Button("Экспорт");
        Button importButton = new Button("Импорт");

        VBox importVBox = new VBox(20);
        importVBox.setPadding(new Insets(30, 30, 0, 0));
        importVBox.getChildren().addAll(exportButton, importButton);

        FlowPane flowPane = new FlowPane(10, 20);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setOrientation(Orientation.VERTICAL);

        VBox vBox = new VBox();
        vBox.setPrefSize(600,300);

        Label mainLabel = new Label("Учебный дисциплины");
        mainLabel.setFont(new Font(30));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(vBox.getPrefWidth() + 2, vBox.getPrefHeight());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(vBox);

        Button[] buttons = new Button[countOfSubject];

        for (int i = 0; i < countOfSubject; i++) {
            final int i1 = i;
            String nameOfSubject = subjects.get(i1).getNameOfSubject();
            buttons[i1] = buttonCreate(nameOfSubject);
            buttons[i1].setOnAction(event -> showContent(subjects.get(FileSystem.getSubjectIndex(nameOfSubject))));
        }

//        Button button = new Button("getSize");
//        button.setOnAction(event -> {
//            System.out.println(stage.getWidth() + "  " + stage.getHeight());
//        });

        vBox.getChildren().addAll(buttons);

        flowPane.getChildren().addAll(mainLabel, scrollPane);

        BorderPane root = new BorderPane();
        root.setCenter(flowPane);
        root.setRight(importVBox);
//        root.setBackground(new Background(backgroundImage));

        importButton.setOnAction(event -> {
            importStage();
        });

        exportButton.setOnAction(event -> {
            exportStage();
        });

        if (godMode){

            Button createSubButton = new Button("Добавить предмет");
            Button renameSubButton = new Button("Переименовать предмет");
            Button deleteSubButton = new Button("Удалить предмет");
            Button changeSubButton = new Button("Настроить предмет");

            HBox hBox = new HBox(50);
            hBox.setPadding(new Insets(0, 50, 50, 50));
            hBox.getChildren().addAll(
                    createSubButton,
                    renameSubButton,
                    deleteSubButton,
                    changeSubButton
            );

            root.setBottom(hBox);

            createSubButton.setOnAction(event ->{

                Label infoLabel = new Label("");
                TextField nameField = new TextField();
                nameField.setPromptText("Название");
                nameField.setFocusTraversable(false);
                Button addChapterButton = new Button("Добавить уровень иерархии");
                Button delChapterButton = new Button("Удалить уровень иерархии");
                Button createButton = new Button("Создать");
                createButton.setDefaultButton(true);
                ArrayList<TextField> chapters = new ArrayList<>();
                chapters.add(new TextField("Раздел"));

                FlowPane createFlowPane = new FlowPane(20, 20);
                createFlowPane.getChildren().addAll(infoLabel, nameField, createButton, addChapterButton, delChapterButton);
                createFlowPane.setPadding(new Insets(50, 50, 0, 50));

                GridPane createGridPane = new GridPane();
                createGridPane.setHgap(20);
                createGridPane.add(new Label(chapters.size() + ""), 0, 0);
                createGridPane.add(chapters.get(0), 1, 0);
                createGridPane.setPadding(new Insets(50));

                VBox createRoot = new VBox();
                createRoot.getChildren().addAll(createFlowPane, createGridPane);

                Scene createScene = new Scene(createRoot);

                Stage createStage = new Stage();
                createStage.setHeight(500);
                createStage.setWidth(600);
                createStage.setScene(createScene);
                createStage.setTitle("Создание предмета");
                createStage.initOwner(stage);
                createStage.initModality(Modality.WINDOW_MODAL);
                createStage.show();

                createButton.setOnAction(event1 -> {

                    String text = nameField.getText().trim();
                    if (text == null || text.equals("")){
                        infoLabel.setText("Обязательное поле: ");
                        nameField.requestFocus();
                    }
                    else
                        if(FileSystem.isSubjNameExists(text)) {
                            infoLabel.setText("Имя предмета занято :");
                        }
                        else{
                            String[] strChapters = new String[chapters.size()];
                            for (int i = 0; i < chapters.size(); i++) {
                                strChapters[i] = chapters.get(i).getText();
                            }

                            Subject newSubject = new Subject(text, strChapters);

                            Button newButton = buttonCreate(text);
                            newButton.setOnAction(event2 -> showContent(newSubject));
                            vBox.getChildren().add(newButton);
                            FileSystem.addSubject(newSubject);
                            createStage.close();
                        }
                });

                addChapterButton.setOnAction(event1 -> {

                    chapters.add(new TextField("Раздел"));
                    int i = chapters.size();
                    createGridPane.add(new Label(i + ""), 0, i);
                    createGridPane.add(chapters.get(i - 1), 1, i);
                    chapters.get(i - 1).requestFocus();
                    chapters.get(i - 1).selectAll();

                });

                delChapterButton.setOnAction(event1 -> {

                    int i = chapters.size();
                    if (i <= 1) {
                        infoLabel.setText("Достигнуто минимальное кол-во уровней иерархии");
                    }
                    else {
                        chapters.remove(i - 1);
                        createGridPane.getChildren().remove(2*i-2);
                        createGridPane.getChildren().remove(2*i-2);
                        chapters.get(i - 2).requestFocus();
                        chapters.get(i - 2).selectAll();
                    }

                });

            });

            renameSubButton.setOnAction(event -> {

                ChoiceBox<Subject> oldNameChoice = new ChoiceBox<>();
                oldNameChoice.getItems().addAll(FileSystem.getSubjects());

                TextField newName = new TextField();
                Label oldNameLabel = new Label("Старое название предмета :");
                Label newNameLabel = new Label("Новое название предмета :");
                Label infoLabel = new Label("");
                Button renameButton = new Button("Переименовать");
                renameButton.setDefaultButton(true);

                HBox hBox1 = new HBox(20);
                hBox1.getChildren().addAll(oldNameLabel, oldNameChoice);

                HBox hBox2 = new HBox(20);
                hBox2.getChildren().addAll(newNameLabel, newName);

                FlowPane renameRoot = new FlowPane(20, 20);
                renameRoot.getChildren().addAll(hBox1, hBox2, infoLabel, renameButton);
                renameRoot.setPadding(new Insets(50));

                Scene renameScene = new Scene(renameRoot);

                Stage renameStage = new Stage();
                renameStage.setTitle("Переименование предмета");
                renameStage.setScene(renameScene);
                renameStage.initOwner(stage);
                renameStage.initModality(Modality.WINDOW_MODAL);
                renameStage.show();

                renameButton.setOnAction(event1 -> {

                    Subject oldSubject = oldNameChoice.getValue();
                    String newNameText = newName.getText().trim();
                    if (oldSubject == null) {
                        if (newNameText.equals(""))
                            infoLabel.setText("Необходимо заполнить оба поля");
                        else
                            infoLabel.setText("Необходимо заполнить имя изменяемого предмета");
                    }
                    else {
                        if (newNameText.equals(""))
                            infoLabel.setText("Необходимо заполнить поле нового имени");
                        else {
                            if (FileSystem.isSubjNameExists(newNameText))
                                infoLabel.setText("Предмет с данным именем уже существует");
                            else {
                                int index = FileSystem.getSubjectIndex(oldSubject);
                                oldSubject.rename(newNameText);
                                vBox.getChildren().remove(index);
                                Button renButton = buttonCreate(newNameText);
                                renButton.setOnAction(event2 -> showContent(oldSubject));
                                vBox.getChildren().add(index, renButton);
                                renameStage.close();
                            }
                        }
                    }

                });

            });

            deleteSubButton.setOnAction(event -> {

                ChoiceBox<Subject> choiceSubject = new ChoiceBox<>();
                choiceSubject.getItems().addAll(FileSystem.getSubjects());

                Label delLabel = new Label("Название предмета :");
                Label infoLabel = new Label("");
                Button deleteButton = new Button("Удалить");
                deleteButton.setDefaultButton(true);

                HBox hBox1 = new HBox(20);
                hBox1.getChildren().addAll(delLabel, choiceSubject);

                FlowPane delRoot = new FlowPane(20, 20);
                delRoot.getChildren().addAll(hBox1, infoLabel, deleteButton);
                delRoot.setPadding(new Insets(50));

                Scene delScene = new Scene(delRoot);

                Stage delStage = new Stage();
                delStage.setScene(delScene);
                delStage.setTitle("Удаление предмета");
                delStage.initOwner(stage);
                delStage.initModality(Modality.WINDOW_MODAL);
                delStage.show();

                deleteButton.setOnAction(event1 -> {
                    Subject subject = choiceSubject.getValue();
                    if (subject == null){
                        infoLabel.setText("Заполните поле предмета");
                    }
                    else if(deleteDialog("предмет " + subject, "Все содержимое предмета будет удалено")){
                        int index = FileSystem.getSubjectIndex(subject);
                        if(!FileSystem.deleteSubject(index))
                            informationMessage("Невозможно удалить предмет, пока открыты файлы, входящие в него.\n" +
                                    " Закройте файлы и попробуйте снова.");
                        else {
                            vBox.getChildren().remove(index);
                            delStage.close();
                        }
                    }
                });

            });

            changeSubButton.setOnAction(event -> {

                double bottomButtonWidth = 120;
                double textWidth = 200;

                ChoiceBox<Subject> choicer = new ChoiceBox<>();
                choicer.getItems().addAll(FileSystem.getSubjects());
                Button applyButton = new Button("Применить");
                applyButton.setPrefWidth(bottomButtonWidth);
                applyButton.setDisable(true);
                Button okButton = new Button("Ок");
                okButton.setPrefWidth(bottomButtonWidth);
                Button cancelButton = new Button("Отмена");
                cancelButton.setPrefWidth(bottomButtonWidth);

                FlowPane changeFlowPane = new FlowPane(300, 20);
                changeFlowPane.setPadding(new Insets(20, 0, 0, 0));
                HBox changeHBox = new HBox();
                changeHBox.setSpacing(10);
                changeHBox.setAlignment(Pos.CENTER_RIGHT);
                changeHBox.getChildren().addAll(applyButton, cancelButton, okButton);

                BorderPane changeRoot = new BorderPane();
                changeRoot.setPadding(new Insets(50));
                changeRoot.setTop(choicer);
                changeRoot.setCenter(changeFlowPane);
                changeRoot.setBottom(changeHBox);

                Scene changeScene = new Scene(changeRoot);

                Stage changeStage = new Stage();
                changeStage.setScene(changeScene);
                changeStage.initOwner(stage);
                changeStage.setWidth(600);
                changeStage.setHeight(500);
                changeStage.initModality(Modality.WINDOW_MODAL);
                changeStage.setTitle("Настройки");
                changeStage.show();

                cancelButton.setOnAction(event1 -> changeStage.close());
                okButton.setOnAction(event1 -> changeStage.close());

                choicer.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {

                        Button addChapterButton = new Button("Добавить уровень иерархии");
                        Subject subject = choicer.getValue();
                        ArrayList<TextField> chapters = new ArrayList<>();
                        ObservableList<Node> children = changeFlowPane.getChildren();
                        while (children.size() > 0) {
                            children.remove(0);
                        }
                        GridPane changePane = new GridPane();
                        changePane.setHgap(20);
                        changePane.setVgap(10);
                        String[] chaptersString = subject.getNameOfChapter();
                        for (int i = 0; i < chaptersString.length; i++) {
                            TextField textField = new TextField(chaptersString[i]);
                            changePane.add(new Label(i + 1 + ""), 0, i);
                            changePane.add(textField, 1, i);
                            textField.setPrefWidth(textWidth);
                            textField.textProperty().addListener(new InvalidationListener() {
                                @Override
                                public void invalidated(Observable observable) {
                                    applyButton.setDisable(false);
                                }
                            });
                            chapters.add(textField);
                        }

                        children.addAll(addChapterButton, changePane);

                        addChapterButton.setOnAction(event1 -> {
                            applyButton.setDisable(false);
                            TextField textField = new TextField("Раздел");
                            int i = chapters.size();
                            changePane.add(new Label(i + 1 + ""), 0, i);
                            changePane.add(textField, 1, i);
                            textField.setPrefWidth(textWidth);
                            textField.selectAll();
                            textField.requestFocus();
                            chapters.add(textField);
                        });

                        applyButton.setOnAction(event1 -> {
                            saveChanges(subject, chapters);
                            applyButton.setDisable(true);
                        });

                        okButton.setOnAction(event1 -> {
                            if (!applyButton.isDisable())
                                saveChanges(subject, chapters);
                            changeStage.close();
                        });

                    }
                });

            });

        }
        mainScene = new Scene(root);
        stage.setScene(mainScene);

    }

    private static void importStage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбирете файл для загрузки");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null)
            return;
        boolean res = FileUtils.isFilePack(file.getPath());
        if (res){
            FileSystem.importing(file.getPath());
        }
        else{
            informationMessage("Данный файл непригоден");
        }
    }

    private static void exportStage(){

        Label label = new Label("Выбирете папку, в которую будут экспортированы учебные материалы.");
        label.setWrapText(true);
        Label info = new Label("");
        info.setWrapText(true);
        TextField name = new TextField("");
        name.setPromptText("Имя файла");
        name.setMaxWidth(300);
        name.setFocusTraversable(false);
        TextField textField = new TextField("");
        textField.setPromptText("Папка, в которую будет сохранен файл");
        textField.setDisable(true);
        textField.setFocusTraversable(false);
        Button openDir = new Button("Выбрать папку");
        Button export = new Button("Начать экспортирование");

        VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(20));
        vBox.getChildren().setAll(label, textField, name);
        HBox hBox = new HBox(30);
        hBox.getChildren().setAll(info, openDir, export);
        hBox.setPadding(new Insets(20));
        hBox.setAlignment(Pos.CENTER_RIGHT);

        BorderPane root = new BorderPane();
        root.setCenter(vBox);
        root.setBottom(hBox);

        Scene scene = new Scene(root);

        Stage exportStage = new Stage();
        exportStage.initOwner(stage);
        exportStage.initModality(Modality.WINDOW_MODAL);
        exportStage.setScene(scene);
        exportStage.setWidth(800);
        exportStage.setHeight(400);
        exportStage.setResizable(false);
        exportStage.setTitle("Экспорт");
        exportStage.show();

        openDir.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(exportStage);
            if (file == null)
                return;
            textField.setText(file.getPath());
        });

        export.setOnAction(event -> {
            String text = textField.getText();
            String text1 = name.getText();
            if (text.equals("") || text1.equals("")){
                info.setText("Необходимо заполнить все пустые поля!");
            }
            else {
                exportStage.close();
                FileSystem.exporting(text + "\\" + text1.trim());
            }
        });

    }

    private static void saveChanges(Subject subject, ArrayList<TextField> chapters){
        String[] strings = new String[chapters.size()];
        for (int i = 0; i < chapters.size(); i++) {
            strings[i] = chapters.get(i).getText().trim();
        }
        subject.setNameOfChapter(strings);
    }

    private static Button buttonCreate(String name){
        Button button = new Button(name);
        button.setPrefWidth(580);
        button.setMinHeight(90);
        button.setWrapText(true);
        button.setFont(new Font(20));
        return button;
    }

    private static void showContent(Subject subject){

        Button goBackButton = new Button("Вернуться к предметам");
        goBackButton.setOnAction(event -> {
            stage.setScene(mainScene);
        });
        goBackButton.setCancelButton(true);

        HeadClass backClass = new HeadClass(subject.getNameOfSubject(), new int[0], subject);
        TreeItem<HeadClass> treeItem = getTreeItem(backClass);
        treeItem.setExpanded(true);

        TreeView<HeadClass> tree = new TreeView<>(treeItem);
        tree.setMinSize(stage.getWidth() / 1.2, stage.getHeight() / 1.2);
//        tree.setMaxSize(tree.getMaxWidth(), tree.getMinHeight());
        tree.setOnMouseClicked(event -> {
            TreeItem<HeadClass> selected = tree.getSelectionModel().getSelectedItem();
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2 && selected != null){
                    selected.setExpanded(!selected.isExpanded());
                    showClassContent(selected.getValue());
                }
            }
        });

        HBox topHBox = new HBox(20);
        topHBox.getChildren().add(goBackButton);
        topHBox.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setTop(topHBox);
        root.setCenter(tree);

        if (godMode){

            Button createButton = new Button("Создать");
            Button renameButton = new Button("Переименовать");
            Button deleteButton = new Button("Удалить");

            topHBox.getChildren().addAll(createButton, renameButton, deleteButton);

            createButton.setOnAction(event -> {

                TreeItem<HeadClass> selected = tree.getSelectionModel().getSelectedItem();
                if (selected == null)
                    informationMessage("Выбирете раздел!");
                else{
                    int[] classPath = selected.getValue().getPath();
                    if (classPath.length >= subject.getNameOfChapter().length)
                        informationMessage("Добавьте больше уровней иерархии!");
                    else {

                        if (!selected.isExpanded())
                            selected.setExpanded(true);

                        Label className = new Label("Создать в разделе " + selected.getValue().toString());
                        className.setWrapText(true);
                        Label infoLabel = new Label("");

                        ArrayList<HeadClass> underClasses = subject.getUnderClasses(classPath);
                        Label chapterLabel = new Label(subject.getNameOfChapter()[classPath.length] + "№" + (underClasses.size() + 1));
                        TextField nameField = new TextField();
                        Button create = new Button("Создать");
                        create.setDefaultButton(true);

                        HBox createHBox = new HBox(20);
                        createHBox.getChildren().add(className);

                        HBox createHBox1 = new HBox(5);
                        createHBox1.getChildren().addAll(chapterLabel, nameField);

                        HBox createHBox2 = new HBox(20);
                        createHBox2.getChildren().addAll(create, infoLabel);

                        VBox createRoot = new VBox(20);
                        createRoot.getChildren().addAll(createHBox, createHBox1, createHBox2);
                        createRoot.setPadding(new Insets(50));

                        Scene createScene = new Scene(createRoot);

                        Stage createStage = new Stage();
                        createStage.setScene(createScene);
                        createStage.show();
                        createStage.setTitle("Создание раздела");
                        createStage.setAlwaysOnTop(true);

                        create.setOnAction(event1 -> {

                            boolean flag = false;
                            String text = nameField.getText().trim();

                            for (int i = 0; i < underClasses.size(); i++) {
                                if (underClasses.get(i).getName().equals(text)) {
                                    flag = true;
                                    infoLabel.setText("Данное имя занято");
                                }
                            }

                            if (text.trim().equals("")){
                                flag = true;
                                infoLabel.setText("Поле пустое");
                            }

                            if (!flag) {

                                int[] newClassPath = new int[classPath.length + 1];
                                for (int i = 0; i < classPath.length; i++) {
                                    newClassPath[i] = classPath[i];
                                }
                                newClassPath[classPath.length] = underClasses.size();
                                HeadClass newHeadClass = new HeadClass(text, newClassPath, subject);
                                selected.getChildren().add(new TreeItem<>(newHeadClass));
                                subject.addClass(newHeadClass);

                                createStage.close();

                            }
                        });

                    }

                }

            });

            renameButton.setOnAction(event -> {

                TreeItem<HeadClass> selected = tree.getSelectionModel().getSelectedItem();

                if (selected == null)
                    informationMessage("Выбирете раздел!");
                else {
                    if (selected.getValue().getPath().length == 0)
                        informationMessage("Предмет можно переименовывать только в главном меню!");
                    else{

                        Label renameLabel = new Label("Переименовать " + selected.getValue().toString());
                        Label infoLabel = new Label("");
                        TextField newName = new TextField();
                        Button rename = new Button("Переименовать");
                        rename.setDefaultButton(true);

                        HBox renHBox = new HBox(20);
                        renHBox.getChildren().addAll(infoLabel, rename);

                        VBox renameRoot = new VBox(20);
                        renameRoot.setPadding(new Insets(50));
                        renameRoot.getChildren().addAll(renameLabel, newName, renHBox);

                        Scene renScene = new Scene(renameRoot);

                        Stage renStage = new Stage();
                        renStage.setScene(renScene);
                        renStage.show();
                        renStage.setWidth(500);
                        renStage.setAlwaysOnTop(true);

                        rename.setOnAction(event1 -> {

                            if (newName.getText() == null || newName.getText().equals(""))
                                infoLabel.setText("Запольните поле!");
                            else{

                                int[] oldPath = selected.getValue().getPath();
                                int[] classPath = new int[oldPath.length - 1];
                                for (int i = 0; i < classPath.length; i++) {
                                    classPath[i] = oldPath[i];
                                }

                                ArrayList<HeadClass> underClasses = subject.getUnderClasses(classPath);

                                boolean flag = false;
                                String text = newName.getText().trim();
                                for (int i = 0; i < underClasses.size(); i++) {
                                    if (underClasses.get(i).getName().equals(text)) {
                                        infoLabel.setText("Данное имя занято!");
                                        flag = true;
                                    }
                                }

                                if (!flag) {

                                    selected.getValue().rename(text);
                                    if (selected.isExpanded()){
                                        selected.setExpanded(false);
                                        selected.setExpanded(true);
                                    }
                                    else{
                                        selected.setExpanded(true);
                                        selected.setExpanded(false);
                                    }
                                    renStage.close();

                                }
                            }

                        });

                    }
                }

            });

            deleteButton.setOnAction(event -> {

                TreeItem<HeadClass> selected = tree.getSelectionModel().getSelectedItem();

                if (selected == null)
                    informationMessage("Выбирете раздел!");
                else {
                    if (selected.getValue().getPath().length == 0)
                        informationMessage("Предмет можно удалить только в главном меню!");
                    else {

                        if (deleteDialog(selected.getValue().toString(), "Будут удалены все подразделы и их содержимое")){
                            selected.getParent().getChildren().remove(selected);
                            subject.removeClass(selected.getValue());
                        }
//                        Label delLabel = new Label("Вы уверены, что хотите удалить " + selected.getValue().toString() + "?");
//                        delLabel.setPrefHeight(100);
//                        delLabel.setWrapText(true);
//                        Button yesButton = new Button("Да");
//                        yesButton.setPrefWidth(100);
//                        Button noButton = new Button("Нет");
//                        noButton.setPrefWidth(100);
//
//                        HBox delHBox = new HBox(250);
//                        delHBox.getChildren().addAll(yesButton, noButton);
//                        delHBox.setAlignment(Pos.BOTTOM_CENTER);
//
//                        VBox delRoot = new VBox(20);
//                        delRoot.setPadding(new Insets(20));
//                        delRoot.getChildren().addAll(delLabel, delHBox);
//
//                        Scene delScene = new Scene(delRoot);
//
//                        Stage delStage = new Stage();
//                        delStage.setTitle("Удаление");
//                        delStage.initOwner(stage);
//                        delStage.initModality(Modality.WINDOW_MODAL);
//                        delStage.show();
//                        delStage.setScene(delScene);
//                        delStage.setMaxWidth(500);
//                        delStage.setAlwaysOnTop(true);
//                        delStage.setX(stage.getX() + (stage.getWidth() - delStage.getWidth()) / 2);
//
//                        noButton.setOnAction(event1 -> {
//                            delStage.close();
//                        });
//
//                        yesButton.setOnAction(event1 -> {
//
//                            int[] classPath = selected.getValue().getPath();
//                            int i = classPath[classPath.length - 1];
//
//                            selected.getParent().getChildren().remove(selected);
//
//                            subject.removeClass(selected.getValue());
//
//                            delStage.close();
//
//                        });

                    }
                }

            });

        }

        Scene scene = new Scene(root);
        stage.setScene(scene);

    }

    private static TreeItem getTreeItem(HeadClass backClass){

        Subject subject = backClass.getParentSubject();

        TreeItem<HeadClass> item = new TreeItem<>(backClass);

        ArrayList<HeadClass> underClasses = subject.getUnderClasses(backClass.getPath());

        for (HeadClass cl  : underClasses) {
            item.getChildren().add(getTreeItem(cl));
        }

        return item;
    }

    private static void showClassContent(HeadClass headClass){

        Subject subject = headClass.getParentSubject();

        Scene backScene = stage.getScene();

        CheckBox checkBox = new CheckBox("Открывать файлы через программы ПК\n(видео и презентации)");

        Button goBackButton = new Button("Назад в оглавление");
        goBackButton.setOnAction(event -> {
            stage.setScene(backScene);
        });
        goBackButton.setCancelButton(true);

        ArrayList<StudyFile> staticAllFiles = subject.getFiles(headClass);
        List<TableElement> tableElements = new ArrayList<>();

        for (int i = 0; i < staticAllFiles.size(); i++) {
            tableElements.add(new TableElement(staticAllFiles.get(i)));
        }

        ObservableList<TableElement> data = FXCollections.observableArrayList(tableElements);

        TableView<TableElement> tableView = new TableView<>(data);

        TableColumn<TableElement, String> nameColumn = new TableColumn<>("Наименование");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<TableElement, String> extensionColumn = new TableColumn<>("Расширение");
        extensionColumn.setCellValueFactory(new PropertyValueFactory<>("extension"));

        tableView.getColumns().setAll(nameColumn, extensionColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableView.setEditable(true);
        tableView.setOnMouseReleased(event -> {
            int selected = tableView.getSelectionModel().getFocusedIndex();
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2 && tableView.getSelectionModel() != null){
                    try {
                        TableElement tableElement = tableView.getItems().get(selected);
                        openFile(subject.getFile(tableElement.getHexCode()), checkBox.isSelected());
                    } catch (FileNotFoundException e) {
                        Logger.logError(e);
                    }
                }
            }
        });

        HBox hBox = new HBox(20);
        hBox.getChildren().add(goBackButton);
        hBox.setPadding(new Insets(30));

        BorderPane root = new BorderPane();
        root.setCenter(tableView);
        root.setTop(hBox);

        if (godMode){

            nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            nameColumn.setOnEditCommit(event -> {
                String newValue = event.getNewValue().trim();
                String oldValue = event.getOldValue();
                String hexCode = event.getRowValue().getHexCode();
                if (newValue.equals("")){
                    informationMessage("Нельзя оставлять пустое поле имени");
                    (event.getTableView().getItems().get(
                            event.getTablePosition().getRow())
                    ).setName(oldValue);
                }
                else {
                (event.getTableView().getItems().get(
                        event.getTablePosition().getRow())
                ).setName(newValue);
                    subject.renameFile(hexCode, newValue);
                }
            });

            Button addButton = new Button("Добавить");
            Button addFewButton = new Button("Добавить несколько");
            Button deleteButton = new Button("Удалить");

            hBox.getChildren().addAll(addButton, addFewButton, deleteButton);

            addButton.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                File file = fileChooser.showOpenDialog(stage);
                if (file == null)
                    return;
                StudyFile studyFile = subject.addFile(headClass.getPath(), file);
                TableElement tableElement = new TableElement(studyFile);
                data.add(tableElement);
            });

            addFewButton.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                List<File> files = fileChooser.showOpenMultipleDialog(stage);
                if (files == null)
                    return;
                for (int i = 0; i < files.size(); i++) {
                    StudyFile studyFile = subject.addFile(headClass.getPath(), files.get(i));
                    TableElement tableElement = new TableElement(studyFile);
                    data.add(tableElement);
                }

            });

            deleteButton.setOnAction(event -> {
                if (deleteDialog("данный файл")) {
                    int selected = tableView.getSelectionModel().getFocusedIndex();
                    if (subject.deleteFile(data.get(selected).getHexCode())) {
                        data.remove(selected);
                    }
                }
            });

        }

        checkBox.setWrapText(true);
        hBox.getChildren().add(checkBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    public static class TableElement{
        private SimpleStringProperty name;
        private SimpleStringProperty extension;
        private SimpleStringProperty hexCode;

        TableElement(StudyFile studyFile){
            name = new SimpleStringProperty(studyFile.getName());
            extension = new SimpleStringProperty(studyFile.getType());
            hexCode = new SimpleStringProperty(studyFile.getHexCode());
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name1){
            name = new SimpleStringProperty(name1);
        }

        public String getExtension() {
            return extension.get();
        }

        public String getHexCode() {
            return hexCode.get();
        }

        public String getFullName(){
            return getHexCode() + "." + getExtension();
        }
    }

    private static void openFile(StudyFile studyFile, boolean openSystem) throws FileNotFoundException {

        String path = studyFile.getGlobalPath();
        String type = studyFile.getType();
        String name = studyFile.getName();
        String hexCode = studyFile.getHexCode();
        Logger.log("Open file : " + path);

        if ((type.equals("mp4") ||
                type.equals("m4a") ||
                type.equals("m4v") ||
                type.equals("wav") ||
                type.equals("aif") ||
                type.equals("aiff")) && !openSystem){
            File file = new File(path);

            VideoStage videoStage = new VideoStage(file, name);
            videoStage.play();
        }
        else if ((type.equals("ppt") || type.equals("pptx")) && !openSystem){
            PresentationStage pres = new PresentationStage(path, hexCode, name);
        }
        else {
            File file = new File(path);
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                Logger.logError(e);
            }
        }

    }

    private static void informationMessage(String mes){

//        Label mesLabel = new Label(mes);
//        Button button = new Button("Ok");
//        button.setDefaultButton(true);
////        button.setStyle("-fx-border-color: red");
//
//        VBox root = new VBox(20);
//        root.setAlignment(Pos.CENTER);
//        root.setPadding(new Insets(30));
//        root.getChildren().addAll(mesLabel, button);
//
//        Scene scene = new Scene(root);
//
//        Stage errorStage = new Stage();
//        errorStage.setScene(scene);
//        errorStage.show();
//        errorStage.setTitle("Внимание!");
//
//        button.setOnAction(event -> {
//            errorStage.close();
//        });


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание!");
        alert.setHeaderText(mes);
        alert.showAndWait();

    }

    private static boolean deleteDialog(String headerText) {
        return deleteDialog(headerText, "");
    }

    private static boolean deleteDialog(String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Вы уверены, что хотите удалить " + headerText);
        alert.setContentText(contentText);
        alert.setTitle("Вопрос");
        ButtonType yesButtonType = new ButtonType("Да", ButtonBar.ButtonData.YES);
        ButtonType noButtonType = new ButtonType("Нет", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButtonType, noButtonType);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesButtonType)
            return true;
        return false;
    }

//    private ScrollPane createScrollPane(ArrayList<StudyFile> files){
//
//        VBox vBox = new VBox();
//        for (int i = 0; i < files.size(); i++) {
//            Label label = new Label(files.get(i).getName());
//            int finalI = i;
//            ArrayList<StudyFile> finalFiles = files;
//            label.setOnMouseClicked(event -> {
//                try {
//                    openFile(finalFiles.get(finalI));
//                } catch (FileNotFoundException e) {
//                    Logger.logError(e);
//                }
//            });
//            vBox.getChildren().add(label);
//        }
//
//        VBox vBox1 = new VBox();
//        for (int i = 0; i < files.size(); i++) {
//            Label label = new Label(files.get(i).getType());
//            int finalI = i;
//            ArrayList<StudyFile> finalFiles = files;
//            label.setOnMouseClicked(event -> {
//                try {
//                    openFile(finalFiles.get(finalI));
//                } catch (FileNotFoundException e) {
//                    Logger.logError(e);
//                }
//            });
//            vBox1.getChildren().add(label);
//        }
//
//        HBox hBox = new HBox(20);
//        hBox.getChildren().addAll(vBox1, vBox);
//
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setContent(hBox);
//        scrollPane.setPrefSize(stage.getWidth() / 1.2, stage.getHeight() / 1.2);
//        scrollPane.setMaxSize(scrollPane.getPrefWidth(), scrollPane.getPrefHeight());
//
//        return scrollPane;
//    }

}

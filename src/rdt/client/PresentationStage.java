package rdt.client;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import rdt.client.fileSystem.FileSystem;
import rdt.util.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PresentationStage extends Stage {

    private String filePath;
    private String outputFolder;
    private String title;
    private BorderPane root;
    private Scene scene;
    private Image[] images;
    private ImageView imageView;
    private int slideNum;
    private double k;
    private boolean exists;
    private ContextMenu contextMenu;


    class Ppt2Img extends Task {
        @Override
        protected Object call() throws Exception {
            ppt2img();
            return null;
        }

        private BufferedImage[] ppt2img() {

            try {

                File pptFile = new File(filePath);
                XMLSlideShow slideShow = new XMLSlideShow(new FileInputStream(pptFile));

                Dimension slideSize = slideShow.getPageSize();
                XSLFSlide[] slides = slideShow.getSlides().toArray(new XSLFSlide[0]);

                BufferedImage[] result = new BufferedImage[slides.length];
                for (int i = 0; i < result.length; i++) {

                    this.updateProgress(i + 1, result.length);

                    result[i] = new BufferedImage(slideSize.width, slideSize.height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D imgGraphics = result[i].createGraphics();

                    imgGraphics.setPaint(Color.white);
                    imgGraphics.fill(new Rectangle2D.Float(0, 0, slideSize.width, slideSize.height));

                    slides[i].draw(imgGraphics);

                    if (outputFolder != null) {
                        File imgFile = new File(outputFolder + "/slide_" + i + ".png");
                        if (!imgFile.exists())
                            imgFile.createNewFile();

                        ImageIO.write(result[i], "PNG", imgFile);
                    }

                }

                slideShow.close();

                return result;

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }

            return null;

        }

    }

    public PresentationStage(String filePath, String outputFolder, String title) {
        super();
        File file = new File(outputFolder);
        if (file.exists()){
            exists = true;
        }
        else{
            file.mkdir();
            exists = false;
        }
        this.filePath = filePath;
        this.outputFolder = outputFolder;
        this.title = title;
        loading();
    }

    private void loading(){


        ProgressIndicator progressIndicator = new ProgressIndicator();

        root = new BorderPane();
        root.setCenter(progressIndicator);

        scene = new Scene(root);
        this.setScene(scene);
        this.setTitle(title);
        this.setMinWidth(450);
        this.setMinHeight(300);
        this.show();

        if (exists){
            main();
        }
        else{
            Ppt2Img task = new Ppt2Img();
            progressIndicator.progressProperty().bind(task.progressProperty());
            task.setOnSucceeded(event -> main());
            task.setOnFailed(event -> root.setCenter(new Label("Что то пошло не так...")));

            new Thread(task).start();
        }


    }

    private void main(){
        listeners();

        File[] files = new File(outputFolder).listFiles();
        images = new Image[files.length];
        for (int i = 0; i < files.length; i++) {
            images[i] = new Image(files[i].toURI().toString());
        }
        double width = images[0].getWidth();
        double height = images[0].getHeight();
        imageView = new ImageView(images[0]);
        k = width / height;
        this.setMinHeight(height + 50);
        this.setMinWidth(width + 20);

        slideNum = 0;
        root.setCenter(imageView);

        MenuItem nextSlide = new MenuItem("Следующий слайд");
        nextSlide.setOnAction(event -> nextSlide());
        MenuItem lastSlide = new MenuItem("Предыдущий слайд");
        lastSlide.setOnAction(event -> lastSlide());
        Menu chooseSlide = new Menu("Перейти на слайд");

        MenuItem[] slides = new MenuItem[images.length];
        for (int i = 0; i < images.length; i++) {
            slides[i] = new MenuItem("Слайд " + (i + 1));
            int finalI = i;
            slides[i].setOnAction(event -> setSlide(finalI));
            chooseSlide.getItems().add(slides[i]);
        }

        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(nextSlide, lastSlide, chooseSlide);

        scene.setOnContextMenuRequested(event -> contextMenu.show(root, event.getScreenX(), event.getScreenY()));

    }

    private void nextSlide(){
        if (slideNum < images.length - 1){
            slideNum++;
            imageView.setImage(images[slideNum]);
        }
    }

    private void lastSlide(){
        if (slideNum > 0){
            slideNum--;
            imageView.setImage(images[slideNum]);
        }
    }

    private void setSlide(int i){
        if (i < images.length && i >= 0){
            slideNum = i;
            imageView.setImage(images[slideNum]);
        }
    }

    private void listeners(){

        this.setOnCloseRequest(event -> {
            File file = new File(outputFolder);
            FileSystem.deleteFolder(file);
            Logger.log("Close PresentationStage : " + filePath);
        });

        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            switch (code){
                case SPACE:
                    nextSlide();
                    break;
                case RIGHT:
                    nextSlide();
                    break;
                case DOWN:
                    nextSlide();
                    break;
                case UP:
                    lastSlide();
                    break;
                case LEFT:
                    lastSlide();
                    break;
            }
        });

        scene.setOnMouseReleased(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)){
                contextMenu.hide();
                int clickCount = event.getClickCount();
                if (clickCount == 2){
                    if (this.isFullScreen())
                        this.setFullScreen(false);
                    else
                        this.setFullScreen(true);
                }
            }
        });

        this.widthProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                changeStageSize();
            }
        });

        this.heightProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                changeStageSize();
            }
        });

    }

    private void changeStageSize(){
        double stageWidth = this.getWidth() - 20;
        double stageHeight = this.getHeight() - 50;
        if (stageWidth / stageHeight > k) {
            imageView.setFitWidth(stageHeight * k);
            imageView.setFitHeight(stageHeight);
        }
        else {
            imageView.setFitWidth(stageWidth);
            imageView.setFitHeight(stageWidth / k);
        }
    }


}

package rdt.client.progressBar;

import javafx.concurrent.Task;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UnpackPPT extends Task {

    private String filePath;
    private String outputFolder;

    public UnpackPPT(String filePath, String outputFolder){
        this.filePath = filePath;
        this.outputFolder = outputFolder;
    }

    @Override
    protected Object call() throws Exception {

        try {

            File pptFile = new File(filePath);
            XMLSlideShow slideShow = new XMLSlideShow(new FileInputStream(pptFile));

            Dimension slideSize = slideShow.getPageSize();
            XSLFSlide[] slides = slideShow.getSlides().toArray(new XSLFSlide[0]);

            BufferedImage[] result = new BufferedImage[slides.length];
            for (int i = 0; i < result.length; i++) {

                result[i] = new BufferedImage(slideSize.width, slideSize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D imgGraphics = result[i].createGraphics();

                imgGraphics.setPaint(Color.white);
                imgGraphics.fill(new Rectangle2D.Float(0, 0, slideSize.width, slideSize.height));

                slides[i].draw(imgGraphics);

                if (outputFolder != null) {
                    File imgFile = new File(outputFolder + "/slide_" + i + ".png");
                    this.updateMessage(imgFile.getName());
                    if (!imgFile.exists())
                        imgFile.createNewFile();

                    ImageIO.write(result[i], "PNG", imgFile);
                    this.updateProgress(i + 1, result.length);
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

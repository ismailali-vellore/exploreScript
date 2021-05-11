import com.assertthat.selenium_shutterbug.core.Shutterbug;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class SVGChart {
    private  WebDriver driver = null;
    private String chartColor = "#FFFFFF";
    private WebElement svgChart = null;
    private WebElement chartPortion = null;
    private int x = 0;
    private  int y = 0;
    public  SVGChart(WebDriver driver, WebElement svgChartElement) {
        this.driver = driver;
        this.svgChart = svgChartElement;
    }

    public  SVGChart findByColor(String chartColor) {
        this.chartColor = chartColor;
        List<WebElement> webElements = this.svgChart.findElements(By.tagName("path"));
        Optional<WebElement> chartPortion =  webElements.stream().filter(a-> a.getAttribute("fill").equalsIgnoreCase(this.chartColor)).findFirst();
        if (chartPortion.isPresent())
            this.chartPortion = chartPortion.get();

        BufferedImage capturedImage = Shutterbug.shootElement(driver, this.chartPortion, true).getImage();
        if (chartColor.replace("#", "").length()==4)
            chartColor = "#FFFFFF";
        Color expColor = Color.decode(chartColor);
        int colFound = 0;
        int rowFound = 0;

        for (int row=0; row<this.chartPortion.getRect().getWidth(); row++ ) {
            boolean matchFound = false;
            for (int col=0; col<this.chartPortion.getRect().getHeight(); col++){
                int rgb = capturedImage.getRGB(row, col);
                Color color = new Color(rgb, true);
                if (color.getRGB()==expColor.getRGB()) {
                    colFound++;
                } else {
                    colFound=0;
                }
                if (colFound>5) {
                    matchFound = true;
                    this.y = col;
                    break;
                }
            }
            if (matchFound){
                rowFound++;
            } else {
                rowFound=0;
            }
            if (rowFound>5) {
                this.x = row;
                break;
            }
        }
        return  this;
    }

    public void mouseOver() {
        Actions action = new Actions(this.driver);
        action.moveToElement(this.chartPortion, this.x, this.y).perform();
    }

    public void mouseClick() {
        Actions action = new Actions(this.driver);
        action.moveByOffset(this.x, this.y);
        action.click(this.chartPortion).perform();
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Projects\\Workspace\\src\\test\\resources\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://cdpn.io/githiro/fullpage/ICfFE");
        Point p = driver.findElement(By.id("result")).getLocation();
        driver.switchTo().frame("result");
        WebElement chart = driver.findElement(By.id("doughnutChart"));
        String usingColor = "#2C3E50";
        Thread.sleep(1000);
        SVGChart obj = new SVGChart(driver, chart);
        obj.findByColor(usingColor).mouseOver();
        driver.close();

    }


}

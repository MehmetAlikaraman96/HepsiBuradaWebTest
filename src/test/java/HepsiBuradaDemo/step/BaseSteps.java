package HepsiBuradaDemo.step;

import HepsiBuradaDemo.base.BaseTest;
import HepsiBuradaDemo.helper.ElementHelper;
import HepsiBuradaDemo.helper.StoreHelper;
import HepsiBuradaDemo.model.ElementInfo;
import com.thoughtworks.gauge.Step;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseSteps extends BaseTest {

    public static int DEFAULT_MAX_ITERATION_COUNT = 150;
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;

    private static Log4jLoggerAdapter logger = (Log4jLoggerAdapter) LoggerFactory
            .getLogger(BaseSteps.class);

    private static String SAVED_ATTRIBUTE;

    private Actions actions = new Actions(driver);

    public BaseSteps(){

        PropertyConfigurator
                .configure(BaseSteps.class.getClassLoader().getResource("log4j.properties"));
    }

    private WebElement findElement(String key){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        WebDriverWait webDriverWait = new WebDriverWait(driver,30 ,1000);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    private List<WebElement> findElements(String key){
        ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
        By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
        return driver.findElements(infoParam);
    }
  private void createFileProducts(){
        File file = new File("csv/products.csv");
      try {
          if (file.createNewFile()){
              System.out.println("Dosya oluşturuldu");
          }else{
              System.out.println("Dosya zaten Mevcut");
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
    private void _clickElement(WebElement element){
        element.click();
    }
    private void _hoverElement(WebElement element){
        actions.moveToElement(element).build().perform();

    }
    private void _sendKeys(WebElement element,String text){
        element.sendKeys(text);
    }
    public void writeToCsv(String filePath, String productName, String price) {
        try {
            File myObj = new File(filePath);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(productName + " - " + price);
            writer.write("\n");
            writer.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

   @Step("<key> elementine tıkla")
    public void clickElement(String key){
        WebElement element=findElement(key);
        element.click();
   }
   @Step("<key> elementine focus ol")
   public void focusElement(String key){
        WebElement element=findElement(key);
        _hoverElement(element);
   }
    @Step("<key> elementine focus ile tıkla")
    public void clickElementWithFocus(String key) throws InterruptedException {
        WebElement element=findElement(key);
        _hoverElement(element);
        element.click();
    }
   @Step("<key> elementine <text> degerini gönder")
    public void sendKeys(String key,String text){
        WebElement element=findElement(key);
        element.sendKeys(text);
   }
    @Step({"Wait <value> seconds",
            "<int> saniye bekle"})
    public void waitBySeconds(int seconds) throws InterruptedException {

            logger.info(seconds + " saniye bekleniyor.");
            Thread.sleep(seconds * 1000);
        }

    @Step("<key> elementi içerisinde <text> değeri var mı kontrol edilir")
    public void controlLogin(String key,String text) throws InterruptedException {
        try {
            Thread.sleep(7000);
            WebElement element=findElement(key);
            Thread.sleep(2000);
            assertThat(element.getText()).contains(text);
        }
        catch (Exception ex)
        {
            logger.info("Element doğrulanmadı");
            throw ex;
        }

    }

        @Step("<key>elementini liste ile al ve listeden bir elemente random ile tıkla")
    public void elementGetList(String key){
            Random random=new Random();
            List<WebElement>Categories=findElements(key);
            int rastgeleSayi = random.nextInt(Categories.size());
            WebElement element = Categories.get(rastgeleSayi);
            element.click();
        }
        @Step("Urunler ıcın csv dosyası olustur")
        public void createCsv(){
        createFileProducts();
        }
    @Step("<key1>elementini liste ile al ve listeden bir elemente random ile tıkla daha sonra <key2>ve<key3> bunu csv dosyasına kaydet")
    public void elementGetListandWriteCsv(String key1, String key2, String key3){
        Random random=new Random();
        List<WebElement>Products=findElements(key1);
        List<WebElement>ProductName=findElements(key2);
        List<WebElement>ProductPrice=findElements(key3);
        int rastgeleSayi = random.nextInt(Products.size());
        WebElement product = Products.get(rastgeleSayi);
        WebElement productName = ProductName.get(rastgeleSayi);
        WebElement productPrice = ProductPrice.get(rastgeleSayi);
        String _productName = productName.getText();
        String _productPrice = productPrice.getText();
        writeToCsv("products.csv",_productName,_productPrice);
        System.out.println(productName.getText()+" "+ productPrice.getText());
        product.click();
    }
    @Step("<key> elementinin fiyat bilgileri karsılastırılır")
    public void controlProductPrice(String key){
        WebElement element=findElement(key);
        String elementPrice=element.getText();
        int i = Integer.parseInt(elementPrice);
    }
    @Step("<key> elementinin adet sayısı <text> degerini içeriyor mu kontrol edilir")
    public void getText(String key,String text){
        WebElement element= findElement(key);
        String getText=element.getText();
        Assert.assertEquals("Adet sayısı tutmuyor",text,getText);
        System.out.println(getText);

    }
    @Step(" <key> elementinin urun Bilgileri eşleşiyor mu kontrol et")
    public  void controlProductsInfo(String key){
        File file = new File("products.csv");
        try {
            Scanner reader = new Scanner(file);
            String oldPrice = null;
            while (reader.hasNextLine()){
                oldPrice += reader.nextLine();
            }
            WebElement element=findElement(key);
            String newPrice = element.getText();
            Assert.assertEquals("Ürün eşleşmiyor",newPrice,oldPrice);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }



}


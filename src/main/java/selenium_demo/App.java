package selenium_demo;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v92.performance.Performance;
import org.openqa.selenium.devtools.v92.performance.model.Metric;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Hello world!
 *
 */
public class App {

  public static String projectDir = System.getProperty("user.dir");

  public static void main(String[] args)
    throws IOException, InterruptedException {
    ChromeDriver driver = new ChromeDriver();
    DevTools devTools = driver.getDevTools();
    devTools.createSession();
    devTools.send(Performance.enable(Optional.empty()));
    List<Metric> metricList = devTools.send(Performance.getMetrics());
    driver.get("http://google.com");
    driver.quit();
    for (Metric m : metricList) {
      System.out.println(m.getName() + " - " + m.getValue());
    }
  }

  public static void step2() throws IOException {
    init();
    WebDriver openedDriver = driverInstance();
    openedDriver.get(
      "https://www.selenium.dev/zh-cn/documentation/webdriver/js_alerts_prompts_and_confirmations/"
    );
    new WebDriverWait(openedDriver, Duration.ofSeconds(10))
    .until(
        ExpectedConditions.visibilityOf(
          openedDriver.findElement(By.xpath("//a[text()='查看样例警告框']"))
        )
      );
    openedDriver.findElement(By.xpath("//a[text()='查看样例警告框']")).click();
    Alert alert = new WebDriverWait(openedDriver, Duration.ofSeconds(10))
    .until(ExpectedConditions.alertIsPresent());
    String text = alert.getText();
    System.out.println(text);
    alert.accept();
    openedDriver.quit();
  }

  public static WebDriver driverInstance() {
    ChromeOptions options = new ChromeOptions();
    options.setExperimentalOption("debuggerAddress", "127.0.0.1:5555");
    WebDriver openedDriver = new ChromeDriver(options);
    return openedDriver;
  }

  /**
   * start chrome dirver with port 5555 by .bat file
   * @throws IOException
   */
  public static void init() throws IOException {
    System.setProperty(
      "webdriver.chrome.driver",
      projectDir + "\\src\\depandency\\chromedriver.exe"
    );
    ProcessBuilder pb = new ProcessBuilder(
      projectDir + "\\src\\depandency\\chromeInit.bat"
    );
    pb.start();
  }

  public static void killProcess() throws IOException {
    ProcessBuilder pb = new ProcessBuilder(
      projectDir + "\\src\\depandency\\findPIDByPort.bat"
    );
    pb.redirectInput();
    Process process = pb.start();
    BufferedReader input = new BufferedReader(
      new InputStreamReader(process.getInputStream())
    );
    String line;
    while ((line = input.readLine()) != null) {
      if (line.indexOf("LISTENING") >= 0) {
        Runtime
          .getRuntime()
          .exec(
            "taskkill /pid " +
            line.substring(line.lastIndexOf(" ") + 1, line.length())
          );
      }
    }
  }

  public static void step1() {
    WebDriver driver = new ChromeDriver();
    String originalWindow = driver.getWindowHandle();
    assert driver.getWindowHandles().size() == 1;

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    driver.switchTo().newWindow(WindowType.TAB);
    wait.until(numberOfWindowsToBe(2));
    for (String windowHandle : driver.getWindowHandles()) {
      if (!originalWindow.contentEquals(windowHandle)) {
        driver.switchTo().window(windowHandle);
        break;
      }
    }
    wait.until(titleIs("Selenium documentation"));
    driver.get("https://google.com/ncr");
    driver.manage().window().fullscreen();
    driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
    WebElement firstResult = new WebDriverWait(driver, Duration.ofSeconds(10))
    .until(ExpectedConditions.elementToBeClickable(By.xpath("//a/h3")));

    Wait<WebDriver> wait2 = new FluentWait<WebDriver>(driver)
      .withTimeout(Duration.ofSeconds(30))
      .pollingEvery((Duration.ofSeconds(5)))
      .ignoring(NoSuchElementException.class);
    WebElement foo1 = wait.until(
      new Function<WebDriver, WebElement>() {
        public WebElement apply(WebDriver driver) {
          return driver.findElement(By.id("foo"));
        }
      }
    );

    System.out.println(firstResult.getText());
    WebElement foo = new WebDriverWait(driver, Duration.ofSeconds(3))
    .until(
        new Function<WebDriver, WebElement>() {
          public WebElement apply(WebDriver driver) {
            return driver.findElement(By.name("q"));
          }
        }
      );
    assertEquals(foo.getText(), "Hello from JavaScript!");
    // WebElement element = driver.findElement(By.cssSelector("h1"));
    // File srcFile_1 = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
    // File srcFile = element.getScreenshotAs(OutputType.FILE);
    // FileUtils.copyFile(srcFile, new File("./image.png"));
    // FileUtils.copyFile(srcFile_1, new File("./image_1.png"));
    driver.quit();
  }

  public static ExpectedCondition<Boolean> titleIs(final String title) {
    return new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        return driver.getTitle() != title;
      }
    };
  }

  public static ExpectedCondition<Boolean> numberOfWindowsToBe(
    final int numberOfWindows
  ) {
    return new ExpectedCondition<Boolean>() {
      @Override
      public Boolean apply(WebDriver driver) {
        driver.getWindowHandles();
        return driver.getWindowHandles().size() == numberOfWindows;
      }
    };
  }
}

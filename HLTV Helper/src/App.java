import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

public class App {
    public static void main(String[] args) throws Exception {
        
        System.setProperty("webdriver.chrome.driver", "C:\\Java Software\\chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
        
        Dimension dimensions1 = new Dimension(1000, 700);
        driver.manage().window().setSize(dimensions1); // resize window to be large enough so that the top-right settings dropdown can be seen
        
        driver.get("https://www.hltv.org/stats/players"); // navigate to this page and pause execution until page is fully loaded
        driver.findElement(By.id("CybotCookiebotDialogBodyButtonDecline")).click(); // decline cookies
        driver.get("https://www.hltv.org/stats/players"); // page refreshes automatically after cookies option chosen, which can cause stale errors - us refreshing the page and waiting for it to complete refresh is a better option than using a Thread.sleep() etc
        
        driver.findElement(By.cssSelector("body > div.navbar > nav > div.navdown > i")).click(); // click settings dropdown toggle to open dropdown
        driver.findElement(By.cssSelector("#popupsettings > div:nth-child(2) > span.right.slider > span.toggleUserTheme.userTheme-night")).click(); // click dark theme - more aesthetically pleasing
        driver.findElement(By.cssSelector("body > div.navbar > nav > div.navdown > i")).click(); // click settings dropdown toggle to close dropdown
        
        driver.manage().window().maximize(); // maximise window - most users would do this once the program is finished, so automating it is a good idea
        System.out.println("Entry finished");
        
    }
}

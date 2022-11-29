import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class App {
    public static void main(String[] args) throws Exception {
        
        System.setProperty("webdriver.chrome.driver", "C:\\Java Software\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.hltv.org/stats/players");

        System.out.println("Entry finished");
    }
}

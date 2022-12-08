import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        
        Scanner userInput = new Scanner(System.in);
        System.out.println("Enter the name of the player you wish to view the 10 highest rated matches of.");
        String playerName = userInput.nextLine();
        
        System.setProperty("webdriver.chrome.driver", "C:\\Java Software\\chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
        
        Dimension dimensions1 = new Dimension(1000, 700);
        driver.manage().window().setSize(dimensions1); // resize window to be large enough so that the top-right settings dropdown can be seen
        
        driver.get("https://www.hltv.org/stats/players?minMapCount=50"); // navigate to this page and pause execution until page is fully loaded. this page contains all players with 50 or more HLTV-registered maps
        driver.findElement(By.id("CybotCookiebotDialogBodyButtonDecline")).click(); // decline cookies
        Thread.sleep(100); // gives time for site to register choice
        driver.get("https://www.hltv.org/stats/players?minMapCount=50"); // page refreshes automatically after cookies option chosen, which can cause stale errors - us refreshing the page and waiting for it to complete refresh is a better option than using a Thread.sleep() etc
        
        driver.findElement(By.cssSelector("body > div.navbar > nav > div.navdown > i")).click(); // click settings dropdown toggle to open dropdown
        driver.findElement(By.cssSelector("#popupsettings > div:nth-child(2) > span.right.slider > span.toggleUserTheme.userTheme-night")).click(); // click dark theme - more aesthetically pleasing
        driver.findElement(By.cssSelector("body > div.navbar > nav > div.navdown > i")).click(); // click settings dropdown toggle to close dropdown
        
        // finding player's match statistics page
        
        List<WebElement> foundElements = driver.findElements(By.partialLinkText(playerName)); // search for and return every webelement containing a hyperlink with the player's name in. can't just take the first element (using findElement) as a player's name can be contained in another player's name - e.g. nex is contained in nexa, so searching for nex could return nexa's hyperlink element
        
        String acceptedHyperlink = "";
        
        for (WebElement i : foundElements) {
            String hyperlink = i.getAttribute("href"); // grab the hyperlink from the webelement
            if (hyperlink.length() > 35 && hyperlink.substring(0, 35).equals("https://www.hltv.org/stats/players/")) { // checking if found link is to a player's stats page
                String[] hyperlinkSegments = hyperlink.split("/");
                if (hyperlinkSegments[hyperlinkSegments.length - 1].equals(playerName.toLowerCase())) { // the ending segment of a HLTV player stats page link is the player's name in lowercase. check if it matches the lowercase version of the player name the user submitted
                    acceptedHyperlink = hyperlink; // this is the requested player's correct link - the checks ensure that this is e.g. nex's page, not nexa's
                    break;
                }
            }
        }
        
        String[] acceptedHyperlinkSegments = acceptedHyperlink.split("/");
        String playerID = acceptedHyperlinkSegments[5]; // HLTV has an ID for each player, located in this segment of the hyperlink
        driver.get("https://www.hltv.org/stats/players/matches/" + playerID + "/" + playerName.toLowerCase()); // using the ID we found, access the statistics page for the player's matches
        
        // finding elements that could be match rating elements
        
        System.out.println("Finding td tag elements");
        List<WebElement> foundtdElements = driver.findElements(By.tagName("td"));
        System.out.println("Finished finding td tag elements");
        
        // generate matches HashMap by first sifting through foundtdElements for match rating elements
        
        HashMap<String, Float> matches = new HashMap<String, Float>(); // key is match hyperlink, value is rating
        String classValue;
        Float rating;
        
        System.out.println("Populating matches HashMap");
        
        for (WebElement i : foundtdElements) {
            classValue = i.getAttribute("class");
            if (classValue.equals("match-lost ratingPositive") || classValue.equals("match-won ratingPositive") || classValue.equals("match-lost ratingNegative") || classValue.equals("match-won ratingNegative")) { // rating elements have one of these four class tags
                
                try {
                    rating = Float.parseFloat(i.getText());
                }
                catch (NumberFormatException e) {
                    break; // if the rating isn't a float, we must have a rating from the old rating 1.0 system (indicated with a * after, hence the conversion fail). we will ignore these ratings for now. if we have reached a rating 1.0, every match below will also use rating 1.0 - so we can stop trying to find matches, hence the break
                }
                
                // using relative locators to navigate from the rating element to the element containing the corresponding match hyperlink
                // this is currently a very inefficient system - I'm sure there are better ways to do this
                
                WebElement matchHyperlinkElement1 = driver.findElement(RelativeLocator.with(By.tagName("td")).toLeftOf(i));
                WebElement matchHyperlinkElement2 = driver.findElement(RelativeLocator.with(By.tagName("td")).toLeftOf(matchHyperlinkElement1));
                WebElement matchHyperlinkElement3 = driver.findElement(RelativeLocator.with(By.tagName("td")).toLeftOf(matchHyperlinkElement2));
                WebElement matchHyperlinkElement4 = driver.findElement(RelativeLocator.with(By.tagName("a")).near(matchHyperlinkElement3));
                String matchHyperlink = matchHyperlinkElement4.getAttribute("href");
                
                matches.put(matchHyperlink, rating); // in the future the current plan is for this to only be executed if the rating is top 10 in the ratings discovered so far
            }
        }
        
        System.out.println("Finished populating matches HashMap");
        
        System.out.println(matches); // temporary
        
        userInput.close();
        driver.manage().window().maximize(); // maximise window - most users would do this once the program is finished, so automating it is a good idea
        System.out.println("Entry finished");
        
    }
}

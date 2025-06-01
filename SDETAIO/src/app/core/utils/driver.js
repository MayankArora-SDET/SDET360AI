const { Builder, By } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');

// Define the URL and JavaScript script
const script = "alert('Hello, Selenium!');";

async function runDriver(url) {
    // Configure Chrome options
    let options = new chrome.Options();
    options.addArguments("--detach"); // Keep the browser open after the script runs (optional)

    // Initialize the WebDriver for Chrome
    let driver = await new Builder()
        .forBrowser('chrome')
        .setChromeOptions(options)
        .build();

    try {
        // Navigate to the URL
        await driver.get(url);

        // Execute JavaScript in the browser
        await driver.executeScript(script);
    } catch (err) {
        console.error("An error occurred:", err);
    } finally {
        // Optional: Comment this line if you want to keep the browser open
        // await driver.quit();
    }
};
runDriver("https://www.oracle.com/in/");


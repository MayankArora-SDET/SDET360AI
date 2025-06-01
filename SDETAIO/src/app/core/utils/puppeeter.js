const puppeteer = require('puppeteer');

 async function openNewTabAndShowAlert() {
    // Launch a new browser instance
    const browser = await puppeteer.launch({ headless: false }); // Set headless: false to see the browser window
    const page = await browser.newPage();  // Open a new page/tab

    // Navigate to a website (or leave it blank for a new tab)
    await page.goto('https://www.oracle.com/in/');  // Navigate to a webpage
    await page.setViewport({ width: 1080, height: 1024 });


    // Execute JavaScript to show an alert box with "Hello"
    await page.evaluate(() => {
        alert('Hello');  // This will show an alert with the message 'Hello'
        return new Promise(resolve => setTimeout(resolve, 10000));
    });

    // Wait for a while so the alert is visible (for demonstration purposes)
    // await page.waitForTimeout(10000); // Wait for 3 seconds

    // Close the browser
    await browser.close();
}

openNewTabAndShowAlert();

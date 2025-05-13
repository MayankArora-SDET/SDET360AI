chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    console.log("Message received in background script:", message);

    if (message.action == "openUrlAndInjectJs") {
        let tabCreated = null;
        chrome.tabs.create({ url: message.urlToOpen }, (tab) => {
            tabCreated = tab;
            console.log("New tab created:", tabCreated);


            chrome.scripting.executeScript(
                {
                    target: { tabId: tabCreated.id },
                    files: ['contentScript.js'],
                },
                () => {
                    if (chrome.runtime.lastError) {
                        console.error("Error injecting script:", chrome.runtime.lastError.message);
                    } else {
                        chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
                            chrome.tabs.sendMessage(tabs[0].id, { action: 'getToken', token: message.token }, function (response) {
                                if (chrome.runtime.lastError) {
                                    console.error("Error:", chrome.runtime.lastError.message);
                                } else {
                                    console.log("Response from content script:", response);
                                }
                            });
                        });


                    }
                }
            );


            chrome.tabs.onUpdated.addListener(function (tabId, changeInfo, updatedTab) {
                if (tabId === tabCreated.id && changeInfo.url) {
                    console.log(`URL has changed to ${changeInfo.url}`)
                    chrome.scripting.executeScript({
                        target: { tabId: tabCreated.id },
                        files: ['contentScript.js'],
                    });
                    chrome.tabs.sendMessage(tabCreated.id, { action: 'getToken', token: message.token }, (response) => {
                        console.log("Response from content script:", response);
                    });
                }
            })
        });


    }
});


// chrome.runtime.onMessage.addListener((message,sender,sendResponse)=>{
//     if(message.action=="remove-js"){
//         console.log("removed")
//         //     chrome.tabs.reload(message.tabId)\
//         chrome.tabs.query({active: true, currentWindow: true},function(tabs) {
//             chrome.tabs.sendMessage(tabs[0].id,  {action: 'stop'}, function(response) {
//                 if (chrome.runtime.lastError) {
//                     console.error("Error:", chrome.runtime.lastError.message);
//                 } else {
//                     console.log("Response from content script:"+ response);
//                 }
//             });
//           }); 


//     }
// })

chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
    if (message.action === "validateToken") {
        fetch("http://tenant1.localhost:8080/api/tokens/validate?token=" + message.token, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Invalid token");
                }
                return response.json()
            })
            .then((response) => {
                sendResponse({ success: true, data: response.tokenData });
                // Inject a script into the active tab

            })
            .catch((error) => {
                sendResponse({ success: false, error: error });
            });

        // Return true to indicate the response will be sent asynchronously
        return true;
    }
    
    // Handle API requests from content script to bypass ad blockers
    if (message.action === "sendApiRequest") {
        console.log("Background script handling API request:", message);
        
        fetch(message.url, {
            method: message.method,
            headers: {
                "Content-Type": "application/json",
                "X-Requested-With": "XMLHttpRequest"
            },
            body: JSON.stringify(message.data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`API error: ${response.status} ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("API request successful:", data);
                sendResponse({ success: true, data: data });
            })
            .catch(error => {
                console.error("API request failed:", error);
                sendResponse({ success: false, error: error.toString() });
            });
        
        // Return true to indicate the response will be sent asynchronously
        return true;
    }
});

chrome.runtime.onMessage.addListener(async (message, sender, sendResponse) => {
    if (message.action == "endSession") {
        console.log("ending")
        try {
            const [tab] = await chrome.tabs.query({ active: true, lastFocusedWindow: true });
            chrome.tabs.remove(tab.id, () => {
                if (chrome.runtime.lastError) {
                    sendResponse({ success: false, error: chrome.runtime.lastError.message })
                } else {
                    sendResponse({ success: true })
                }
            })

        } catch (error) {
            sendResponse({ success: false, error: error.message })
        }
        return true
    }
})








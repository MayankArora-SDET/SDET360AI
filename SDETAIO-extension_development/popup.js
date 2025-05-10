document.getElementById("startRecording").addEventListener("click", async () => {
    const token = document.getElementById("token").value;

    if (token === "") {
        alert("Please enter a token");
        return;
    }


    // Send a message to the background script to validate the token
    chrome.runtime.sendMessage(
        {
            action: "validateToken",
            token: token
        },
        async (response) => {
            if (response.success == true) {
                chrome.runtime.sendMessage(
                    {
                        urlToOpen: response.data.url,
                        token: token,
                        action: "openUrlAndInjectJs",
                    },
                );

            } else {
                console.error("Token validation failed:", response.error);
                document.getElementById("error").textContent = "Failed to validate token. Please try again.";
                alert("Failed to validate token. Please try again.");
            }
        }
    );
});




document.getElementById("stopRecording").addEventListener("click", () => {
    (async () => {
        try {
            const [tab] = await chrome.tabs.query({ active: true, lastFocusedWindow: true });
            console.log(tab);

            const response = await chrome.tabs.sendMessage(tab.id, { action: "stop" });
            console.log(response);

        } catch (err) {
            console.log(err);
        }

    })();
})

// document.getElementById("stopRecording").addEventListener("click",()=>{
//     chrome.tabs.query({active: true, currentWindow: true}, (tabs) => {
//         if (tabs.length > 0) {
//              chrome.runtime.sendMessage({action: "remove-js", tabId: tabs[0].id});
//         }
//     });
// })


if (typeof scriptInjected === "undefined") {
    try {
        mainScript()
    } catch (e) {
        console.log(e)
    }
}
function mainScript() {
    scriptInjected = true
    createElement()

    var capturedElements = new Set();
    var inputElements = new Set();
    var inputValues = {};
    var eventsToSend = (sessionStorage.getItem("eventsToSend") && sessionStorage.getItem("eventsToSend") !== '')
        ? JSON.parse(sessionStorage.getItem("eventsToSend"))
        : [];
    if (sessionStorage.getItem("isRecording") == null) {
        sessionStorage.setItem("isRecording", "true")
    }
    if (sessionStorage.getItem("action") == null) { //this data storage is it send in api to create new array or to append
        sessionStorage.setItem("action", "create")
    }

    setTimeout(() => {
        console.log(sessionStorage.getItem("eventsToSend"), sessionStorage.getItem("isRecording"))
    }, 2000)

    function createElement() {
        // Create a new element (e.g., a div) with some HTML content
        const newElement = document.createElement('div');



        newElement.innerHTML = `
    <div  id="draggableDiv"   style="display:flex; cursor: move; align-items: center;justify-items:center;padding:10px;gap:10px;position:fixed;top:0;z-index:9999;background-color:white;height:40px;"font-size:smaller>
    <div style="width: 8px;height: 8px; background-color: red;border-radius: 10px;"></div>
    <p style="margin: 0;padding: 0;font-size: small;font-weight: 700;font-family: monospace;">Recording <span id="defineRecordState" style="width:60px;display:inline-block">Started</span></p>
    <button id="toggleRecording" style="border: none;background-color: #4652a5;border-radius: 5px;color: white;font-size: small;padding:4px;width:110px">Pause Recording</button>
       <button id="endSession" style="border: none;background-color: #4652a5;border-radius: 5px;color: white;font-size: small;padding:4px">End Session</button>

    </div>
    `;
        setTimeout(() => {
            document.body.insertBefore(newElement, document.body.firstChild);
            dragElement(document.getElementById("draggableDiv"));
            const toggleRecordingButton = document.getElementById("toggleRecording");
            const endSessionButton = document.getElementById("endSession");
            const defineRecordState = document.getElementById("defineRecordState")
            toggleRecordingButton.textContent = sessionStorage.getItem("isRecording") == "true" ? "Pause Recording" : "Resume Recording"

            toggleRecordingButton.addEventListener("click", () => {
                const isRecording = sessionStorage.getItem("isRecording")

                if (isRecording == "true") {
                    sendToAPI().then(response => {
                        sessionStorage.setItem('eventsToSend', JSON.stringify([]))
                        sessionStorage.setItem('isRecording', "false")
                        toggleRecordingButton.textContent = "Start Recording"
                        defineRecordState.textContent = "Paused"

                        if (sessionStorage.getItem("action") == "create") {
                            sessionStorage.setItem("action", "update")
                        }
                        eventsToSend = []
                    }).catch(e => {
                        console.error(e)

                    })
                } else {
                    sessionStorage.setItem("isRecording", 'true')
                    toggleRecordingButton.textContent = "Pause Recording"
                    defineRecordState.textContent = "Started"

                }

            })
            endSessionButton.addEventListener("click", async () => {
                try {
                    chrome.runtime.sendMessage({
                        action: "endSession",
                    }, (response) => {
                        if (response.success == true) {
                            console.log("Stopped successfully")
                        }
                    })
                } catch (error) {
                    console.log(error)
                }

            })

            document.getElementById("draggableDiv").addEventListener('click', function (event) {
                // Prevent the click event from doing anything
                event.preventDefault(); // Stops the default behavior
                event.stopPropagation(); // Stops the event from bubbling up
                return false
            });
        }, 1000)
    }
    //Make the DIV element draggagle:

    function dragElement(elmnt) {
        var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
        if (document.getElementById(elmnt.id)) {
            /* if present, the header is where you move the DIV from:*/
            document.getElementById(elmnt.id).onmousedown = dragMouseDown;
        } else {
            /* otherwise, move the DIV from anywhere inside the DIV:*/
            elmnt.onmousedown = dragMouseDown;
        }

        function dragMouseDown(e) {
            e = e || window.event;
            e.preventDefault();
            // get the mouse cursor position at startup:
            pos3 = e.clientX;
            pos4 = e.clientY;
            document.onmouseup = closeDragElement;
            // call a function whenever the cursor moves:
            document.onmousemove = elementDrag;
        }

        function elementDrag(e) {
            e = e || window.event;
            e.preventDefault();
            // calculate the new cursor position:
            console.log(pos1, pos2, pos3, pos4, "1")

            pos1 = pos3 - e.clientX;
            pos2 = pos4 - e.clientY;
            pos3 = e.clientX;
            pos4 = e.clientY;
            // set the element's new position:
            elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
            elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
            elmnt.style.
                console.log(pos1, pos2, pos3, pos4, "2")

        }

        function closeDragElement() {
            /* stop moving when mouse button is released:*/
            document.onmouseup = null;
            document.onmousemove = null;
        }
    }
    async function sendToAPI() {
        let eventsCaptured = JSON.parse(sessionStorage.getItem("eventsToSend") || "[]");
        const token = sessionStorage.getItem("token");
        const action = sessionStorage.getItem("action");
    
        // Add action to each event object
        eventsCaptured = eventsCaptured.map(event => ({
            ...event,
            action: action
        }));
    
        try {
            const response = await fetch("http://tenant1.localhost:8080/api/tokens/record", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    events: eventsCaptured,
                    token: token,
                    enableAssertion: false
                })
            });
    
            if (!response.ok) {
                throw new Error(`API error: ${response.status} ${response.statusText}`);
            }
    
            return await response.json();
        } catch (e) {
            console.error("Error sending data to API:", e);
            throw e;
        }
    }
    
    



    chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
        if (message.action === 'stop') {
            sendToAPI().then(response => {
                console.log("recorded successfully", response)
                sessionStorage.setItem('eventsToSend', JSON.stringify([]))
                sendResponse({ status: 'success', message: 'Data logged successfully' });

            }).catch(e => {
                console.error(e)
                sendResponse({ status: 'error', message: 'Failed to log interaction' });

            })

            // Return true to indicate that the response is sent asynchronously
            return true; // Keeps the message channel open for async tasks
        }

        // Reset captured elements

        if (message.action === 'getToken') {
            // Simulate processing the token
            sessionStorage.setItem("token", message.token);
            sendResponse({ status: 'success', data: 'Token processed successfully' });

            return true; // Ensures the channel stays open
        }
        if (message.action == "remove-js") {
            console.log("removed")
            chrome.tabs.reload(message.tabId)

        }
    });
    // Attach listeners only once per page
    function attachListeners() {
        if (window.listenersAttached) return; // Prevent reattaching listeners if already attached
        // Capture list of click interactions
        document.body.addEventListener("click", function (event) {
            if (sessionStorage.getItem("isRecording") == "true") {
                var element = event.target;
                var relativeXPath = getRelativeXpath(element);
                var absoluteXPath = getAbsoluteXPath(element);
                var relationalXPath = getRelationalXPath(element);

                if (!capturedElements.has(absoluteXPath)) {
                    capturedElements.add(absoluteXPath);
                    // Log interaction data directly to the interaction_log.txt
                    eventsToSend.push({ action: "click", relativeXPath: relativeXPath, absoluteXPath: absoluteXPath, relationalXPath: relationalXPath });
                    sessionStorage.setItem("eventsToSend", JSON.stringify(eventsToSend))
                }
            }


        });

        // Capture input interactions
        document.body.addEventListener('input', function (event) {
            if (sessionStorage.getItem("isRecording") == "true") {
                var element = event.target;
                var absoluteXPath = getAbsoluteXPath(element);
                if (!inputElements.has(absoluteXPath)) {
                    inputElements.add(absoluteXPath);
                    inputValues[absoluteXPath] = element.value;
                } else {
                    inputValues[absoluteXPath] = element.value;
                }
            }
        }, true);

        // Capture blur interactions for finalizing input logging
        document.body.addEventListener('blur', function (event) {
            if (sessionStorage.getItem("isRecording") == "true") {
                var element = event.target;
                var relativeXPath = getRelativeXpath(element);
                var absoluteXPath = getAbsoluteXPath(element);
                var relationalXPath = getRelationalXPath(element);
                if (inputElements.has(absoluteXPath)) {
                    eventsToSend.push({
                        type: 'input',
                        relativeXPath: relativeXPath,
                        absoluteXPath: absoluteXPath,
                        relationalXPath: relationalXPath,
                        value: inputValues[absoluteXPath]
                    });
                    sessionStorage.setItem("eventsToSend", JSON.stringify(eventsToSend))
                }
            }
        }, true);
        window.listenersAttached = true;
    }

    function getRelativeXpath(element) {
        if (!element) return null;

        function getNodeXPath(node) {
            let attributes = [];
            if (node.id) attributes.push('@id="' + node.id + '"');
            if (node.name) attributes.push('@name="' + node.name + '"');
            if (attributes.length > 0) return node.nodeName.toLowerCase() + "[" + attributes.join(" and ") + "]";

            let siblingIndex = 1;
            let sibling = node;
            while (sibling.previousElementSibling) {
                sibling = sibling.previousElementSibling;
                siblingIndex++;
            }
            return node.nodeName.toLowerCase() + "[" + siblingIndex + "]";
        }

        let current = element;
        let xpath = "";
        if (element.nodeType === Node.TEXT_NODE) {
            return element.parentNode.nodeName.toLowerCase() + '[text()="' + element.textContent.trim() + '"]';
        }

        while (current && current !== document.documentElement) {
            const currentNodeXPath = getNodeXPath(current);
            xpath = currentNodeXPath + (xpath ? "/" + xpath : "");
            if (current.id || current.name) break;
            current = current.parentElement;
        }
        return "//" + xpath;
    }

    // Function to get the absolute XPath of an element
    function getAbsoluteXPath(element) {
        let path = [];
        while (element.nodeType === Node.ELEMENT_NODE) {
            let index = 1;
            let sibling = element.previousSibling;
            while (sibling) {
                if (sibling.nodeType === Node.ELEMENT_NODE && sibling.nodeName === element.nodeName) {
                    index++;
                }
                sibling = sibling.previousSibling;
            }
            path.unshift(element.nodeName.toLowerCase() + '[' + index + ']');
            element = element.parentNode;
        }
        return path.length ? '/' + path.join('/') : null;
    }

    // Function to get the relational XPath of an element
    function getRelationalXPath(element) {
        let path = [];
        while (element && element.nodeType === Node.ELEMENT_NODE) {
            let nodeName = element.nodeName.toLowerCase();
            let attributes = getAttributes(element);
            let xpathPart = nodeName;
            if (attributes.length > 0) {
                xpathPart += `[${attributes.join(' and ')}]`;
            }
            path.unshift(xpathPart);
            element = element.parentElement;
        }
        return path.length ? '//' + path.join('//') : null;
    }

    function getAttributes(element) {
        let attrs = [];
        if (element.id) attrs.push(`@id="${element.id}"`);
        if (element.name) attrs.push(`@name="${element.name}"`);

        return attrs;
    }
    attachListeners(); // Attach the event listeners





}
let message = "";
let recording = false;
async function sendPrompt(prompt) {
    const url = window.location.origin + "/api/sendpromt";
    const params = {
        promt: prompt,
        timeOutForAnswer: "120"
    };
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(params)
        });
        if (response.ok) {
            console.log("Sent prompt successfully!");
        } else {
            console.log("Could not send prompt for some reason");
        }
    } catch (error) {
        console.error("Error in 'sendPrompt' method", error);
    }
}
document.addEventListener('keydown', (event) => {
    const key = event.key;
    if (key === "Home") {
        recording = true;
        message = "";
        document.getElementById('output').innerText = "Message: " + message;
        console.log("Recording started");
    } else if (key === "End" && recording) {
        recording = false;
        console.log("Recording stopped");
        document.getElementById('output').innerText = "Final Message: " + message;
        sendPrompt(message);
    } else if (key === "Delete" && recording) {
        recording = false;
        message = "";
        document.getElementById('output').innerText = "Message: " + message;
        console.log("Message cleared, waiting for Home");
    } else if (key === "Escape") {
        window.location.href = "https://newlms.misis.ru/";
    } else if (recording) {
        if (key === "Backspace") {
            message = message.slice(0, -1);
        } else {
            message += key;
        }
        document.getElementById('output').innerText = "Message: " + message;
    }
});
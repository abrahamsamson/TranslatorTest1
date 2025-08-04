let recognition;
let isRecognizing = false;
let stompClient = null;

// DOM elements
const presenterModeCheckbox = document.getElementById("presenterMode");
const startSessionBtn = document.getElementById("startSessionBtn");
const joinSessionBtn = document.getElementById("joinSessionBtn");
const sessionCodeDisplay = document.getElementById("sessionCode");
const recognizedDiv = document.getElementById("recognizedText");
const languageSelect = document.getElementById("languageSelect");
const presenterControls = document.getElementById("presenterControls");
const viewerControls = document.getElementById("viewerControls");
const joinCodeInput = document.getElementById("joinCode");
const recognizedHistory = document.getElementById("recognizedHistory");
const translatedHistory = document.getElementById("translatedHistory");

// Create a div for translated text if not present
let translatedDiv = document.getElementById("translatedText");
if (!translatedDiv) {
  translatedDiv = document.createElement("div");
  translatedDiv.id = "translatedText";
  translatedDiv.style = "min-height: 30px; background: #e0ffe7; padding: 10px; border-radius: 5px; margin-bottom: 10px; text-align:left;";
  recognizedDiv.parentNode.insertBefore(translatedDiv, recognizedHistory);
}

let recognizedLines = [];
let translatedLines = [];

let sessionCode = null;

// Toggle presenter/viewer controls visibility
presenterModeCheckbox.addEventListener("change", () => {
  if (presenterModeCheckbox.checked) {
    presenterControls.style.display = "block";
    viewerControls.style.display = "none";
  } else {
    presenterControls.style.display = "none";
    viewerControls.style.display = "block";
  }
});

// Start session as presenter
startSessionBtn.addEventListener("click", async () => {
  try {
    const response = await fetch("http://localhost:8080/create-session", {
      method: "POST",
    });

    if (!response.ok) {
      throw new Error("Failed to create session");
    }

    const data = await response.json();
    sessionCode = data.sessionCode;
    sessionCodeDisplay.innerText = sessionCode;

    connectStomp();
    startContinuousRecognition();
  } catch (err) {
    alert("Error creating session: " + err.message);
    console.error(err);
  }
});

// Join session as viewer
joinSessionBtn.addEventListener("click", () => {
  const code = joinCodeInput.value.trim();
  if (!code) {
    alert("Please enter a session code to join.");
    return;
  }

  sessionCode = code;
  sessionCodeDisplay.innerText = sessionCode;
  connectStomp();
});

// Connect to STOMP WebSocket server
function connectStomp() {
  const socket = new SockJS('http://localhost:8080/ws');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);

    // Subscribe to translation topic
    stompClient.subscribe('/topic/translated', function(message) {
      try {
        const data = JSON.parse(message.body);

        // Update current speech and history
        if (data.originalText) {
          recognizedDiv.innerText = data.originalText;
          recognizedLines.push(data.originalText);
          updateHistory(recognizedHistory, recognizedLines);
        }

        // Update translated text and history
        if (data.translatedText) {
          translatedDiv.innerText = data.translatedText;
          translatedLines.push(data.translatedText);
          updateHistory(translatedHistory, translatedLines);
        }
      } catch (e) {
        translatedDiv.innerText = message.body;
        translatedLines.push(message.body);
        updateHistory(translatedHistory, translatedLines);
      }
    });
  });
}

// Update transcript history lists
function updateHistory(listElement, linesArray) {
  listElement.innerHTML = "";
  linesArray.forEach(line => {
    const li = document.createElement("li");
    li.textContent = line;
    listElement.appendChild(li);
  });
}

// Send translation request via STOMP
function sendTranslate(text, targetLang) {
  if (stompClient && stompClient.connected) {
    stompClient.send("/app/translate", {}, JSON.stringify({
      text: text,
      targetLang: targetLang
    }));
  }
}

// Start continuous speech recognition (Presenter only)
function startContinuousRecognition() {
  if (!("webkitSpeechRecognition" in window)) {
    alert("Your browser does not support Speech Recognition API.");
    return;
  }

  if (recognition) {
    recognition.stop();
    recognition = null;
  }

  recognition = new webkitSpeechRecognition();
  recognition.continuous = true;
  recognition.interimResults = true;
  recognition.lang = "en-US";

  recognition.onstart = () => {
    isRecognizing = true;
    console.log("Speech recognition started...");
  };

  recognition.onresult = (event) => {
    let interimTranscript = "";
    let finalTranscript = "";

    for (let i = event.resultIndex; i < event.results.length; ++i) {
      if (event.results[i].isFinal) {
        finalTranscript += event.results[i][0].transcript;
      } else {
        interimTranscript += event.results[i][0].transcript;
      }
    }

    recognizedDiv.innerText = finalTranscript || interimTranscript;

    // Only add final results to history and send for translation
    if (finalTranscript && stompClient && stompClient.connected) {
      sendTranslate(finalTranscript, languageSelect.value);
      recognizedLines.push(finalTranscript);
      updateHistory(recognizedHistory, recognizedLines);
    }
  };

  recognition.onerror = (event) => {
    console.error("Speech recognition error", event.error);
  };

  recognition.onend = () => {
    if (isRecognizing) {
      recognition.start(); // Restart recognition automatically for continuous mode
    }
  };

  recognition.start();
}
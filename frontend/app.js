const micBtn = document.getElementById('micBtn');
const inputText = document.getElementById('inputText');
const translateBtn = document.getElementById('translateBtn');
const languageSelect = document.getElementById('languageSelect');
const translatedDiv = document.getElementById('translatedText');

let recognition;
let recognizing = false;

// Check if browser supports SpeechRecognition
if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  recognition = new SpeechRecognition();
  recognition.lang = 'en-US';
  recognition.interimResults = false;

  recognition.onstart = () => {
    recognizing = true;
    micBtn.innerText = '🎙️ Listening... Click to stop';
  };

  recognition.onresult = (event) => {
    const transcript = event.results[0][0].transcript;
    inputText.value = transcript;
  };

  recognition.onerror = (event) => {
    alert('Speech recognition error: ' + event.error);
  };

  recognition.onend = () => {
    recognizing = false;
    micBtn.innerText = '🎤';
  };

  micBtn.addEventListener('click', () => {
    if (recognizing) {
      recognition.stop();
      micBtn.innerText = '🎤';
    } else {
      recognition.start();
    }
  });
} else {
  micBtn.disabled = true;
  micBtn.title = 'Speech recognition not supported in this browser.';
  micBtn.style.opacity = 0.5;
}

// Translation button click
translateBtn.addEventListener('click', () => {
  const text = inputText.value.trim();
  const targetLang = languageSelect.value;

  if (!text) {
    alert("Please enter some text to translate.");
    return;
  }

  fetch('http://localhost:8080/translate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text, targetLang })
  })
    .then(response => {
      if (!response.ok) throw new Error('Network response not ok');
      return response.json();
    })
    .then(data => {
      translatedDiv.innerText = data.translatedText || 'No translation returned';
    })
    .catch(error => {
      console.error('Error:', error);
      translatedDiv.innerText = 'Translation failed.';
    });
});

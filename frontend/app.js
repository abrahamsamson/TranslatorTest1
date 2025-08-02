document.getElementById('translateBtn').addEventListener('click', () => {
  const text = document.getElementById('inputText').value.trim();
  const targetLang = document.getElementById('languageSelect').value;
  const translatedDiv = document.getElementById('translatedText');

  if (!text) {
    alert("Please enter some text to translate.");
    return;
  }

  fetch('http://localhost:8080/translate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ text, targetLang })
  })
    .then(response => response.json())
    .then(data => {
      translatedDiv.innerText = data.translatedText;
    })
    .catch(error => {
      console.error('Error:', error);
      translatedDiv.innerText = 'Translation failed.';
    });
});

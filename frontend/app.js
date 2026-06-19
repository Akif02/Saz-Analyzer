const startBtn = document.getElementById('startBtn');
const stopBtn = document.getElementById('stopBtn');
const output = document.getElementById('output');

let ws;
let intervalId;

function log(message) {
    const p = document.createElement('p');
    p.textContent = `[${new Date().toLocaleTimeString()}.${new Date().getMilliseconds().toString().padStart(3, '0')}] ${message}`;
    p.style.margin = "2px 0";
    output.insertBefore(p, output.firstChild);
}

startBtn.addEventListener('click', () => {
    ws = new WebSocket('ws://localhost:8081/ws/pitch');

    ws.onopen = () => {
        log('WebSocket verbunden (Frontend -> Java Backend).');
        startBtn.disabled = true;
        stopBtn.disabled = false;

        // Dummy-Frequenz (z.B. D4 = 293.66 Hz) alle 100ms senden
        intervalId = setInterval(() => {
            if (ws.readyState === WebSocket.OPEN) {
                const dummyFrequency = 293.66;
                log(`Sende Frequenz an Backend: ${dummyFrequency}`);
                ws.send(JSON.stringify({ frequency: dummyFrequency }));
            }
        }, 100);
    };

    ws.onmessage = (event) => {
        log(`Antwort vom Backend empfangen: ${event.data}`);
    };

    ws.onerror = (error) => {
        log(`WebSocket Fehler! Ist das Backend gestartet?`);
        console.error(error);
    };

    ws.onclose = () => {
        log('WebSocket geschlossen.');
        stopInterval();
    };
});

stopBtn.addEventListener('click', () => {
    if (ws) {
        ws.close();
    }
    stopInterval();
});

function stopInterval() {
    clearInterval(intervalId);
    startBtn.disabled = false;
    stopBtn.disabled = true;
}

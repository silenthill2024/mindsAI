const express = require("express");
const http = require("http");
const WebSocket = require("ws");
const cors = require("cors");

const app = express();

app.use(cors());
app.use(express.json());

const server = http.createServer(app);

const wss = new WebSocket.Server({
    server,
    path: "/stream"
});

const clients = new Set();

function broadcast(message) {

    const data = JSON.stringify(message);

    clients.forEach(client => {

        if (client.readyState === WebSocket.OPEN) {
            client.send(data);
        }

    });
}

wss.on("connection", (ws) => {

    console.log("Nuevo cliente conectado");

    clients.add(ws);

    ws.send(JSON.stringify({
        type: "CONNECTED",
        message: "Conectado a MindsAI Streaming API"
    }));

    ws.on("message", (message) => {

        try {

            const data = JSON.parse(message.toString());

            console.log("Evento recibido:", data);

            broadcast(data);

        } catch (error) {

            ws.send(JSON.stringify({
                type: "ERROR",
                message: "Mensaje JSON inválido"
            }));

        }

    });

    ws.on("close", () => {

        console.log("Cliente desconectado");

        clients.delete(ws);

    });

    ws.on("error", (error) => {

        console.error("WebSocket error:", error.message);

        clients.delete(ws);

    });

});

app.get("/", (req, res) => {

    res.json({
        app: "MindsAI Streaming API",
        status: "online"
    });

});

app.get("/status", (req, res) => {

    res.json({
        status: "online",
        websocket: "/stream",
        clients: clients.size
    });

});

app.post("/event", (req, res) => {

    const event = req.body;

    if (!event.type) {

        return res.status(400).json({
            error: "El campo type es requerido"
        });

    }

    broadcast(event);

    res.status(201).json({
        success: true,
        event
    });

});

const PORT = process.env.PORT || 3000;

server.listen(PORT, "0.0.0.0", () => {

    console.log("");
    console.log("====================================");
    console.log(" MindsAI Streaming API");
    console.log("====================================");
    console.log(`HTTP:      http://localhost:${PORT}`);
    console.log(`WebSocket: ws://localhost:${PORT}/stream`);
    console.log("====================================");
    console.log("");

});
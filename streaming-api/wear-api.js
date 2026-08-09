const express = require("express");
const crypto = require("crypto");

const app = express();

app.use(express.json({
    limit: "10mb"
}));

const PORT = 3001;

/*
 * Sesiones Wear en memoria.
 *
 * Para la versión académica actual es suficiente.
 * Después se puede mover a BD.
 */
const wearSessions = new Map();

function generateToken() {
    return crypto
        .randomBytes(32)
        .toString("hex");
}

function getBearerToken(req) {

    const authorization =
        req.headers.authorization || "";

    if (!authorization.startsWith("Bearer ")) {
        return null;
    }

    return authorization
        .substring(7)
        .trim();
}

function findSessionByToken(token) {

    for (const session of wearSessions.values()) {

        if (session.token === token) {
            return session;
        }
    }

    return null;
}

/*
 * Celular registra al usuario que actualmente
 * inició sesión.
 */
app.post("/wear/auth", (req, res) => {

    const {
        deviceId,
        uid,
        nombre,
        email,
        role,
        photoUrl
    } = req.body || {};

    if (!deviceId) {
        return res.status(400).json({
            success: false,
            message: "deviceId requerido"
        });
    }

    if (!uid) {
        return res.status(400).json({
            success: false,
            message: "uid requerido"
        });
    }

    const existing =
        wearSessions.get(deviceId);

    const token =
        existing?.token ||
        generateToken();

    const session = {
        token,
        deviceId,
        uid,
        nombre: nombre || "",
        email: email || "",
        role: role || "ALUMNO",
        photoUrl: photoUrl || "",
        updatedAt: Date.now()
    };

    wearSessions.set(
        deviceId,
        session
    );

    console.log(
        `[WEAR] Sesión actualizada: ${nombre} (${uid})`
    );

    res.json({
        success: true,
        token,
        user: {
            uid: session.uid,
            nombre: session.nombre,
            email: session.email,
            role: session.role,
            photoUrl: session.photoUrl
        }
    });
});

/*
 * El reloj obtiene el token correspondiente
 * a su deviceId.
 *
 * Esta ruta es temporal para desarrollo local.
 */
app.get(
    "/wear/device/:deviceId/session",
    (req, res) => {

        const session =
            wearSessions.get(
                req.params.deviceId
            );

        if (!session) {

            return res
                .status(404)
                .json({
                    success: false,
                    message:
                        "Reloj sin sesión"
                });
        }

        res.json({
            success: true,
            token: session.token
        });
    }
);

/*
 * Perfil protegido con Bearer token.
 */
app.get("/wear/profile", (req, res) => {

    const token =
        getBearerToken(req);

    if (!token) {

        return res
            .status(401)
            .json({
                success: false,
                message: "Token requerido"
            });
    }

    const session =
        findSessionByToken(token);

    if (!session) {

        return res
            .status(401)
            .json({
                success: false,
                message: "Token inválido"
            });
    }

    res.json({
        success: true,
        data: {
            uid: session.uid,
            nombre: session.nombre,
            email: session.email,
            role: session.role,
            photoUrl: session.photoUrl
        }
    });
});

app.get("/health", (_, res) => {

    res.json({
        success: true,
        service: "MindsAI Wear API"
    });
});

app.listen(
    PORT,
    "0.0.0.0",
    () => {

        console.log("");
        console.log(
            "================================"
        );
        console.log(
            " MindsAI Wear API"
        );
        console.log(
            ` Puerto: ${PORT}`
        );
        console.log(
            "================================"
        );
        console.log("");
    }
);
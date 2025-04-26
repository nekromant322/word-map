import { CONFIG } from "../config/api-config.js"

export async function requestLoginCode(email) {
    const response = await fetch(`${CONFIG.API_BASE_URL}/auth/admin/login/${encodeURIComponent(email)}`);
    return response;
}

export async function confirmLoginCode(email, code) {
    const response = await fetch(`${CONFIG.API_BASE_URL}/auth/admin/confirm`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, code })
    });
    return response;
}

export async function refreshAccessToken(refreshToken) {
    const response = await fetch(`${CONFIG.API_BASE_URL}/auth/admin/refresh`, {
        method: "POST",
        headers: {
            "Refresh": refreshToken
        }
    });

    return response;
}

export async function logoutAdmin() {
    try {
        const accessToken = localStorage.getItem("access-token");

        const response = await fetch(`http://localhost:8080/auth/admin/logout`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + accessToken
            }
        });
    } catch (error) {
        // ignored
    } finally {
        localStorage.removeItem("access-token");
        localStorage.removeItem("refresh-token");
    }
}

import { CONFIG } from "../config/api-config.js"

export async function requestLoginCode(email) {
    const response = await fetch(`${CONFIG.API_BASE_URL}/auth/admin/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email })
    });
    return response;
}

export async function confirmLoginCode(confirmID, codeInput) {
    const response = await fetch(`${CONFIG.API_BASE_URL}/auth/admin/confirm`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ confirmID, codeInput })
    });
    return response;
}

export async function refreshAccessToken() {
    const response = await fetch(`${CONFIG.API_BASE_URL}/auth/admin/refresh`, {
        method: "POST",
        credentials: "include"
    });

    return response;
}

export async function logoutAdmin() {
    const accessToken = localStorage.getItem("access-token");

    try {
        const response = await fetch(`http://localhost:8080/auth/admin/logout`, {
            method: "POST",
            keepalive: true,
            credentials: "include",
            headers: {
                "Authorization": "Bearer " + accessToken
            },
        });

        await response.text();

        localStorage.removeItem("access-token");

        setTimeout(() => {
            window.location.replace('/login');
        }, 300);

    } catch (err) {
        console.error("Logout failed", err);
        localStorage.removeItem('access-token');
        window.location.replace('/login');
    }
}

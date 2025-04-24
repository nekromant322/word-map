import {refreshAccessToken} from "../api/admin-auth-api.js";
import {ALL_RULES} from "./rules.js";

export function getAccessToken() {
    return localStorage.getItem("access-token");
}

export function getRefreshToken() {
    return localStorage.getItem("refresh-token");
}

export function setAccessToken(token) {
    localStorage.setItem("access-token", token);
}

export function setRefreshToken(token) {
    localStorage.setItem("refresh-token", token);
}

export function deleteAccessToken() {
    localStorage.removeItem("access-token");
}

export function deleteRefreshToken() {
    localStorage.removeItem("refresh-token");
}

export const availableBlocks = [
    { name: "Управление словарем", page: "../pages/dictionary.html", rule: ALL_RULES.MANAGE_DICTIONARY },
    { name: "Очистка словаря", page: "../pages/dictionary.html", rule: ALL_RULES.WIPE_DICTIONARY},
    { name: "Управление рейтингом игроков", page: "rating.html", rule: ALL_RULES.MANAGE_RATING},
    { name: "Управление мирами", page: "worlds.html", rule: ALL_RULES.MANAGE_WORLD},
    { name: "Управление пользователями", page: "../pages/admins.html", rule: ALL_RULES.MANAGE_ROLE },
    { name: "Управление ивентами (игровыми событиями)", page: "ivents.html", rule: ALL_RULES.MANAGE_IVENT},
    { name: "Управление магазином", page: "shop.html", rule: ALL_RULES.MANAGE_SHOP}
];

export function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c =>
            '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(''));

        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

export async function checkAuthorization() {
    const accessToken = getAccessToken();
    const refreshToken = getRefreshToken();

    if (!accessToken || !refreshToken) {
        redirectToLogin();
        return;
    }

    const accessPayload = parseJwt(accessToken);
    const now = Math.floor(Date.now() / 1000); // текущее время в секундах

    if (accessPayload.exp && accessPayload.exp < now) {
        const success = await tryRefreshToken(refreshToken);
        if (!success) {
            redirectToLogin();
        }
    }
}

export async function tryRefreshToken(token) {
    try {
        const response = await refreshAccessToken(token);

        if (!response.ok) return false;

        const responseBody = await response.json();
        localStorage.setItem("access-token", responseBody.accessToken);
        localStorage.setItem("refresh-token", responseBody.refreshToken);
        return true;
    } catch (error) {
        console.error("Ошибка обновления токена:", error);
        return false;
    }
}

export function redirectToLogin() {
    deleteAccessToken();
    deleteRefreshToken();
    window.location.href = "../../pages/login.html";
}

export function checkAccessByRule(rule) {
    const token = getAccessToken();

    const payload = parseJwt(token);
    const role = payload.role || "";
    const rules = payload.rules || [];

    return role === "ADMIN" || rules.includes(rule);
}
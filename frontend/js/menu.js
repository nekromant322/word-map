import {parseJwt, availableBlocks, checkAuthorization, redirectToLogin, getAccessToken} from './util/jwt.js';
import {logoutAdmin} from "./api/admin-auth-api.js";

document.addEventListener("DOMContentLoaded", async () => {
    await checkAuthorization();

    const token = getAccessToken();
    const container = document.getElementById("button-container");
    const welcome = document.getElementById("welcome-message");

    const payload = parseJwt(token);

    if (!payload) {
        redirectToLogin();
    }

    const role = payload.role;
    const rules = payload.rules || [];

    welcome.textContent = `Вы вошли как ${role}`;

    availableBlocks.forEach(block => {
        if (role === 'ADMIN' || rules.includes(block.rule)) {
            const div = document.createElement("div");
            div.className = "col-md-4 mb-3";
            div.innerHTML = `
                <a href="${block.page}" class="btn btn-primary w-100">${block.name}</a>
            `;
            container.appendChild(div);
        }
    });

    document.getElementById("logout-btn").addEventListener("click", () => {
        logoutAdmin();
        redirectToLogin();
    });
});

import {confirmLoginCode, requestLoginCode} from "./api/admin-auth-api.js";
import {setAccessToken} from "./util/jwt.js";
import {redirectToMenu} from "./util/redirect.js";

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("login-form");
    const emailInput = document.getElementById("email");
    const sendCodeBtn = document.getElementById("send-code-btn");

    const resultDiv = document.getElementById("result");
    const codeForm = document.getElementById("code-form");
    const codeInput = document.getElementById("code");

    let confirmID;

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const email = emailInput.value.trim();
        resultDiv.textContent = "";
        sendCodeBtn.disabled = true; // блокируем кнопку

        try {
            const response = await requestLoginCode(email);

            if (response.ok) {
                const data = await response.json();
                confirmID = data.confirmID;

                resultDiv.style.display = "none";

                sendCodeBtn.style.display = "none";
                codeForm.classList.remove("d-none");
            } else if (response.status === 404) {
                resultDiv.textContent = "❌ Пользователь не найден";
                resultDiv.className = "alert alert-danger mt-3";
            } else if (response.status === 403) {
                resultDiv.textContent = "❌ Аккаунт заблокирован";
                resultDiv.className = "alert alert-warning mt-3";
            } else if (response.status === 400) {
                resultDiv.textContent = "❌ Невалидный формат почты";
                resultDiv.className = "alert alert-danger mt-3";
            } else {
                resultDiv.textContent = `❌ Ошибка входа`;
                resultDiv.className = "alert alert-danger mt-3";
            }
        } catch (error) {
            console.log(error.message);
            resultDiv.textContent = `❌ Ошибка входа`;
            resultDiv.className = "alert alert-danger mt-3";
        } finally {
            sendCodeBtn.disabled = false; // разблокируем кнопку
        }
    });

    codeForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        const code = codeInput.value.trim();
        resultDiv.textContent = "";

        try {
            console.log(confirmID)
            const response = await confirmLoginCode(confirmID, code);

            if (response.ok) {
                const tokenResponse = await response.json();

                setAccessToken(tokenResponse.accessToken);
                redirectToMenu();
            } else if (response.status === 400) {
                resultDiv.textContent = "❌ Неверный код подтверждения";
                resultDiv.className = "alert alert-danger mt-3";
            } else if (response.status === 404) {
                resultDiv.textContent = "❌ Пользователь не найден";
                resultDiv.className = "alert alert-danger mt-3";
            } else {
                resultDiv.textContent = `❌ Ошибка при подтверждении`;
                resultDiv.className = "alert alert-danger mt-3";
            }
        } catch (error) {
            console.log(error.message);
            resultDiv.textContent = "❌ Ошибка при подтверждении";
            resultDiv.className = "alert alert-danger mt-3";
        }
    });
});

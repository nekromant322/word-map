import {getAccessToken, redirectToLogin, tryRefreshToken} from "../util/jwt.js";

export async function authorizedFetch(input, init = {}) {
    let token = getAccessToken();

    // Добавляем токен в заголовки
    init.headers = {
        ...init.headers,
        Authorization: `Bearer ${token}`
    };

    let response = await fetch(input, init);

    if (response.status === 401) {
        const success = await tryRefreshToken();

        if (success) {
            // Обновим токен и повторим запрос
            token = localStorage.getItem("access-token");
            init.headers.Authorization = `Bearer ${token}`;
            response = await fetch(input, init);
        } else {
            redirectToLogin();
        }
    }

    return response;
}

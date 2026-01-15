export const CONFIG = {
    API_BASE_URL: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
        ? "http://localhost:8080"
        : "http://45.8.230.91:2507"
};
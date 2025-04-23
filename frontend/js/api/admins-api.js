import {getAccessToken} from "../util/jwt.js";
import {CONFIG} from "../config/api-config.js";
import {adminsOnPage} from "../config/constants.js";

export async function getRules() {
    const token = getAccessToken();
    const response = await fetch(`${CONFIG.API_BASE_URL}/admins/rules`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    return response;
}

export async function getAdmins(pageNumber) {
    const numbersOnPage = adminsOnPage;
    const token = getAccessToken();

    const response = await fetch(`${CONFIG.API_BASE_URL}/admins?page=${pageNumber}&size=${numbersOnPage}`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    return response;
}

export async function getAdmin(adminId) {
    const token = getAccessToken();

    const response = await fetch(`${CONFIG.API_BASE_URL}/admins/${adminId}`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });
    return response;
}
import {getAccessToken} from "../util/jwt.js";
import {CONFIG} from "../config/api-config.js";
import {adminsOnPage} from "../config/constants.js";
import {authorizedFetch} from "./util.js";

export async function getRules() {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/admins/rules`, {
        method: "GET"
    });
}

export async function getAdmins(pageNumber) {
    const numbersOnPage = adminsOnPage;
    return authorizedFetch(`${CONFIG.API_BASE_URL}/admins?page=${pageNumber}&size=${numbersOnPage}`, {
        method: "GET"
    });
}

export async function getAdmin(adminId) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/admins/${adminId}`, {
        method: "GET"
    });
}
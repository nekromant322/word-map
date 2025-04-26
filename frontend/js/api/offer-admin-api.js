import {CONFIG} from "../config/api-config.js";
import {authorizedFetch} from "./util.js";

export async function getWordOffers(page) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/wordsOffer/admin/check?page=${page}`, {
        method: "GET"
    });
}

export async function approveWord(id) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/wordsOffer/admin/approve/${id}`, {
        method: "POST"
    });
}

export async function rejectWord(id) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/wordsOffer/admin/reject/${id}`, {
        method: "POST"
    });
}

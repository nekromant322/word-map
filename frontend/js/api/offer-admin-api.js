import {CONFIG} from "../config/api-config.js";
import {authorizedFetch} from "./util.js";

export async function getWordOffers(page = 0, size = 10, sortBy = "createdAt", sortDir = "desc", status = "") {
    const body = {
        page,
        size,
        sortBy,
        sortDir,
        status
    };

    return authorizedFetch(`${CONFIG.API_BASE_URL}/wordsOffer/admin/list`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });
}

export async function approveWord(id, description) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/wordsOffer/admin/approve/${id}?description=${encodeURIComponent(description)}`, {
        method: "POST"
    });
}

export async function rejectWord(id) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/wordsOffer/admin/reject/${id}`, {
        method: "POST"
    });
}

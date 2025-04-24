import {CONFIG} from "../config/api-config.js";
import {authorizedFetch} from "./util.js";

export async function getWordsJson() {
    return await authorizedFetch(`${CONFIG.API_BASE_URL}/dictionary/word/list`, {
        method: "GET"
    })
}

export async function findWord(query) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/dictionary/word/${encodeURIComponent(query)}`, {
        method: "GET"
    });
}

export async function addNewWord(word, description) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/dictionary/word`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ word, description })
    });
}

export async function updateWord(id, word, description){
    return authorizedFetch(`${CONFIG.API_BASE_URL}/dictionary/word`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ id, word, description })
    });
}

export async function deleteWord(wordId) {
    return authorizedFetch(`${CONFIG.API_BASE_URL}/dictionary/word/${encodeURIComponent(wordId)}`, {
        method: "DELETE"
    });
}
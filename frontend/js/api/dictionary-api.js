import {CONFIG} from "../config/api-config.js";

export async function getWordsJson() {
    const token = localStorage.getItem("access-token");

    const response = await fetch(`${CONFIG.API_BASE_URL}/dictionary/word/list`, {
        method: "GET",
        headers: {
            "Authorization": 'Bearer ' + token
        }
    });
    return response;
}

export async function findWord(query) {
    const token = localStorage.getItem("access-token");
    const response = await fetch(`${CONFIG.API_BASE_URL}/dictionary/word/${encodeURIComponent(query)}`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    return response;
}

export async function addNewWord(word, description) {
    const token = localStorage.getItem("access-token");
    const response = await fetch(`${CONFIG.API_BASE_URL}/dictionary/word`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ word, description })
    });
    return response;
}

export async function updateWord(id, word, description){
    const token = localStorage.getItem("access-token");
    const response = await fetch(`${CONFIG.API_BASE_URL}/dictionary/word`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ id, word, description })
    });
    return response;
}

export async function deleteWord(wordId) {
    const token = localStorage.getItem("access-token");
    const response = await fetch(`${CONFIG.API_BASE_URL}/dictionary/word/${encodeURIComponent(wordId)}`, {
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + token
        }
    });
    return response
}
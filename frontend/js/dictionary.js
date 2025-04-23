import {addNewWord, deleteWord, findWord, getWordsJson, updateWord} from "./api/dictionary-api.js";
import {checkAccessByRule, checkAuthorization, parseJwt, redirectToLogin} from "./util/jwt.js";
import {ALL_RULES} from "./util/rules.js";
import {redirectToMenu} from "./util/redirect.js";

document.addEventListener("DOMContentLoaded", async () => {
    await checkAuthorization();

    const hasAccess = checkAccessByRule(ALL_RULES.MANAGE_DICTIONARY);
    if (!hasAccess) {
        redirectToMenu();
    }

    const exportBtn = document.getElementById("export-btn");
    const addForm = document.getElementById("add-word-form");
    const updateForm = document.getElementById("update-word-form");
    const searchBtn = document.getElementById("searchBtn");
    const searchInput = document.getElementById("searchInput");
    const searchResult = document.getElementById("searchResult");

    // ВЫГРУЗКА СЛОВ
    exportBtn.addEventListener("click", async () => {
        exportBtn.disabled = true;
        exportBtn.textContent = "⏳ Загрузка...";

        try {
            const response = await getWordsJson();

            console.log(response)
            if (!response.ok) throw new Error("Ошибка выгрузки слов");

            const blob = await response.blob();
            const link = document.createElement("a");
            link.href = window.URL.createObjectURL(blob);
            link.download = "dictionary.json";
            link.click();
        } catch (error) {
            console.log(error.message)
            alert("Ошибка выгрузки словаря");
        } finally {
            exportBtn.disabled = false;
            exportBtn.textContent = "📥 Выгрузить словарь";
        }
    });

    // ДОБАВЛЕНИЕ СЛОВА
    addForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const word = document.getElementById("add-word-word-input").value.trim();
        const description = document.getElementById("add-word-word-input").value.trim();

        if (!word || !description) return;

        try {
            const response = await addNewWord(word, description);

            if (response.status === 400) {
                alert("Слово уже существует");
            } else if (!response.ok) {
                throw new Error("Не удалось добавить слово");
            } else {
                alert("Слово добавлено");
                addForm.reset();
            }
        } catch (err) {
            alert(err.message);
        }
    });

    // обновление слова
    updateForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const id = document.getElementById("update-word-id-input").value.trim();
        const word = document.getElementById("update-word-word-input").value.trim();
        const description = document.getElementById("update-word-description-input").value.trim();

        if (!id || !word || !description) {
            alert("Пожалуйста, заполните все поля.");
            return;
        }

        try {
            const response = await updateWord(id, word, description);

            if (response.status === 404) {
                alert("Слово с таким ID не найдено");
            } else if (!response.ok) {
                throw new Error("Ошибка обновления слова");
            } else {
                alert("Слово обновлено успешно");
                updateForm.reset();
                const modal = document.getElementById("updateWordModal");
                modal.hide();
            }
        } catch (err) {
            alert("Ошибка: " + err.message);
        }
    });

    searchBtn.addEventListener("click", async () => {
        const query = searchInput.value.trim();
        if (!query) {
            alert("Введите слово для поиска");
            return;
        }

        try {
            const response = await findWord(query);

            if (response.status === 404) {
                searchResult.textContent = "Слово не найдено";
                return;
            }

            if (!response.ok) throw new Error("Ошибка поиска слова");

            const word = await response.json();
            searchResult.innerHTML = `
            <strong>Id:</strong> ${word.id} <br/>
            <strong>Слово:</strong> ${word.word} <br/>
            <strong>Описание:</strong> ${word.description} <br/>
            <button id="deleteWordBtn" class="btn btn-danger mt-2">🗑️ Удалить слово</button>
        `;

            // Привязываем обработчик уже к появившейся кнопке
            document.getElementById("deleteWordBtn").addEventListener("click", async () => {
                if (!confirm("Точно удалить это слово?")) return;

                try {
                    const deleteResponse = await deleteWord(word.id); // используем ID

                    if (deleteResponse.status === 404) {
                        alert("Слово уже было удалено");
                    } else if (!deleteResponse.ok) {
                        throw new Error("Ошибка при удалении");
                    } else {
                        searchResult.textContent = "Слово удалено успешно.";
                    }
                } catch (error) {
                    alert("Ошибка: " + error.message);
                }
            });

        } catch (error) {
            alert("Ошибка: " + error.message);
        }
    });
});

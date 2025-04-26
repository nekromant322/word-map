import {getWordOffers, approveWord, rejectWord} from "./api/offer-admin-api.js";
import {checkAuthorization, checkAccessByRule} from "./util/jwt.js";
import {redirectToMenu} from "./util/redirect.js";
import {ALL_RULES} from "./util/rules.js";

document.addEventListener("DOMContentLoaded", async () => {
    await checkAuthorization();

    const hasAccess = checkAccessByRule(ALL_RULES.MANAGE_OFFER);
    if (!hasAccess) {
        redirectToMenu();
        return;
    }

    const offerList = document.getElementById("offers-list");
    const nextPageBtn = document.getElementById("nextPage");
    const prevPageBtn = document.getElementById("prevPage");

    let currentPage = 0;

    async function loadOffers() {
        try {
            const response = await getWordOffers(currentPage);
            if (!response.ok) throw new Error("Ошибка при загрузке предложений");

            const pageData = await response.json();
            offerList.innerHTML = "";

            pageData.content.forEach(offer => {
                const li = document.createElement("li");
                li.className = "list-group-item d-flex justify-content-between align-items-center";
                li.innerHTML = `
                    <div>
                        <strong>${offer.word}</strong> — ${offer.description}
                        <br/>
                        Автор: ${offer.userId  || "неизвестен"}
                    </div>
                    <div>
                        <button class="btn btn-success btn-sm me-2" data-id="${offer.id}" data-action="approve">✔️</button>
                        <button class="btn btn-danger btn-sm" data-id="${offer.id}" data-action="reject">❌</button>
                    </div>
                `;
                offerList.appendChild(li);
            });

            offerList.querySelectorAll("button").forEach(button => {
                button.addEventListener("click", async (e) => {
                    const id = e.target.dataset.id;
                    const action = e.target.dataset.action;

                    if (action === "approve") {
                        await approveWord(id);
                    } else if (action === "reject") {
                        await rejectWord(id);
                    }

                    await loadOffers(); // reload page
                });
            });

        } catch (err) {
            console.error(err);
            alert("Ошибка: " + err.message);
        }
    }

    nextPageBtn.addEventListener("click", () => {
        currentPage++;
        loadOffers();
    });

    prevPageBtn.addEventListener("click", () => {
        if (currentPage > 0) {
            currentPage--;
            loadOffers();
        }
    });

    await loadOffers();
});

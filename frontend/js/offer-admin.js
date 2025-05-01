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
    const descriptionModal = new bootstrap.Modal(document.getElementById("descriptionModal"));
    const descriptionInput = document.getElementById("descriptionInput");
    const confirmApproveBtn = document.getElementById("confirmApproveBtn");

    let currentPage = 0;
    let pageSize = 10;
    let sortBy = "createdAt";
    let sortDir = "desc";
    let statusFilter = "";
    let currentApproveId = null;

    const statusSelect = document.getElementById("statusSelect");

    statusSelect.addEventListener("change", () => {
        statusFilter = statusSelect.value;
        currentPage = 0;
        loadOffers();
    });
    async function loadOffers() {
        try {
            const response = await getWordOffers(currentPage, pageSize, sortBy, sortDir, statusFilter);
            if (!response.ok) throw new Error("Ошибка при загрузке предложений");

            const pageData = await response.json();
            offerList.innerHTML = "";

            if (pageData.content.length === 0) {
                offerList.innerHTML = "<div class='text-muted'>Нет предложений для отображения</div>";
                return;
            }

            pageData.content.forEach(offer => {
                const li = document.createElement("li");
                li.className = "list-group-item d-flex justify-content-between align-items-center";
                li.innerHTML = `
                <div>
                    <strong>${offer.word}</strong>
                    <br/>
                    Автор: ${offer.userId || "неизвестен"}<br/>
                    Статус: ${offer.status}
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
                        currentApproveId = id;
                        descriptionInput.value = "";
                        descriptionModal.show();
                    } else if (action === "reject") {
                        await rejectWord(id);
                        await loadOffers();
                    }
                });
            });

        } catch (err) {
            console.error(err);
            alert("Ошибка: " + err.message);
        }
    }

    confirmApproveBtn.addEventListener("click", async () => {
        const description = descriptionInput.value.trim();
        if (!description) {
            alert("Введите описание.");
            return;
        }

        await approveWord(currentApproveId, description);
        descriptionModal.hide();
        await loadOffers();
    });

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
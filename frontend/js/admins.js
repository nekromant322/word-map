import {getAdmins, getAdmin, getRules} from "./api/admins-api.js";
import {checkAuthorization, checkAccessByRule, redirectToLogin, availableBlocks} from "./util/jwt.js";
import {ALL_RULES} from "./util/rules.js";
import {redirectToMenu} from "./util/redirect.js";
import {adminsOnPage} from "./config/constants.js";

document.addEventListener("DOMContentLoaded", async () => {
    await checkAuthorization();

    const hasAccess = checkAccessByRule(ALL_RULES.MANAGE_ROLE);
    if (!hasAccess) {
        redirectToMenu();
    }

    const adminList = document.getElementById("admins-list");
    const adminDetails = document.getElementById("admin-details");
    const adminName = document.getElementById("admin-name");
    const adminInfo = document.getElementById("admin-info");
    const rulesList = document.getElementById("rules-list");

    const nextPageBtn = document.getElementById("nextPage");
    const prevPageBtn = document.getElementById("prevPage");

    let currentPage = 0;
    let countOnPage = adminsOnPage;

    async function loadAdmins() {
        try {
            const response = await getAdmins(currentPage);
            if (!response.ok) throw new Error("Ошибка загрузки админов");

            const data = await response.json();
            adminList.innerHTML = "";

            data.admins.content.forEach(admin => {
                const button = document.createElement("button");
                button.className = "list-group-item list-group-item-action";
                button.textContent = `${admin.id}. ${admin.email} ${admin.role}`;
                button.addEventListener("click", () => loadAdminDetails(admin.id));
                adminList.appendChild(button);
            });

            countOnPage = data.admins.totalElements;
        } catch (err) {
            alert("Ошибка: " + err.message);
        }
    }

    async function loadAdminDetails(id) {
        try {
            const response = await getAdmin(id);
            if (!response.ok) throw new Error("Ошибка загрузки информации об админе");

            const admin = await response.json();
            adminName.textContent = `${admin.id}. (${admin.email})`;

            // Мапим rule'ы в читаемые названия
            const ruleNames = admin.adminRules || [];
            const mappedRules = ruleNames.map(rule =>
                availableBlocks.find(block => block.rule === rule.rule)?.name || rule
            );

            adminInfo.innerHTML = `
            Роль: <strong>${admin.role}</strong><br/>
            Доступ: <strong>${admin.access ? "Разрешён" : "Запрещён"}</strong><br/>
            Правила: <ul>${mappedRules.map(name => `<li>${name}</li>`).join("")}</ul>
        `;
            adminDetails.classList.remove("d-none");
        } catch (err) {
            alert("Ошибка: " + err.message);
        }
    }

    async function loadRules() {
        try {
            const response = await getRules();
            if (!response.ok) throw new Error("Ошибка загрузки правил");

            const rules = await response.json();
            rulesList.innerHTML = "";

            rules.forEach(rule => {
                const friendlyName = availableBlocks.find(b => b.rule === rule.rule)?.name || "Неизвестное правило";

                const li = document.createElement("li");
                li.className = "list-group-item";
                li.innerHTML = `
                <strong>${friendlyName}</strong><br/>
                <small class="text-muted">${rule.id}. ${rule.rule}</small>
            `;
                rulesList.appendChild(li);
            });
        } catch (err) {
            console.error(err.message);
            alert("Ошибка загрузки правил: " + err.message);
        }
    }

    nextPageBtn.addEventListener("click", () => {
        if (adminsOnPage === countOnPage) {
            currentPage++;
            loadAdmins();
        }
    });

    prevPageBtn.addEventListener("click", () => {
        if (currentPage > 0) {
            currentPage--;
            loadAdmins();
        }
    });

    await loadAdmins();
    await loadRules();
});

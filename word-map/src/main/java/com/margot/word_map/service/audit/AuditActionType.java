package com.margot.word_map.service.audit;

import lombok.Getter;

import java.util.List;

@Getter
public enum AuditActionType {
    ADMIN_CREATED("Создан пользователь {email}", "email"),
    ADMIN_LOGGED_IN("Выполнен вход"),
    ADMIN_UPDATED("Отредактирован пользователь {email}", "email"),
    ADMIN_ACCESS_CHANGED("Изменен доступ пользователя {email}", "email"),

    PLATFORM_CREATED("Добавлена новая платформа {name}", "name"),
    PLATFORM_UPDATED("Обновлены данные о платформе {name}", "name"),
    PLATFORM_DELETED("Удалена платформа {name}", "name"),

    LANGUAGE_CREATED("Добавлен новый язык {name}", "name"),
    LANGUAGE_UPDATED("Обновлены данные языка {name}", "name"),
    LANGUAGE_DELETED("Удален язык {name}", "name"),
    LANGUAGE_LETTER_CREATED("Добавлена новая буква '{letter}' для языка {languageId}",
            "letter", "languageId"),
    LANGUAGE_LETTER_UPDATED("Обновлена буква '{letter}' для языка {languageId}", "letter", "languageId"),
    LANGUAGE_LETTER_DELETED("Удалена буква '{letter}' из языка {name}", "letter", "name"),

    DICTIONARY_WORD_ADDED("Новое слово в словаре '{word}'", "word"),
    DICTIONARY_WORD_UPDATED("Обновлено слово в словаре '{word}'", "word"),
    DICTIONARY_WORD_DELETED("Удалено слово '{word}' из словаря", "word"),
    DICTIONARY_OFFER_STATUS_CHANGED("Изменен статус предложенного слова '{word}'", "word"),

    PATTERN_CREATED("Создан паттерн {id}", "id"),
    PATTERN_UPDATED("Отредактирован паттерн {id}", "id"),
    PATTERN_DELETED("Удален паттерн {id}", "id");

    private final String value;
    private final List<String> requiredKeys;

    AuditActionType(String value, String... keys) {
        this.value = value;
        this.requiredKeys = List.of(keys);
    }

    public String render(Object... args) {
        if (args.length != requiredKeys.size()) {
            throw new IllegalArgumentException("this audit action requires " + requiredKeys.size() + " entries");
        }

        String template = value;
        for (int i = 0; i < requiredKeys.size(); i++) {
            template = template.replace("{" + requiredKeys.get(i) + "}", String.valueOf(args[i]));
        }

        return template;
    }
}
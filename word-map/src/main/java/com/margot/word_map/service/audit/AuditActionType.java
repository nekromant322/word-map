package com.margot.word_map.service.audit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum AuditActionType {
    ADMIN_CREATED("Создан пользователь email"),
    ADMIN_LOGGED_IN("Выполнен вход"),
    ADMIN_UPDATED("Отредактирован пользователь email"),
    ADMIN_ACCESS_CHANGED("Изменен доступ пользователя email"),

    PLATFORM_CREATED("Добавлена новая платформа {request.body.name}"),
    PLATFORM_UPDATED("Обновлены данные о платформе {request.body.name}"),
    PLATFORM_DELETED("Удалена платформа {platform.name}"),

    LANGUAGE_CREATED("Добавлен новый язык {request.body.name}"),
    LANGUAGE_UPDATED("Обновлены данные языка {request.body.name}"),
    LANGUAGE_DELETED("Удален язык {language.name}"),
    LANGUAGE_LETTER_CREATED("Добавлена новая буква {request.body.letter} для языка {request.body.languageId}"),
    LANGUAGE_LETTER_UPDATED("Обновлена буква {request.body.letter} для языка {letter.language_id}"),
    LANGUAGE_LETTER_DELETED("Удалена буква {letter.letter} из языка {language.name}"),

    DICTIONARY_WORD_ADDED("Новое слово в словаре '${request.body.word}'"),
    DICTIONARY_WORD_UPDATED("Обновлено слово в словаре '${selected_word}'"),
    DICTIONARY_WORD_DELETED("Удалено слово '${word}' из словаря"),
    DICTIONARY_OFFER_STATUS_CHANGED("Изменен статус предложенного слова '${request.body.word}'"),

    PATTERN_CREATED("Создан паттерн ${id}"),
    PATTERN_UPDATED("Отредактирован паттерн ${id}"),
    PATTERN_DELETED("Удален паттерн ${id}");

    private final String value;

    public String render(Map<String, Object> data) {
        String template = value;
        for (var entry : data.entrySet()) {
            template = template.replace(
                    entry.getKey(),
                    String.valueOf(entry.getValue()));
        }

        return template;
    }
}
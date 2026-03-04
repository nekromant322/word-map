package com.margot.word_map.service.audit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AuditActionTypeTest {

    @ParameterizedTest
    @MethodSource("getRenderTestValues")
    public void testCreateStringFromTemplate(AuditActionType type, String expected, Object... args) {
        assertThat(type.render(args)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getEnumWrongArgs")
    public void testThrowExceptionWhenArgsCountDoesNotMatch(AuditActionType type, Object... args) {
        assertThatThrownBy(() -> type.render(args))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(
                        "Для этого действия аудита требуется " + type.getRequiredKeys().size() + " элементов");
    }

    static Stream<Arguments> getRenderTestValues() {
        return Stream.of(
                Arguments.of(AuditActionType.ADMIN_CREATED, "Создан пользователь abc123@example.com",
                        new String[] {"abc123@example.com"}),
                Arguments.of(AuditActionType.ADMIN_LOGGED_IN, "Выполнен вход", new String[] {}),
                Arguments.of(AuditActionType.LANGUAGE_LETTER_CREATED,
                        "Добавлена новая буква 'a' для языка 1", new String[] {"a", "1"}),
                Arguments.of(AuditActionType.DICTIONARY_WORD_DELETED, "Удалено слово 'abc' из словаря",
                        new String[] {"abc"})
        );
    }

    static Stream<Arguments> getEnumWrongArgs() {
        return Stream.of(
                Arguments.of(AuditActionType.ADMIN_CREATED, new Object[] {}),
                Arguments.of(AuditActionType.ADMIN_LOGGED_IN, new Object[] {"abc123"}),
                Arguments.of(AuditActionType.LANGUAGE_LETTER_CREATED, new Object[] {"abc123"}),
                Arguments.of(AuditActionType.PATTERN_DELETED, new Object[] {"abc123", "def456"})
        );
    }
}

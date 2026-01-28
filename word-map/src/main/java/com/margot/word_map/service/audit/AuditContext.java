package com.margot.word_map.service.audit;

import java.util.HashMap;
import java.util.Map;

public class AuditContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT =
            ThreadLocal.withInitial(HashMap::new);

    public static void add(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static Map<String, Object> get() {
        return CONTEXT.get();
    }

    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    public static boolean contains(String key) {
        return CONTEXT.get().containsKey(key);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

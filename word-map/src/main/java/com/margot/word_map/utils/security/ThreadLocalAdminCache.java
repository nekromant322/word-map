package com.margot.word_map.utils.security;

import com.margot.word_map.model.Admin;
import org.springframework.stereotype.Component;

@Component
public class ThreadLocalAdminCache {

    private static final ThreadLocal<Admin> ADMIN_THREAD_LOCAL = new ThreadLocal<>();

    public void setAdmin(Admin admin) {
        ADMIN_THREAD_LOCAL.set(admin);
    }

    public Admin getAdmin() {
        return ADMIN_THREAD_LOCAL.get();
    }

    public boolean isEmpty() {
        return getAdmin() == null;
    }

    public void clear() {
        ADMIN_THREAD_LOCAL.remove();
    }
}

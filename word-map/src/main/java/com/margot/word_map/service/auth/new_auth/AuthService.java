package com.margot.word_map.service.auth.new_auth;

import com.margot.word_map.service.auth.new_auth.admin.AdminService;
import com.margot.word_map.service.auth.new_auth.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AdminService adminService;


}

package com.njha.inboxapp.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is used only for testing user github login functionality. Can be deleted later.
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public String getUser(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println(principal);
        return "Hello " + principal.getAttribute("name");
    }
}

package com.dreamhome.config;

import com.dreamhome.repository.UserRepository;
import com.dreamhome.table.Users;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class CookieHelper {

    final UserRepository userRepository;

    public String getCookieValue(HttpServletRequest req, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void deleteCookie(HttpServletRequest req, HttpServletResponse res, String cookieName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    cookie.setMaxAge(0);
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setSecure(true);
                    res.addCookie(cookie);
                    break;
                }
            }
        }
    }


    public void setCookie(HttpServletResponse res, Users user, String cookieName) {
        UUID cookieId = UUID.randomUUID();
        user.setCookie(cookieId);
        Cookie cookie = new Cookie(cookieName, cookieId.toString());
        cookie.setPath("/");
        cookie.setSecure(true);
        res.addCookie(cookie);
        userRepository.save(user);
    }

}

package org.valdon.abtests.service.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieService {

    public Optional<String> getValueFromCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter((c -> c.getName().equals(cookieName)))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void setValueToCookie(
            String cookieName,
            String value,
            HttpServletResponse response,
            int maxAge
    ) {
        response.addCookie(createCookie(cookieName, value, maxAge));
    }

    public void deleteCookie(
            String cookieName,
            HttpServletResponse response
    ) {
        response.addCookie(createCookie(cookieName, null, 0));
    }

    private Cookie createCookie(String cookieName, String value, int maxAge) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        return cookie;
    }

}

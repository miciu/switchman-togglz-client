package de.is24.common.togglz.filter.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;


public class SimpleCookieHandler implements CookieHandler {
  final String cookieName;
  final String cookiePath;

  public SimpleCookieHandler() {
    this("togglz", "/");
  }

  public SimpleCookieHandler(String cookieName, String cookiePath) {
    this.cookieName = cookieName;
    this.cookiePath = cookiePath;
  }

  @Override
  public String retrieveCookiePayload(HttpServletRequest request) {
    Optional<Cookie> cookieOptional = retrieveCookie(request);
    if (!cookieOptional.isPresent()) {
      return "";
    }
    return cookieOptional.get().getValue();
  }

  @Override
  public void safePayloadToCookie(String cookiePayload, HttpServletResponse response) {
    Cookie cookie = new Cookie(cookieName, cookiePayload);
    cookie.setPath(cookiePath);
    cookie.setMaxAge(-1);
    response.addCookie(cookie);
  }

  private Optional<Cookie> retrieveCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    return findCookieByNameAndPath(cookies, cookieName, cookiePath);
  }

  private Optional<Cookie> findCookieByNameAndPath(Cookie[] cookies, String cookieName, String cookiePath) {
    Cookie[] nullSafeCookies = (cookies != null) ? cookies : new Cookie[0];
    cookiePath = (cookiePath == null) ? "/" : cookiePath;
    for (int i = nullSafeCookies.length; i-- > 0;) {
      Cookie cookie = nullSafeCookies[i];
      if (cookieName.equals(cookie.getName())) {
        String pathFromCookie = cookie.getPath();
        if (cookiePath.equals(pathFromCookie) || ("/".equals(cookiePath) && (pathFromCookie == null))) {
          return Optional.of(cookie);
        }
      }
    }
    return Optional.empty();
  }

}

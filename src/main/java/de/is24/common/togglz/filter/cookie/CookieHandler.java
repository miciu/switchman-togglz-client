package de.is24.common.togglz.filter.cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface CookieHandler {
  String retrieveCookiePayload(HttpServletRequest request);

  void savePayloadToCookie(String cookiePayload, HttpServletResponse response);
}

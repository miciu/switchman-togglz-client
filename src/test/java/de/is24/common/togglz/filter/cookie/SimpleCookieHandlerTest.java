package de.is24.common.togglz.filter.cookie;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.http.Cookie;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class SimpleCookieHandlerTest {
  private static final String CONTENT = "Some content.";
  private static final String COOKIE_NAME = "togglz";
  private static final String COOKIE_PATH = "/";

  private MockHttpServletRequest mockHttpServletRequest;
  private SimpleCookieHandler simpleCookieHandler = new SimpleCookieHandler(COOKIE_NAME, COOKIE_PATH);
  ;

  private String cookiePayload;
  private MockHttpServletResponse mockHttpServletResponse;

  @Test
  public void retrievesPayloadFromCookie() {
    givenRequestWithTogglzCookie();
    whenRetrievingPayload();
    thenTheCorrectPayloadIsRetrieved();
  }

  @Test
  public void savesPayloadToCookie() {
    givenResponse();
    whenSavingPayload();
    thenResponseContainsCookieWithPayload();
  }

  private void givenResponse() {
    mockHttpServletResponse = new MockHttpServletResponse();
  }

  private void givenRequestWithTogglzCookie() {
    mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setCookies(new Cookie(COOKIE_NAME, CONTENT));
  }

  private void whenSavingPayload() {
    simpleCookieHandler.savePayloadToCookie(CONTENT, mockHttpServletResponse);
  }

  private void whenRetrievingPayload() {
    cookiePayload = simpleCookieHandler.retrieveCookiePayload(mockHttpServletRequest);
  }

  private void thenResponseContainsCookieWithPayload() {
    Cookie cookieFromResponse = mockHttpServletResponse.getCookie(COOKIE_NAME);
    assertThat(cookieFromResponse.getPath(), is(COOKIE_PATH));
    assertThat(cookieFromResponse.getValue(), is(CONTENT));
  }

  private void thenTheCorrectPayloadIsRetrieved() {
    assertThat(cookiePayload, is(CONTENT));
  }
}

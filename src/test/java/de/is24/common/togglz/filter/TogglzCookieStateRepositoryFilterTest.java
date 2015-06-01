package de.is24.common.togglz.filter;

import de.is24.common.togglz.filter.cookie.SimpleCookieHandler;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.togglz.core.Feature;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.repository.FeatureState;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;


@RunWith(MockitoJUnitRunner.class)
public class TogglzCookieStateRepositoryFilterTest {
  private static final String FEATURES_COOKIE_PAYLOAD = "ENABLED_FEATURE=true,DISABLED_FEATURE=false";
  private static final String CHANGED_FEATURES_COOKIE_PAYLOAD = "DISABLED_FEATURE=false,ENABLED_FEATURE=false";
  private static final String POST = "POST";
  private MockHttpServletRequest request = new MockHttpServletRequest();
  private MockHttpServletResponse response = new MockHttpServletResponse();

  @Mock
  FilterChain filterChain;

  private SimpleCookieHandler simpleCookieHandler = new SimpleCookieHandler();
  private TogglzCookieStateRepositoryFilter filterAndRepository = new TogglzCookieStateRepositoryFilter(
    new EnumBasedFeatureProvider(Features.class),
    simpleCookieHandler);

  @Test
  public void shouldDeserializeFeatureStatesFromCookie() throws IOException, ServletException {
    givenFeatureStatesInCookie();

    Mockito.doAnswer(invocationOnMock -> {
      thenFeatureStatesAreDeserialized();
      return null;
    }).when(filterChain).doFilter(request, response);

    filterAndRepository.doFilter(request, response, filterChain);
  }

  @Test
  public void shouldEraseFeatureStateAfterFilterChain() throws IOException, ServletException {
    givenFeatureStatesInCookie();
    filterAndRepository.doFilter(request, response, filterChain);
    thenFeatureStateIsErased();
  }

  @Test
  public void serializedFeatureMapAfterTogglzConsolePostRequest() throws IOException, ServletException {
    givenPostCallToTogglzConsoleUrl();
    givenFeatureStatesInRequestCookie();

    Mockito.doAnswer(invocationOnMock -> {
      FeatureState changedFeatureState = new FeatureState(Features.ENABLED_FEATURE);
      filterAndRepository.setFeatureState(changedFeatureState);
      return null;
    }).when(filterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    filterAndRepository.doFilter(request, response, filterChain);
    thenChangedFeatureStateIsSerializedToCookie();
  }

  private void givenFeatureStatesInRequestCookie() {
    request.setCookies(new Cookie("togglz", FEATURES_COOKIE_PAYLOAD));
  }

  private void givenPostCallToTogglzConsoleUrl() {
    request.setRequestURI(filterAndRepository.togglzEditPagePath);
    request.setMethod(POST);
  }

  private void givenFeatureStatesInCookie() {
    simpleCookieHandler.savePayloadToCookie(FEATURES_COOKIE_PAYLOAD, response);
    request.setCookies(response.getCookies());
  }

  private void thenFeatureStatesAreDeserialized() {
    assertThat(filterAndRepository.getFeatureState(Features.ENABLED_FEATURE), is(enabledFeatureState()));
    assertThat(filterAndRepository.getFeatureState(Features.DISABLED_FEATURE), is(not(enabledFeatureState())));
  }

  private void thenChangedFeatureStateIsSerializedToCookie() {
    assertThat(response.getCookie("togglz").getValue(), is(CHANGED_FEATURES_COOKIE_PAYLOAD));
  }

  private void thenFeatureStateIsErased() {
    assertThat(filterAndRepository.getFeatureState(Features.ENABLED_FEATURE), is(nullValue()));
    assertThat(filterAndRepository.getFeatureState(Features.DISABLED_FEATURE), is(nullValue()));
  }

  private Matcher<FeatureState> enabledFeatureState() {
    return new FeatureMatcher<FeatureState, Boolean>(is(true), "feature state that is enabled", "feature state") {
      @Override
      protected Boolean featureValueOf(FeatureState featureState) {
        return featureState.isEnabled();
      }
    };
  }

  private enum Features implements Feature {
    ENABLED_FEATURE,
    DISABLED_FEATURE;
  }
}

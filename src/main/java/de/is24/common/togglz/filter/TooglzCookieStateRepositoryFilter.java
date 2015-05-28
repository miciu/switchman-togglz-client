package de.is24.common.togglz.filter;

import de.is24.common.togglz.filter.cookie.CookieHandler;
import de.is24.common.togglz.filter.wrapper.BufferingResponseWrapper;
import de.is24.common.togglz.repository.ThreadLocalStateRepository;
import org.apache.commons.lang.StringUtils;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.spi.FeatureProvider;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * A StateRepository for the Togglz framework, that serializes the FeatureStates in a cookie on the client.
 * This makes no sense in production settings, but if you like to run parallel webtests that expect the same feature
 * switch in different states, this class is helpful.
 *
 * This class implements Servlet's Filter interface to be able to load and save the FeaturesStates from / to a cookie.
 * As this has to be done, before Togglz comes into play, this Filter has to be placed before the Togglz filter.
 *
 */
public class TooglzCookieStateRepositoryFilter extends ThreadLocalStateRepository implements Filter {
  private String togglzEditPagePath = "/internal/togglz/edit";

  private final FeatureProvider featureProvider;
  private final CookieHandler cookieHandler;

  public TooglzCookieStateRepositoryFilter(FeatureProvider featureProvider, CookieHandler cookieHandler) {
    this.featureProvider = featureProvider;
    this.cookieHandler = cookieHandler;
  }

  // ---------- filter stuff -----------
  @Override
  public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                       final FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
    setModificationState(false);
    setFeatureMap(buildFeatureMapFromRequest(httpRequest));
    try {
      if (isTogglzPostRequest(httpRequest)) {
        handleTogglzPostRequest(servletRequest, servletResponse, filterChain);
      } else {
        filterChain.doFilter(servletRequest, servletResponse);
      }
    } finally {
      resetThreadLocals();
    }
  }

  private boolean isTogglzPostRequest(final HttpServletRequest servletRequest) {
    return "POST".equalsIgnoreCase(servletRequest.getMethod()) &&
      servletRequest.getRequestURI().endsWith(togglzEditPagePath);
  }

  public void handleTogglzPostRequest(final ServletRequest servletRequest, final ServletResponse servletResponse,
                                      final FilterChain filterChain) throws IOException, ServletException {
    BufferingResponseWrapper wrapper = new BufferingResponseWrapper((HttpServletResponse) servletResponse);
    try {
      // toggle console does not send CRSF header, so faking it here
      filterChain.doFilter(servletRequest, wrapper);
    } finally {
      if (getModificationState()) {
        safeFeatureMapToCookie(getFeatureMap(), wrapper);
      }
      wrapper.send();
    }
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do
  }

  @Override
  public void destroy() {
    // nothing to do
  }

  public String getTogglzEditPagePath() {
    return togglzEditPagePath;
  }

  public void setTogglzEditPagePath(String togglzEditPagePath) {
    this.togglzEditPagePath = togglzEditPagePath;
  }

  private Map<String, FeatureState> buildFeatureMapFromRequest(final HttpServletRequest request) {
    String cookiePayload = cookieHandler.retrieveCookiePayload(request);
    Map<String, FeatureState> featureMap = deserializeFeatureMap(cookiePayload);
    ;
    if (featureMap == null) {
      featureMap = new HashMap<>();
    }
    return featureMap;
  }

  private String serializeFeatureMap(Map<String, FeatureState> map) {
    Collection<String> features = new ArrayList<>();
    map.forEach((f, s) -> features.add(f + "=" + s.isEnabled()));
    return StringUtils.join(features, ",");
  }

  private Map<String, FeatureState> deserializeFeatureMap(String value) {
    String[] split = value.split(",");

    Map<String, FeatureState> map = new HashMap<>();

    for (int i = 0; i < split.length; i++) {
      String featureState = split[i];
      String[] featureStateParts = featureState.split("=");
      String featureName = featureStateParts[0];
      boolean enabled = Boolean.valueOf(featureStateParts[1]);

      Optional<Feature> feature = findFeatureByName(featureName);

      if (feature.isPresent()) {
        Feature f = feature.get();
        map.put(f.name(), new FeatureState(f, enabled));
      }
    }

    return map;
  }

  private Optional<Feature> findFeatureByName(String name) {
    Set<Feature> features = featureProvider.getFeatures();

    return features.stream().filter(f -> name.equals(f.name())).findFirst();
  }

  private void safeFeatureMapToCookie(final Map<String, FeatureState> featureMap,
                                      final HttpServletResponse response) {
    String cookiePayload = serializeFeatureMap(featureMap);
    cookieHandler.safePayloadToCookie(cookiePayload, response);
  }

}

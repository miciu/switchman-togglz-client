package de.is24.common.togglz.remote.config;

/**
 * You have to provide these settings in your Spring configuration. Just add a bean!
 */
public class TogglzRemoteClientSettings {
  private final boolean remoteEnabled;
  private final Boolean fallbackEnabled;

  private final String username;
  private final String password;

  private final String baseUri;
  private final Integer timeoutInMilliseconds;

  public TogglzRemoteClientSettings(boolean remoteEnabled, Boolean fallbackEnabled, String username, String password,
                                    String baseUri, Integer timeoutInMilliseconds) {
    this.remoteEnabled = remoteEnabled;
    this.fallbackEnabled = fallbackEnabled;
    this.username = username;
    this.password = password;
    this.baseUri = baseUri;
    this.timeoutInMilliseconds = timeoutInMilliseconds;
  }

  public boolean isRemoteEnabled() {
    return remoteEnabled;
  }

  public Boolean getFallbackEnabled() {
    return fallbackEnabled;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getBaseUri() {
    return baseUri;
  }

  public Integer getTimeoutInMilliseconds() {
    return timeoutInMilliseconds;
  }
}

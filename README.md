# switchman-togglz-client
[![Build Status](https://api.travis-ci.org/ImmobilienScout24/switchman-togglz-client.svg?branch=master)](https://travis-ci.org/ImmobilienScout24/switchman-togglz-client)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.is24.common/switchman-togglz-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.is24.common/switchman-togglz-client/)

IS24-switchman togglz client that can be used to merge local feature state with remote feature state.

## What is this for?
If you have a lot of microservices and you want to be able to switch on a feature not on each service (instance?!) but on 
a environment (like testing/production stage). This might be better than enabling the feature in all microservices or even worse send the feature switch state in your internal
APIs.

This client uses the Togglz framework and enhances this with a remote feature repository stored in IS24-Switchman service.

If this is configured correctly, it works like this:
- You define feature switches in an enum locally
- Via name, this client merges the feature state with the remote state from IS24 Switchman
- You still can have a local Togglz console. If you change the feature state there, a cookie overriding remote state
will be written (for local development use)

## HowTo configure
To use this client in a Spring Boot project, you can do the following:

Add dependency to switchman-togglz-client to you pom.xml
```xml
    <dependency>
      <groupId>de.is24.common</groupId>
      <artifactId>switchman-togglz-client</artifactId>
      <version>1.1</version>
    </dependency>
```
You may also have to add Togglz dependencies since the library uses provided scope a lot.

Add a configuration for the client lib:
```java
@Configuration
@Import(FeatureSwitchRemoteClientConfig.class)
class FeatureSwitches {

  /* Read config values */
  @Value("${togglz.remote.enabled:false}")
  private boolean remoteEnabled;

  @Value("${togglz.remote.client.userName:user}")
  private String username;

  @Value("${togglz.remote.client.password:password}")
  private String password;

  @Value("${togglz.remote.client.baseUri:http://localhost:8080}")
  private String baseUri;

  @Value("${togglz.remote.client.fallbackEnabled:true}")
  private Boolean fallbackEnabled;

  @Value("${togglz.remote.client.timeout:5000}")
  private Integer timeoutInMilliseconds;
  
  /* Register beans to make client work */
  @Bean
  public TogglzRemoteClientSettings togglzRemoteClientSettings() {
    return new TogglzRemoteClientSettings(remoteEnabled,
      fallbackEnabled,
      username,
      password,
      baseUri,
      timeoutInMilliseconds);
  }

  @Bean
  public CookieHandler cookieHandler() {
    return new SimpleCookieHandler();
  }
  
  @Bean
  public FeatureProvider localFeatureProvider() {
    return new EnumBasedFeatureProvider(your.local.enum.FeatureSwitches.class);
  }

  /* Register local togglz console */
  @Bean
  public ServletRegistrationBean togglzConsoleServletRegistration() {
    final String togglzConsolePath = "/togglz/";
    return new ServletRegistrationBean(new TogglzConsoleServlet(), togglzConsolePath + "*");
  }

  /* Register togglz filter to enable cookie overwriting and ensure this gets processed before the standard togglz filter. */
    @Bean
    @Autowired
    public FilterRegistrationBean togglzCookieOverwritingFilter(TogglzCookieStateRepositoryFilter togglzCookieOverwritingFilter) {
      FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
      filterRegistrationBean.setFilter(togglzCookieOverwritingFilter);
      filterRegistrationBean.setOrder(0);
      return filterRegistrationBean;
    }

}
```

If you enabled CSRF protection, please also make sure you provide this property:
```
togglz.editpage.path=/togglz/edit
```
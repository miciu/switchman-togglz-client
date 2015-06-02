package de.is24.common.togglz.remote.config;

import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HystrixConfiguration;
import de.is24.common.togglz.filter.CookieOverwritingPersistentStateRepositoryFilter;
import de.is24.common.togglz.filter.cookie.CookieHandler;
import de.is24.common.togglz.provider.MergingFeatureProvider;
import de.is24.common.togglz.provider.RemoteFeatureSwitchProvider;
import de.is24.common.togglz.remote.RemoteFeatureStatesClient;
import de.is24.common.togglz.remote.api.serialization.HalEnabledObjectMapper;
import de.is24.common.togglz.remote.http.PreEmptiveAuthHttpRequestFactory;
import de.is24.common.togglz.repository.NoOpStateRepository;
import de.is24.common.togglz.repository.RemoteEnabledStateRepository;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.togglz.core.bootstrap.TogglzBootstrap;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.user.SingleUserProvider;
import org.togglz.core.user.UserProvider;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class FeatureSwitchRemoteClientConfig {
  @Autowired
  private TogglzRemoteClientSettings togglzRemoteClientSettings;

  @Value("${togglz.editpage.path:/togglz/edit}")
  private String togglzEditPagePath;

  @Autowired
  @Bean
  public TogglzBootstrap togglzBootstrap(final FeatureManager featureManager) {
    return () -> featureManager;
  }

  @Autowired
  @Bean
  public FeatureManager featureManager(FeatureProvider featureProvider,
                                       StateRepository cookieOverwritingPersistentStateRepository,
                                       UserProvider userProvider) {
    FeatureManagerBuilder featureManagerBuilder = new FeatureManagerBuilder();
    return featureManagerBuilder.name("remoteAccessFeatureManager")
      .featureProvider(featureProvider)
      .stateRepository(cookieOverwritingPersistentStateRepository)
      .userProvider(userProvider)
      .build();
  }

  @Autowired
  @Bean
  public StateRepository remoteEnabledStateRepository(RemoteFeatureStatesClient remoteFeatureStatesClient) {
    return new RemoteEnabledStateRepository(remoteFeatureStatesClient);
  }

  @Autowired
  @Bean
  public FeatureProvider featureProvider(RemoteFeatureStatesClient remoteFeatureStatesClient) {
    FeatureProvider localFeatureProvider = localFeatureProvider();
    return togglzRemoteClientSettings.isRemoteEnabled()
      ? new MergingFeatureProvider(localFeatureProvider, new RemoteFeatureSwitchProvider(remoteFeatureStatesClient))
      : localFeatureProvider;
  }

  @Bean
  public FeatureProvider localFeatureProvider() {
    throw new NotImplementedException(
      "\nTo use togglz you must define: \n\n @Bean\n" +
      "  public FeatureProvider localFeatureProvider() {\n" +
      "    return new EnumBasedFeatureProvider(YourFeatures.class);\n" +
      "  }");
  }

  @Bean
  public UserProvider userProvider() {
    return new SingleUserProvider("admin", true);
  }

  @Bean
  public CookieHandler cookieHander() {
    throw new NotImplementedException(
      "\nYou must define a CookieHandler in your configuration like so: \n\n @Bean\n" +
      "  public CookieHandler cookieHandler() {\n" +
      "    return new SimpleCookieHandler();\n" +
      "  }");
  }

  @Autowired
  @Bean
  public CookieOverwritingPersistentStateRepositoryFilter cookieOverwritingPersistentStateRepository(
    StateRepository remoteEnabledStateRepository, FeatureProvider featureProvider, CookieHandler cookieHandler) {
    CookieOverwritingPersistentStateRepositoryFilter repository = new CookieOverwritingPersistentStateRepositoryFilter(
      togglzRemoteClientSettings.isRemoteEnabled() ? remoteEnabledStateRepository : localStateRepository(),
      featureProvider,
      cookieHandler);
    repository.setTogglzEditPagePath(togglzEditPagePath);
    return repository;
  }

  @Bean
  public StateRepository localStateRepository() {
    return new NoOpStateRepository();
  }

  @Autowired
  @Bean
  public RemoteFeatureStatesClient remoteFeatureStatesClient(HateoasLinkProvider featureSwitchHateoasLinkProvider,
                                                             RestOperations remoteFeatureSwitchRemoteConfigurations) {
    return new RemoteFeatureStatesClient(remoteFeatureSwitchRemoteConfigurations,
      featureSwitchHateoasLinkProvider,
      togglzRemoteClientSettings.getBaseUri());
  }

  @Autowired
  @Bean
  public HateoasLinkProvider featureSwitchHateoasLinkProvider(RestOperations remoteFeatureSwitchRemoteConfigurations) {
    return new HateoasLinkProvider(remoteFeatureSwitchRemoteConfigurations);
  }

  @Bean
  public RestOperations remoteFeatureSwitchRemoteConfigurations() {
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    AuthScope authScope = new AuthScope(null, -1, null, null);
    credentialsProvider.setCredentials(authScope,
      new UsernamePasswordCredentials(togglzRemoteClientSettings.getUsername(),
        togglzRemoteClientSettings.getPassword()));

    HttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
    RestTemplate restTemplate = new RestTemplate(new PreEmptiveAuthHttpRequestFactory(httpClient));
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(1);
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    messageConverter.setObjectMapper(new HalEnabledObjectMapper());
    messageConverters.add(messageConverter);
    restTemplate.setMessageConverters(messageConverters);
    return restTemplate;
  }

  @Bean
  public HystrixConfiguration hystrixConfiguration() {
    return new HystrixConfiguration(togglzRemoteClientSettings.getFallbackEnabled(),
      togglzRemoteClientSettings.getTimeoutInMilliseconds());
  }


}

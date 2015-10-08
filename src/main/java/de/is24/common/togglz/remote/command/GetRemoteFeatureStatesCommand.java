package de.is24.common.togglz.remote.command;

import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hateoas.HateoasRequestEntity;
import de.is24.common.hystrix.HystrixConfiguration;
import de.is24.common.togglz.remote.api.RemoteFeatureState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import java.util.Collections;


public class GetRemoteFeatureStatesCommand
  extends AbstractFeatureStateRemoteCommand<Resources<Resource<RemoteFeatureState>>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRemoteFeatureStatesCommand.class);
  private final String remoteApiUri;

  public GetRemoteFeatureStatesCommand(HystrixConfiguration hysterixConfiguration, RestOperations restOperations,
                                       HateoasLinkProvider featureSwitchHateoasLinkProvider,
                                       String remoteConfigurationProviderUri) {
    super(hysterixConfiguration.getConfiguration(COMMAND_GROUP_KEY), restOperations, featureSwitchHateoasLinkProvider);
    this.remoteApiUri = remoteConfigurationProviderUri;
  }

  @Override
  protected Resources<Resource<RemoteFeatureState>> runCommand() throws Exception {
    Link linkToConfigurations = getLinkByName(remoteApiUri, RemoteFeatureState.REL).expand(
        Collections.singletonMap("size", Integer.MAX_VALUE)
    );

    ResponseEntity<Resources<Resource<RemoteFeatureState>>> responseEntity = restOperations.exchange(
      linkToConfigurations.getHref(),
      HttpMethod.GET,
      HateoasRequestEntity.requestEntity(),
      new ParameterizedTypeReference<Resources<Resource<RemoteFeatureState>>>() {
      });

    return responseEntity.getBody();
  }

  @Override
  protected Resources<Resource<RemoteFeatureState>> getFallback() {
    LOGGER.warn(
      "No feature states could be retrieved. Using empty remote configuration.",
      this.getFailedExecutionException());
    return new Resources<Resource<RemoteFeatureState>>((Iterable) Collections.emptyList(),
      (Iterable) Collections.emptyList());
  }
}

package de.is24.common.togglz.remote;

import de.is24.common.hateoas.HateoasLinkProvider;
import org.springframework.web.client.RestOperations;


public class FeatureSwitchRemoteClient {
  protected final HateoasLinkProvider featureSwitchHateoasLinkProvider;
  protected final String remoteServiceBaseUri;
  protected final RestOperations restOperations;

  public FeatureSwitchRemoteClient(RestOperations restOperations,
                                   HateoasLinkProvider hateoasLinkProvider,
                                   String remoteServiceBaseUri) {
    this.restOperations = restOperations;
    this.featureSwitchHateoasLinkProvider = hateoasLinkProvider;
    this.remoteServiceBaseUri = remoteServiceBaseUri;
  }
}

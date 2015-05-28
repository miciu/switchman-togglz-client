package de.is24.common.togglz.remote;

import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HystrixConfiguration;
import de.is24.common.togglz.remote.api.RemoteFeatureState;
import de.is24.common.togglz.remote.command.GetRemoteFeatureStatesCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.togglz.core.repository.FeatureState;
import java.util.ArrayList;
import java.util.List;


@Service
public class RemoteFeatureStatesClient extends FeatureSwitchRemoteClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(RemoteFeatureStatesClient.class);

  @Autowired
  private HystrixConfiguration hystrixConfiguration;

  public RemoteFeatureStatesClient(RestOperations restOperations,
                                   HateoasLinkProvider hateoasLinkProvider,
                                   String remoteServiceBaseUri) {
    super(restOperations, hateoasLinkProvider, remoteServiceBaseUri);
  }

  @Cacheable("remoteFeatureStates")
  public List<FeatureState> getRemoteFeatureStates() {
    LOGGER.info("Retrieving latest FeatureStates from remote service.");

    Resources<Resource<RemoteFeatureState>> remoteFeatureStateResources = new GetRemoteFeatureStatesCommand(
      hystrixConfiguration,
      restOperations,
      featureSwitchHateoasLinkProvider,
      remoteServiceBaseUri).execute();

    List<FeatureState> featureStates = new ArrayList<>();
    remoteFeatureStateResources.forEach(r -> {
      RemoteFeatureState remoteFeatureState = r.getContent();
      featureStates.add(RemoteFeatureState.toFeatureState(remoteFeatureState));
    });
    return featureStates;
  }

}

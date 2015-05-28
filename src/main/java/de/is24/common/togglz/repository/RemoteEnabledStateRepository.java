package de.is24.common.togglz.repository;

import de.is24.common.togglz.remote.RemoteFeatureStatesClient;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;


public class RemoteEnabledStateRepository implements StateRepository {
  private final RemoteFeatureStatesClient remoteFeatureStatesClient;

  public RemoteEnabledStateRepository(
    RemoteFeatureStatesClient remoteFeatureStatesClient) {
    this.remoteFeatureStatesClient = remoteFeatureStatesClient;
  }

  @Override
  public FeatureState getFeatureState(Feature feature) {
    return remoteFeatureStatesClient.getRemoteFeatureStates()
      .stream()
      .filter(s -> s.getFeature().name().equals(feature.name())).findFirst()
      .orElse(null);
  }

  @Override
  public void setFeatureState(FeatureState featureState) {
    //client side storing is not supported.
  }
}

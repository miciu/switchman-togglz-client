package de.is24.common.togglz.provider;

import de.is24.common.togglz.remote.RemoteFeatureStatesClient;
import de.is24.common.togglz.remote.api.RemoteFeature;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.spi.FeatureProvider;

import java.util.HashSet;
import java.util.Set;

public class RemoteFeatureSwitchProvider implements FeatureProvider {
  
  private final RemoteFeatureStatesClient remoteFeatureStatesClient;

  public RemoteFeatureSwitchProvider(RemoteFeatureStatesClient remoteFeatureStatesClient) {
    this.remoteFeatureStatesClient = remoteFeatureStatesClient;
  }

  @Override
  public Set<Feature> getFeatures() {
    Set<Feature> features = new HashSet<>();
    remoteFeatureStatesClient.getRemoteFeatureStates().forEach(s -> features.add(s.getFeature()));
    return features;
  }

  @Override
  public FeatureMetaData getMetaData(Feature feature) {
    if (feature instanceof RemoteFeature) {
      return (RemoteFeature)feature;
    }
    return null;
  }
}

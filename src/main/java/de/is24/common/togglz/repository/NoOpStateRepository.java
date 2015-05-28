package de.is24.common.togglz.repository;

import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;


public class NoOpStateRepository implements StateRepository {
  @Override
  public FeatureState getFeatureState(Feature feature) {
    return null;
  }

  @Override
  public void setFeatureState(FeatureState featureState) {
  }
}

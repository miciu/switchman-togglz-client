package de.is24.common.togglz.filter;

import de.is24.common.togglz.filter.cookie.CookieHandler;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.spi.FeatureProvider;


public class CookieOverwritingPersistentStateRepositoryFilter extends TooglzCookieStateRepositoryFilter {
  private final StateRepository persistentStateRepository;

  public CookieOverwritingPersistentStateRepositoryFilter(final StateRepository persistentStateRepository,
                                                          FeatureProvider featureProvider,
                                                          CookieHandler cookieHandler) {
    super(featureProvider, cookieHandler);
    this.persistentStateRepository = persistentStateRepository;
  }

  @Override
  public FeatureState getFeatureState(final Feature feature) {
    FeatureState featureState;
    try {
      featureState = super.getFeatureState(feature);
      if (featureState == null) {
        featureState = getFeatureStateFromPersistentStateRepository(feature);
      }
    } catch (NullPointerException npe) {
      featureState = getFeatureStateFromPersistentStateRepository(feature);
    }

    return featureState;
  }

  private FeatureState getFeatureStateFromPersistentStateRepository(Feature feature) {
    return this.persistentStateRepository.getFeatureState(feature);
  }
}

package de.is24.common.togglz.repository;

import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import java.util.Map;


public class ThreadLocalStateRepository implements StateRepository {
  private final ThreadLocal<Map<String, FeatureState>> featureMap = new InheritableThreadLocal<>();
  private final ThreadLocal<Boolean> modificationState = new InheritableThreadLocal<>();

  @Override
  public FeatureState getFeatureState(Feature feature) {
    return featureMap.get().get(feature.name());
  }

  @Override
  public void setFeatureState(FeatureState featureState) {
    featureMap.get().put(featureState.getFeature().name(), featureState);
    modificationState.set(true);
  }

  protected void setFeatureMap(Map<String, FeatureState> featureStateMap) {
    featureMap.set(featureStateMap);
  }

  protected Map<String, FeatureState> getFeatureMap() {
    return featureMap.get();
  }

  protected void setModificationState(Boolean modified) {
    modificationState.set(modified);
  }

  protected Boolean getModificationState() {
    return modificationState.get();
  }

  protected void resetThreadLocals() {
    featureMap.remove();
    modificationState.remove();
  }
}

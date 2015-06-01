package de.is24.common.togglz.repository;

import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import java.util.HashMap;
import java.util.Map;


public class ThreadLocalStateRepository implements StateRepository {
  private final ThreadLocal<Map<String, FeatureState>> threadLocalFeatureMap = new InheritableThreadLocal<>();
  private final ThreadLocal<Boolean> modificationState = new InheritableThreadLocal<>();

  @Override
  public FeatureState getFeatureState(Feature feature) {
    return getOrCreateMapForCurrentThread().get(feature.name());
  }

  @Override
  public void setFeatureState(FeatureState featureState) {
    Map<String, FeatureState> featureMap = getOrCreateMapForCurrentThread();
    featureMap.put(featureState.getFeature().name(), featureState);
    threadLocalFeatureMap.set(featureMap);
    modificationState.set(true);
  }

  protected void setThreadLocalFeatureMap(Map<String, FeatureState> featureStateMap) {
    threadLocalFeatureMap.set(featureStateMap);
  }

  protected Map<String, FeatureState> getThreadLocalFeatureMap() {
    return threadLocalFeatureMap.get();
  }

  protected void setModificationState(Boolean modified) {
    modificationState.set(modified);
  }

  protected Boolean getModificationState() {
    return modificationState.get();
  }

  protected void resetThreadLocals() {
    threadLocalFeatureMap.remove();
    modificationState.remove();
  }

  private Map<String, FeatureState> getOrCreateMapForCurrentThread() {
    Map<String, FeatureState> featureStatesMap = threadLocalFeatureMap.get();
    if (featureStatesMap == null) {
      featureStatesMap = new HashMap<>(1);
    }
    return featureStatesMap;
  }
}

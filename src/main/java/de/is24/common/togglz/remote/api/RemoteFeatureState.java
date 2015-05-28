package de.is24.common.togglz.remote.api;

import lombok.Data;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.repository.FeatureState;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@Data
public class RemoteFeatureState implements Serializable {
  public static final String REL = "featureStates";
  @NotNull
  private RemoteFeature feature;
  @NotNull
  private Boolean enabled;

  private String strategyId;

  private Map<String, String> parameters = new HashMap<>();

  public static FeatureState toFeatureState(RemoteFeatureState remoteFeatureState) {
    FeatureState featureState = new FeatureState(remoteFeatureState.getFeature(), remoteFeatureState.getEnabled());
    featureState.setStrategyId(remoteFeatureState.getStrategyId());
    remoteFeatureState.getParameters().forEach((k,v) -> featureState.setParameter(k, v));    
    return featureState;
  }
  
  public static RemoteFeatureState from(FeatureState featureState) {
    RemoteFeatureState remoteFeatureState = new RemoteFeatureState();
    RemoteFeature feature = new RemoteFeature();
    Feature featureFromGivenState = featureState.getFeature();
    feature.setName(featureFromGivenState.name());
    
    if(featureFromGivenState instanceof FeatureMetaData) {
      FeatureMetaData metaData = (FeatureMetaData) featureFromGivenState;
      feature.setLabel(metaData.getLabel());
      feature.setEnabledByDefault(metaData.isEnabledByDefault());
    }   
        
    remoteFeatureState.setFeature(feature);
    remoteFeatureState.setEnabled(featureState.isEnabled());
    remoteFeatureState.setStrategyId(featureState.getStrategyId());
    
    remoteFeatureState.setParameters(new HashMap<>(2));    
    featureState.getParameterMap().forEach((k,v) -> remoteFeatureState.getParameters().put(k, v));
    return remoteFeatureState;
  }

}

package de.is24.common.togglz.provider;

import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.spi.FeatureProvider;

import java.util.*;

/**
 * This FeatureProvider (FP) takes n other FPs and merges their Features by name
 */
public class MergingFeatureProvider implements FeatureProvider {
  public static final Comparator<Feature> FEATURE_COMPARATOR = new Comparator<Feature>() {
    @Override
    public int compare(Feature a, Feature b) {
      return a.name().compareTo(b.name());
    }
  };
  private final List<FeatureProvider> featureProviders;

  public MergingFeatureProvider(FeatureProvider... featureProviders) {
    this.featureProviders = Arrays.asList(featureProviders);
  }

  @Override
  public Set<Feature> getFeatures() {
    Set<Feature> features = new TreeSet<Feature>(FEATURE_COMPARATOR);

    featureProviders.stream().forEachOrdered(featureProvider -> {
      features.addAll(featureProvider.getFeatures());
    });
    
    return features;
  }

  @Override
  public FeatureMetaData getMetaData(Feature feature) {
    return null;
  }
}

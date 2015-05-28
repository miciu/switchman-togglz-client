package de.is24.common.togglz.provider;


import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.togglz.core.Feature;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;

public class MergingFeatureProviderTest {
  enum Features1 implements Feature {
    A, B
  }

  enum Features2 implements Feature {
    C, D
  }

  enum Features3 implements Feature {
    A, X
  }

  @Test
  public void shouldEnumerateAllFeaturesFromChildFeatureProvidersWithNonCollidingNames() {
    EnumBasedFeatureProvider featureProviderA = new EnumBasedFeatureProvider(Features1.class);
    EnumBasedFeatureProvider featureProviderB = new EnumBasedFeatureProvider(Features2.class);

    FeatureProvider mergingFeatureProvider = new MergingFeatureProvider(featureProviderA, featureProviderB);

    MatcherAssert.assertThat(namesOf(mergingFeatureProvider.getFeatures()), is(new TreeSet<String>(Arrays.asList("A", "B", "C", "D"))));
  }

  @Test
  public void shouldEnumerateAllFeaturesFromChildFeatureProvidersWithCollidingNames() {
    EnumBasedFeatureProvider featureProviderA = new EnumBasedFeatureProvider(Features1.class);
    EnumBasedFeatureProvider featureProviderB = new EnumBasedFeatureProvider(Features3.class);

    FeatureProvider mergingFeatureProvider = new MergingFeatureProvider(featureProviderA, featureProviderB);

    MatcherAssert.assertThat(namesOf(mergingFeatureProvider.getFeatures()), is(new TreeSet<String>(Arrays.asList("A", "B", "X"))));
  }

  private SortedSet<String> namesOf(Collection<Feature> features) {
    ArrayList<String> items = features.stream().map(feature -> feature.name()).collect(Collectors.toCollection(ArrayList<String>::new));
    return new TreeSet<String>(items);
  }
}

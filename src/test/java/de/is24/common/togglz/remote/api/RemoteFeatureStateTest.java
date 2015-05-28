package de.is24.common.togglz.remote.api;

import org.junit.Test;
import org.togglz.core.Feature;
import org.togglz.core.activation.ReleaseDateActivationStrategy;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.util.NamedFeature;
import java.util.HashMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class RemoteFeatureStateTest {
  public static final String TEST = "test";
  public static final String DATE_PARAM = "2012-12-31";
  public static final String TIME_PARAM = "14:15:00";
  public static final String STRATEGY_ID = ReleaseDateActivationStrategy.ID;
  private static final String TEST_LABEL = "Some label";
  private static final Boolean DEFAULT_ENABLED = true;

  private FeatureState featureState;
  private RemoteFeatureState remoteFeatureState;

  @Test
  public void transformsFeatureStateToRemoteFeatureState() {
    givenFeatureState(new NamedFeature(TEST));
    whenRemoteFeatureStateIsCreated();
    thenRemoteFeatureStateMatchesFeatureState();
  }

  @Test
  public void transformsRemoteFeatureStateToFeatureState() {
    givenRemoteFeatureState();
    whenFeatureStateIsCreated();
    thenFeatureStateMatchesRemoteFeatureState();
  }

  @Test
  public void transformsRemoteFeatureStateToRemoteFeatureStateWhenGiven() {
    givenFeatureStateWithRemoteFeature();
    whenRemoteFeatureStateIsCreated();
    thenRemoteFeatureContainsMetaData();
  }

  private void thenRemoteFeatureContainsMetaData() {
    assertThat(remoteFeatureState.getFeature().getLabel(), is(TEST_LABEL));
    assertThat(remoteFeatureState.getFeature().getEnabledByDefault(), is(DEFAULT_ENABLED));
  }

  private void givenRemoteFeatureState() {
    remoteFeatureState = new RemoteFeatureState();

    RemoteFeature feature = givenRemoteFeature();

    remoteFeatureState.setFeature(feature);
    remoteFeatureState.setEnabled(true);

    remoteFeatureState.setStrategyId(STRATEGY_ID);

    HashMap<String, String> parameters = new HashMap<>(2);
    parameters.put(ReleaseDateActivationStrategy.PARAM_DATE, DATE_PARAM);
    parameters.put(ReleaseDateActivationStrategy.PARAM_TIME, TIME_PARAM);
    remoteFeatureState.setParameters(parameters);
  }

  private void givenFeatureStateWithRemoteFeature() {
    givenFeatureState(givenRemoteFeature());
  }

  private RemoteFeature givenRemoteFeature() {
    RemoteFeature feature = new RemoteFeature();
    feature.setName(TEST);
    feature.setLabel(TEST_LABEL);
    feature.setEnabledByDefault(DEFAULT_ENABLED);
    return feature;
  }

  private void givenFeatureState(Feature feature) {
    featureState = new FeatureState(feature);
    featureState.setEnabled(true);
    featureState.setStrategyId(STRATEGY_ID);
    featureState.setParameter(ReleaseDateActivationStrategy.PARAM_DATE, DATE_PARAM);
    featureState.setParameter(ReleaseDateActivationStrategy.PARAM_TIME, TIME_PARAM);
  }

  private void whenRemoteFeatureStateIsCreated() {
    remoteFeatureState = RemoteFeatureState.from(featureState);
  }

  private void whenFeatureStateIsCreated() {
    featureState = RemoteFeatureState.toFeatureState(remoteFeatureState);
  }

  private void thenRemoteFeatureStateMatchesFeatureState() {
    assertThat(remoteFeatureState.getFeature().getName(), is(TEST));
    assertThat(remoteFeatureState.getEnabled(), is(true));
    assertThat(remoteFeatureState.getStrategyId(), is(STRATEGY_ID));
    assertThat(remoteFeatureState.getParameters().get(ReleaseDateActivationStrategy.PARAM_DATE), is(DATE_PARAM));
    assertThat(remoteFeatureState.getParameters().get(ReleaseDateActivationStrategy.PARAM_TIME), is(TIME_PARAM));
  }

  private void thenFeatureStateMatchesRemoteFeatureState() {
    assertThat(((RemoteFeature) featureState.getFeature()).name(), is(TEST));
    assertThat(featureState.isEnabled(), is(true));
    assertThat(featureState.getStrategyId(), is(STRATEGY_ID));
    assertThat(featureState.getParameterMap().get(ReleaseDateActivationStrategy.PARAM_DATE), is(DATE_PARAM));
    assertThat(featureState.getParameterMap().get(ReleaseDateActivationStrategy.PARAM_TIME), is(TIME_PARAM));
  }

}

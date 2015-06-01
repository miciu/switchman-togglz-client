package de.is24.common.togglz.repository;

import org.junit.Test;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;


public class ThreadLocalStateRepositoryTest {
  private static final Feature FEATURE = () -> "FEATURE_NAME";
  private FeatureState featureState;
  private ThreadLocalStateRepository threadLocalStateRepository = new ThreadLocalStateRepository();

  @Test
  public void shouldPutFeatureStateInMap() {
    givenFeatureState();
    whenSettingFeatureState();
    thenFeatureStateCanBeRetrieved();
  }

  @Test
  public void shouldHideFeatureStateInOtherThread() throws InterruptedException {
    givenFeatureState();
    whenStateIsSetInDifferentThread();
    thenFeatureStateCannotBeRetrieved();
  }

  private void givenFeatureState() {
    featureState = new FeatureState(FEATURE);
  }

  private void whenSettingFeatureState() {
    threadLocalStateRepository.setFeatureState(featureState);
  }

  private void whenStateIsSetInDifferentThread() throws InterruptedException {
    Runnable runnable = () -> {
      whenSettingFeatureState();
      thenFeatureStateCanBeRetrieved();
    };
    Thread thread = new Thread(runnable);
    thread.start();
    while (thread.isAlive()) {
      Thread.sleep(100);
    }
  }

  private void thenFeatureStateCanBeRetrieved() {
    assertThat(threadLocalStateRepository.getFeatureState(FEATURE), is(featureState));
  }

  private void thenFeatureStateCannotBeRetrieved() {
    assertThat(threadLocalStateRepository.getFeatureState(FEATURE), is(nullValue()));
  }

}

package de.is24.common.togglz.remote.api;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.togglz.core.Feature;
import org.togglz.core.metadata.FeatureGroup;
import org.togglz.core.metadata.FeatureMetaData;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


@Data
public class RemoteFeature implements Feature, FeatureMetaData, Serializable {
  @NotEmpty
  @NotNull
  private String name;

  private String label;

  private Boolean enabledByDefault = false;

  @Override
  public String name() {
    return name;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public boolean isEnabledByDefault() {
    return enabledByDefault;
  }

  @Override
  public Set<FeatureGroup> getGroups() {
    // currently not supported by this implementation
    return Collections.emptySet();
  }

  @Override
  public Map<String, String> getAttributes() {
    //currently not supported by this implementation
    return Collections.emptyMap();
  }
}

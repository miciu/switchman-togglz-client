package de.is24.common.togglz.remote.command;

import de.is24.common.hateoas.HateoasLinkProvider;
import de.is24.common.hystrix.HateoasRemoteCommand;
import org.springframework.web.client.RestOperations;


public abstract class AbstractFeatureStateRemoteCommand<T> extends HateoasRemoteCommand<T> {
  protected static final String COMMAND_GROUP_KEY = "FeatureStateRemoteGroup";

  public AbstractFeatureStateRemoteCommand(Setter setter, RestOperations restOperations,
                                           HateoasLinkProvider hateoasLinkProvider) {
    super(setter, restOperations, hateoasLinkProvider);
  }
}

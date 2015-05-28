package de.is24.common.togglz.remote.api.serialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.hateoas.hal.Jackson2HalModule;


public class HalEnabledObjectMapper extends ObjectMapper {
  public HalEnabledObjectMapper() {
    Jackson2HalModule module = new Jackson2HalModule();
    module.addSerializer(DateTime.class, new DateTimeSerializer());
    module.addDeserializer(DateTime.class, new DateTimeDeserializer());
    registerModule(module);
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

}

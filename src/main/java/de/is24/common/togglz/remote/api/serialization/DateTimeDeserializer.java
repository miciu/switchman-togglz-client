package de.is24.common.togglz.remote.api.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.IOException;


public class DateTimeDeserializer extends JsonDeserializer<DateTime> {
  public static final String SERIALIZATION_FORMAT = "yyyy-MM-dd'T'HH:mm";

  private static DateTimeFormatter formatter = DateTimeFormat.forPattern(SERIALIZATION_FORMAT);

  @Override
  public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                       throws IOException, JsonProcessingException {
    return formatter.parseDateTime(jsonParser.getValueAsString());
  }
}

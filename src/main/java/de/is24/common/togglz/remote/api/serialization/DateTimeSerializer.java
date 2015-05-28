package de.is24.common.togglz.remote.api.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.IOException;


public class DateTimeSerializer extends JsonSerializer<DateTime> {
  public static final String SERIALIZATION_FORMAT = "yyyy-MM-dd'T'HH:mm";

  private static DateTimeFormatter formatter = DateTimeFormat.forPattern(SERIALIZATION_FORMAT);

  @Override
  public void serialize(DateTime value, JsonGenerator gen,
                        SerializerProvider arg2) throws IOException, JsonProcessingException {
    gen.writeString(formatter.print(value));
  }
}

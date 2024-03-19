package net.metrosystems.inventoryaggregator.exception.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

public class EntityNotFoundException extends ApiErrorAwareException {

  public EntityNotFoundException(Class<?> clazz) {
    super(generateMessage(clazz));
  }

  public EntityNotFoundException(Class<?> clazz, String k1, String v1) {
    super(generateMessage(clazz, k1, v1));
  }

  public EntityNotFoundException(Class<?> clazz, String k1, String v1, String k2, String v2) {
    super(generateMessage(clazz, k1, v1, k2, v2));
  }

  public EntityNotFoundException(Class<?> clazz, String k1, String v1, String k2, String v2, String k3, String v3) {
    super(generateMessage(clazz, k1, v1, k2, v2, k3, v3));
  }

  public EntityNotFoundException(Class<?> clazz, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4) {
    super(generateMessage(clazz, k1, v1, k2, v2, k3, v3, k4, v4));
  }

  public EntityNotFoundException(Class<?> clazz, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5) {
    super(generateMessage(clazz, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5));
  }

  public EntityNotFoundException(Class<?> clazz, String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String... args) {
    super(generateMessage(clazz, Stream.concat(Arrays.stream(new String[]{k1, v1, k2, v2, k3, v3, k4, v4, k5, v5}), Arrays.stream(args)).toArray(String[]::new)));
  }

  private static String generateMessage(Class<?> clazz, String... searchParamsMap) {
    return StringUtils.capitalize(clazz.getSimpleName()) + " was not found for parameters " + toMap(searchParamsMap);
  }

  private static Map<String, String> toMap(String... entries) {
    if(entries == null || entries.length == 0) {
      return Map.of();
    }

    if (entries.length % 2 == 1) throw new IllegalArgumentException("Invalid entries");
    return IntStream.range(0, entries.length / 2)
        .map(i -> i * 2)
        .collect(
            HashMap::new,
            (m, i) -> m.put(entries[i], entries[i + 1]),
            Map::putAll);
  }

  @Override
  public HttpStatus getHttpStatus() {
    return HttpStatus.NOT_FOUND;
  }
}

package net.metrosystems.inventoryaggregator.exception;

import static org.springframework.util.StringUtils.hasText;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomainSupplier {

  public static final String UNKNOWN = "UNKNOWN";

  @Value("${app.vertical:#{null}}")
  private final String vertical;

  @Value("${app.service:#{null}}")
  private final String service;

  @Value("${spring.application.name:#{null}}")
  private final String applicationName;

  String getDomain() {
    if (hasText(vertical) && hasText(service)) {
      return vertical + "." + service;
    }

    if (hasText(applicationName)) {
      log.warn(
          "Could not resolve application domain, fallback to applicationName. "
              + "Consider to configure application properly!");
      return applicationName;
    }

    log.warn(
        "Could not resolve application domain or fallback to applicationName. "
            + "Consider to configure application properly!");

    return UNKNOWN;
  }
}

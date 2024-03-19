package net.metrosystems.inventoryaggregator.articleid.service.it;

import static org.assertj.core.api.Assertions.assertThat;

import net.metrosystems.inventoryaggregator.exception.model.ApiError;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@ITTestDefinition
class RestApiErrorResponseTest {

  @Autowired TestRestTemplate restTemplate;

  @LocalServerPort int port;

  @Test
  @Disabled
  void shouldHideSensibleInformation() {
    // Given
    var path =
        "/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}";
    var values = new Object[] {"countryCode", "salesline", 123, 456, 789, 1, 1};

    // When
    var entity =
        restTemplate.getForEntity("http://localhost:" + port + path, ApiError.class, values);

    // Then
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

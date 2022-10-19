package net.metrosystems.inventoryaggregator.articleid.service.it;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import net.metrosystems.inventory.common.apierror.model.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ITTestDefinition
class RestApiErrorResponseTest {

    @Autowired
    TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Test
    void shouldHideSensibleInformation() {
        //Given
        var path = "/inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles/ean/{ean}";
        var values = new Object[] {"countryCode", "salesline", 123, 456, 789};

        //When
        var entity = restTemplate.getForEntity("http://localhost:" + port + path, ApiError.class, values);

        //Then
        assertThat(entity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(entity.getBody().getMessage()).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(entity.getBody().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(LocalDateTime.parse(entity.getBody().getTimestamp(), ISO_DATE_TIME))
                .isCloseTo(now(), within(1, ChronoUnit.SECONDS));
    }
}

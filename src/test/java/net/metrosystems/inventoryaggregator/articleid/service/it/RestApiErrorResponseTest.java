package net.metrosystems.inventoryaggregator.articleid.service.it;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;
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
        var entity = restTemplate.getForEntity("http://localhost:" + port + path, Map.class, values);

        //Then
        assertThat(entity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(entity.getBody().get("message")).isEqualTo("Check inventoryaggregator_service's logs!");
        assertThat(entity.getBody().get("error")).isEqualTo(INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(entity.getBody().get("status")).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(entity.getBody().get("timestamp")).isNotNull();
        assertThat(entity.getBody().get("path")).isNotNull();
        assertThat(entity.getBody().size()).isEqualTo(5);
    }
}

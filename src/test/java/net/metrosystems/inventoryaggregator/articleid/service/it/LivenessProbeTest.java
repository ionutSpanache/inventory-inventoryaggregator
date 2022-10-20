package net.metrosystems.inventoryaggregator.articleid.service.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@ITTestDefinition
class LivenessProbeTest {

    @Autowired
    TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Test
    void shouldHideSensibleInformation() {
        //Given
        var path = "/.well-known/live";

        //When
        var entity = restTemplate.getForEntity("http://localhost:" + port + path, Void.class);

        //Then
        assertThat(entity.getStatusCode()).isEqualTo(OK);
    }
}

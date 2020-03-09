package inventory.aggregator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class OpenApiSwaggerConfig {
    @Value("${app.service}")
    private String serviceName;

    @Value("${spring.profiles.active}")
    private String profile;
    @Bean
    public OpenAPI api() {
        Map<String,Object> extensions = new HashMap<>();
        extensions.put("x-keywords", "Store inventory aggregator");
        extensions.put("x-related-masterdata", "article");
        extensions.put("x-solution", "Inventory");
        extensions.put("x-scope", "metro");
        extensions.put("x-activated-countries", Arrays.asList("ALL"));
        extensions.put("x-git-url", "https://git.metrosystems.net/storeinventory/inventoryaggregator");
        License license = new License();
        license.setName("Apache License Version 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0");
        Contact contact = new Contact();
        contact.setName("Store Inventory Team");
        contact.setEmail("Metrosystemsrobuk-devops-mmsacl@metrosystems.net");
        contact.addExtension("x-teams", "Inventory");

        var openApi = new OpenAPI()
                .info(new Info()
                        .title("Store Inventory aggregator service")
                        .description("REST API for Store Inventory article information")
                        .version("1.0.0")
                        .termsOfService("http://terms-of-services.url")
                        .license(license)
                        .contact(contact)
                        .extensions(extensions)
                );

        if (!profile.equalsIgnoreCase("local")) {
            String url;

            if (profile.equalsIgnoreCase("prod"))
                url = "https://inventory.metronom.com";
            else
                url = "https://inventory-" + profile + ".metronom.com";

            var server = new Server();
            server.setUrl(url);
            openApi.addServersItem(server);
        }

        return openApi;
    }

}

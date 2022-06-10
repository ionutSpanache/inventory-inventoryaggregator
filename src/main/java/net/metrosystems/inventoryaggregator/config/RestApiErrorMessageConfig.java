package net.metrosystems.inventoryaggregator.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

@Configuration
class RestApiErrorMessageConfig {

    /* TODO: since spring boot 2.3 there's server.error.include-message property that can be used */
    @Bean
    ErrorAttributes errorAttributes(@Value("${spring.application.name}") final String applicationName) {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions errorAttributeOptions) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, errorAttributeOptions);
                errorAttributes.put("message", "Check " + applicationName + "'s logs!");
                return errorAttributes;
            }
        };
    }
}

package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URI;

@Configuration
@Data
@Component
@ConfigurationProperties(prefix = "article-cache-aaa")
public class ConfigArticleCacheAAA {
    private URI host;
    private String general;
    private String username;
    private String password;
    private String from;
    private String generalQueryParam;


}

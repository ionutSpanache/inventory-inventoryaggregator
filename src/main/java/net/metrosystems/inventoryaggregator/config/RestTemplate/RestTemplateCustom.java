package net.metrosystems.inventoryaggregator.config.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import net.metrosystems.inventoryaggregator.articleId.client.article_cache_aaa.config.ConfigArticleCacheAAA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

@Data
@Service
public class RestTemplateCustom {

    private RestTemplate restTemplate;

    private ConfigArticleCacheAAA configArticleCacheAAA;

    @Autowired
    public RestTemplateCustom(ConfigArticleCacheAAA configArticleCacheAAA, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.configArticleCacheAAA = configArticleCacheAAA;
        addInterceptorToRestTemplate();


    }

    private void addInterceptorToRestTemplate() {
        List<ClientHttpRequestInterceptor> interceptors = this.restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new TraceInformationRestTemplateAgentInterceptor());
        interceptors.add(new BasicAuthInterceptor(configArticleCacheAAA));
        this.restTemplate.setInterceptors(interceptors);
    }

    public Object getForObject(String url, Class<?> returnType, Boolean sendDecodedURL) {
        if (sendDecodedURL) {
            return this.restTemplate.getForObject(UriUtils.decode(url, "UTF-8"), returnType);
        } else {
            return this.restTemplate.getForObject(url, returnType);
        }

    }

    public Object getForObject(String url, Class<?> returnType) {
        return this.getForObject(url, returnType, false);
    }

    public void delete(String url) {
        this.restTemplate.delete(url);
    }

    public Object postForObject(String url, Object newValue, Class<?> returnType) {
        return this.postForObject(url, newValue, returnType, false);
    }

    public Object postForObject(String url, Object newValue, Class<?> returnType, Boolean sendDecodedURL) {

        if (sendDecodedURL) {
            return restTemplate.postForObject(UriUtils.decode(url, "UTF-8"), newValue, returnType);
        } else {
            return restTemplate.postForObject(url, newValue, returnType);
        }


    }


}

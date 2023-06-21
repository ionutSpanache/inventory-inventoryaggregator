package net.metrosystems.inventoryaggregator.config.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.config.ConfigArticleCacheAAA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.ClientHttpRequestFactory;
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

    public Object postForObject(String url, Object newValue, Class<?> returnType, Boolean sendDecodedURL) {

        if (sendDecodedURL!=null && sendDecodedURL.booleanValue()) {
            return restTemplate.postForObject(UriUtils.decode(url, "UTF-8"), newValue, returnType);
        } else {
            return restTemplate.postForObject(url, newValue, returnType);
        }


    }


}

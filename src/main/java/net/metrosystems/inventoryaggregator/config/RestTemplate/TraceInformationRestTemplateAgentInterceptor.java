package net.metrosystems.inventoryaggregator.config.RestTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;


@Order(1)
public class TraceInformationRestTemplateAgentInterceptor implements ClientHttpRequestInterceptor {


    @Autowired
    HTTPHeaderConfig httpHeaderConfig;

    public TraceInformationRestTemplateAgentInterceptor() {
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    )
            throws IOException {
        httpHeaderConfig = new HTTPHeaderConfig();
        request.getHeaders().remove("Content-Type");
        request.getHeaders().remove("Content-Length");
        request.getHeaders().remove("Accept");
        request.getHeaders().remove("Accept-Charset");
        request.getHeaders().addAll(httpHeaderConfig.buildHttpHeaders().buildAddTraceInformation().getHeaders());
        return execution.execute(request, body);
    }
}

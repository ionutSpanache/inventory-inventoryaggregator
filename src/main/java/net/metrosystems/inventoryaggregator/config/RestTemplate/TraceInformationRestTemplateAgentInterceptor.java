package net.metrosystems.inventoryaggregator.config.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
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

        synchronized (this) {
            httpHeaderConfig = new HTTPHeaderConfig();
            request.getHeaders().remove("Content-Type");
            request.getHeaders().remove("Content-Length");
            request.getHeaders().remove("Accept");
            request.getHeaders().remove("Accept-Charset");
//        request.getHeaders().addAll(httpHeaderConfig.buildHttpHeaders().buildAddTraceInformation().getHeaders());
//        request.getHeaders().addAll(httpHeaderConfig.buildHttpHeaders().getHeaders());
            initHeader(request.getHeaders());
        }
        return execution.execute(request, body);
    }

    private void initHeader(HttpHeaders headers) {
        headers.add("X-Correlation-ID", MDC.get("X-Correlation-ID"));
        headers.add("Flow", MDC.get("Flow"));
        headers.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
        List<MediaType> acceptMediaTypeList = new ArrayList<>();
        acceptMediaTypeList.add(MediaType.valueOf("application/json;charset=UTF-8"));
        headers.setAccept(acceptMediaTypeList);
    }
}

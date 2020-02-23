package inventory.aggregator.config.RestTemplate;

import inventory.aggregator.config.aspect.TraceInformation;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component
public class HTTPHeaderConfig {

    private HttpHeaders headers;

    public HTTPHeaderConfig() {
        initHeader();
    }

    public HTTPHeaderConfig buildHttpHeaders() {
        initHeader();
        return this;
    }

    public HTTPHeaderConfig buildAddTraceInformation() {
        MultiValueMap<String, String> traceInformation = new LinkedMultiValueMap<>();
        traceInformation.setAll(TraceInformation.getTraceInformationForComunication());
        headers.addAll(traceInformation);
        return this;
    }

    private void initHeader() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
        List<MediaType> acceptMediaTypeList = new ArrayList<>();
        acceptMediaTypeList.add(MediaType.valueOf("application/json;charset=UTF-8"));
        headers.setAccept(acceptMediaTypeList);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public void addHeaderParam(String param, String value) {
        this.headers.add(param, value);
    }

    public void addHeaderParams(MultiValueMap<String, String> value) {
        this.headers.addAll(value);
    }

    public void resetHeaders() {
        initHeader();
    }

    public void addTraceInforamtion() {
        MultiValueMap<String, String> traceInformation = new LinkedMultiValueMap<>();
        traceInformation.setAll(TraceInformation.getTraceInformationForComunication());
        headers.addAll(traceInformation);

    }

    public HttpServletResponse addHeadersToResponse(HttpServletResponse response) {
        for (String header : headers.keySet()) {
            response.setHeader(header, headers.getFirst(header));
        }
        return response;
    }

    public String addBasicAuth(String username, String password) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);

        return authHeader;
    }


}

package net.metrosystems.inventoryaggregator.config.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class HTTPHeaderConfig {

  public HTTPHeaderConfig() {
    initHeader();
  }

  private void initHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Correlation-ID", MDC.get("X-Correlation-ID"));
    headers.add("Flow", MDC.get("Flow"));
    headers.setContentType(MediaType.valueOf("application/json;charset=UTF-8"));
    List<MediaType> acceptMediaTypeList = new ArrayList<>();
    acceptMediaTypeList.add(MediaType.valueOf("application/json;charset=UTF-8"));
    headers.setAccept(acceptMediaTypeList);
    headers.addIfAbsent(HttpHeaders.ACCEPT_ENCODING, "gzip");

  }

  public String addBasicAuth(String username, String password) {
    String auth = username + ":" + password;
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));

    return  "Basic " + new String(encodedAuth);
  }
}

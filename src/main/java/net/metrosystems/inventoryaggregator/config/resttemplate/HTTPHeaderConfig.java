package net.metrosystems.inventoryaggregator.config.resttemplate;

import java.util.ArrayList;
import java.util.List;

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
    headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");

  }
}

package net.metrosystems.inventoryaggregator.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Scanner;
import org.springframework.stereotype.Component;

@Component
public class JsonHelper {
  private ObjectMapper objectMapper = new ObjectMapper();

  public <T> T fromJsonString(String content, Class<T> cls) throws IOException {
    return this.objectMapper.readValue(content, cls);
  }

  public <T> String toJson(T value) throws JsonProcessingException {
    return this.objectMapper.writeValueAsString(value);
  }

  public <T> T fromJson(String contentAsString, TypeReference<T> type) throws IOException {
    return this.objectMapper.readValue(contentAsString, type);
  }

  public String getJsonFileContent(Scanner sc) {
    StringBuilder jsonFileContent = new StringBuilder();
    while (sc.hasNextLine()) {
      jsonFileContent.append(sc.nextLine());
    }
    return jsonFileContent.toString();
  }

  public JsonNode getJsonNode(String content) throws IOException {
    return this.objectMapper.readTree(content);
  }
}

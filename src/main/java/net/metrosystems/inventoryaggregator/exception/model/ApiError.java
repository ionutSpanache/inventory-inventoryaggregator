package net.metrosystems.inventoryaggregator.exception.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ApiError {
  @JsonProperty("timestamp")
  private String timestamp;

  @JsonProperty("status")
  private Integer status;

  @JsonProperty("domain")
  private String domain;

  @JsonProperty("message")
  private String message;

  @JsonProperty("debugMessage")
  private String debugMessage;

  @JsonProperty("messageId")
  private String messageId;

  @JsonProperty("messageVariables")
  private @Valid Map<String, String> messageVariables = null;

  @JsonProperty("subErrors")
  private @Valid List<ApiError> subErrors = null;
}

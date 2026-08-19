package net.metrosystems.inventoryaggregator.articlestoremasterdata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AclStoreMasterData {
  private Integer articleNumber;
  private Integer variantNumber;
  private Integer bundleNumber;
  private Integer departmentNumber;
  private String priorityAssortmentCode;
  private String shelfEdgeTicketType;
  private Integer leadTime;
  private Integer orderProposalBlockingCode;
  private String departmentName;
  private String labelType;
}

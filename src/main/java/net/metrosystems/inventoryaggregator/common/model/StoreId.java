package net.metrosystems.inventoryaggregator.common.model;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class StoreId {

    private String countryCode;
    private String salesLine;
    private Integer storeNumber;

    public StoreId(String countryCode, String salesLine, Integer storeNumber) {
        this.countryCode = escapeHtml4(countryCode);
        this.salesLine = escapeHtml4(salesLine);
        this.storeNumber = storeNumber;
    }
}

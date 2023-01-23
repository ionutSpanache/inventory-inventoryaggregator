package net.metrosystems.inventoryaggregator.articleattributes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class MovieLocationResponse {
    private String country;
    private String salesLine;
    private String locationID;
    List<Article> articles;
    private Integer daysBefore;
}

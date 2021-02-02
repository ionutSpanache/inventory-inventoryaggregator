package net.metrosystems.inventoryaggregator.articleid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleId implements Serializable {
    private static final long serialVersionUID = -2728759835284405236L;
    private Long articleNumber;
    private Long variantNumber;
    private Long bundleNumber;
}

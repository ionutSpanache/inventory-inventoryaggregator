package inventory.aggregator.articlestoremasterdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleStoreMasterData  implements Serializable {
	private static final long serialVersionUID = -6578051573443000702L;
	private Integer articleNumber;
	private Integer variantNumber;
	private Integer bundleNumber;
	private Integer departmentNumber;
	private String  priorityAssortmentCode;
	private String shelfEdgeTicketType;
	private Integer leadTime;
}
package inventory.aggregator.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PositionContext {

	private List<String> country;
	private List<String> salesLine;
	private List<String> store;

}

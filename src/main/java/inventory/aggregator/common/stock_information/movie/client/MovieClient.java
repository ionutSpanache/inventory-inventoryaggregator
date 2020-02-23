package inventory.aggregator.common.stock_information.movie.client;

import inventory.aggregator.common.stock_information.movie.model.StockDetails;
import inventory.aggregator.common.stock_information.movie.model.StockDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class MovieClient {
		
	RestTemplate restTemplate;

    @Value("${MOVIE_STOCK_HOST}")
    private String movieStockServiceHost;

    @Value("${MOVIE_STOCK_PATH_FOR_VARIANT}")
    private String movieStockPathForVariant;
	
    @Value("${MOVIE_STOCK_PATH_FOR_VARIANT_BUNDLE}")
    private String movieStockPathForVariantBundle;
    
	public MovieClient() {
		restTemplate = new RestTemplate();
	}
	
	public List<StockDetails> getStockDetailsForArticleVariant(String countryCode, String salesLine, int storeNo,
															   Long mmsArtNo, Long variantNo) {
        String urlInUse = String.format(
                movieStockServiceHost + movieStockPathForVariant,
                countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo, mmsArtNo, variantNo);
		try {
			return restTemplate.getForObject(urlInUse, StockDetailsResponse.class).getStockDetails();
		}catch(Exception e) {
			return null;
		}
		
	}
	
	public List<StockDetails> getStockDetailsForArticleVariantBundle(String countryCode, String salesLine, int storeNo,
			                                                    Long articleNumber, Long variant, Long bundleNumber) {
        String urlInUse = String.format(
                movieStockServiceHost + movieStockPathForVariantBundle,
                countryCode.toUpperCase(), salesLine.toUpperCase(), storeNo, articleNumber, variant, bundleNumber);
        try {
            return restTemplate.getForObject(urlInUse, StockDetailsResponse.class).getStockDetails();
        }catch(Exception e) {
            return null;
        }

}
}
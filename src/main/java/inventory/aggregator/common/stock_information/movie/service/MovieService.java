package inventory.aggregator.common.stock_information.movie.service;

import inventory.aggregator.common.stock_information.movie.client.MovieClient;
import inventory.aggregator.common.stock_information.movie.model.MovieAdapter;
import inventory.aggregator.common.stock_information.movie.model.MovieOpenOrder;
import inventory.aggregator.common.stock_information.movie.model.Stock;
import inventory.aggregator.common.stock_information.movie.model.StockDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieService {
	
	private MovieClient movieClient;
	
	
	@Autowired
	public MovieService(MovieClient pMovieClient) {
		this.movieClient = pMovieClient;
	}

	
	private MovieAdapter transformStockDetailToMovieAdapter(StockDetails stockDetails) {
		MovieAdapter movieAdapter = new MovieAdapter();
		movieAdapter.setArticleNo(stockDetails.getArticleNo());
		movieAdapter.setVariantId(stockDetails.getVariant());
		movieAdapter.setBundleId(stockDetails.getBundleNo());
		movieAdapter.setStock(stockDetails.getStockSummary().getSystemStock());
		movieAdapter.setStockMarkDown(stockDetails.getStockSummary().getMarkDownStock());
		movieAdapter.setReservedStock(stockDetails.getStockSummary().getReservation());
		movieAdapter.setUnavailableStock(stockDetails.getStockSummary().getBlockedStock() + stockDetails.getStockSummary().getReservation());
		if (stockDetails.getOpenMovement() != null) {
			Float openInterstoreQuantity = 0.0f;
			Float openReturnQuantity = 0.0f; 
			for (Integer i=0; i<stockDetails.getOpenMovement().size(); i++) {
				if(stockDetails.getOpenMovement().get(i).getReason().equals("IST")) {
				   openInterstoreQuantity = stockDetails.getOpenMovement().get(i).getQuantity();
				}
				if(stockDetails.getOpenMovement().get(i).getReason().equals("RET")) {
					openReturnQuantity = stockDetails.getOpenMovement().get(i).getQuantity();
				}
			}
			movieAdapter.setOpenInterstoreQuantity(openInterstoreQuantity);
			movieAdapter.setOpenReturnQuantity(openReturnQuantity);
		}
		if (stockDetails.getSystemStock() != null) {
			String lastGRDateTemp = stockDetails.getSystemStock().get(0).getLastGRDate();
			String lastSaleDateTemp = stockDetails.getSystemStock().get(0).getLastSalesDate();
			String lastSCDDateTemp = stockDetails.getSystemStock().get(0).getLastSCDate();
			for (Integer i=1; i<stockDetails.getSystemStock().size(); i++) {
				if (Integer.parseInt(lastGRDateTemp) > Integer.parseInt(stockDetails.getSystemStock().get(i).getLastGRDate())) {
					lastGRDateTemp = stockDetails.getSystemStock().get(i).getLastGRDate();
				}
				if (Integer.parseInt(lastSaleDateTemp) < Integer.parseInt(stockDetails.getSystemStock().get(i).getLastSalesDate())) {
					lastSaleDateTemp = stockDetails.getSystemStock().get(i).getLastSalesDate();
				}
				if (Integer.parseInt(lastSCDDateTemp) < Integer.parseInt(stockDetails.getSystemStock().get(i).getLastSCDate())) {
					lastSCDDateTemp = stockDetails.getSystemStock().get(i).getLastSCDate();
				}
			}
			movieAdapter.setLastGoodReceivingDate(lastGRDateTemp);
			movieAdapter.setLastSalesDate(lastSaleDateTemp);
			movieAdapter.setLastStockCorrectionDate(lastSCDDateTemp);
		}
		if(stockDetails.getOpenOrders() != null) {
			movieAdapter.setOpenOrders(new ArrayList<MovieOpenOrder>());
			for(Stock openOrder : stockDetails.getOpenOrders()) {
				movieAdapter.getOpenOrders().add(new MovieOpenOrder(openOrder.getDeliveryDate(), openOrder.getQuantity(), openOrder.getOrderDate()));
			}
		}
		if(stockDetails.getStockRelatedData() != null) {
	    movieAdapter.setStockCoverage(stockDetails.getStockRelatedData().getStockCoverage());
	    movieAdapter.setDailyMeanSales(stockDetails.getStockRelatedData().getDailyMeanSales());
		}
		movieAdapter.setBlockedStock(stockDetails.getStockSummary().getBlockedStock());
		movieAdapter.setAvailableStock(stockDetails.getStockSummary().getAvailableStock());
		
		return movieAdapter;
	}
	
	
	public List<MovieAdapter> getArticleStockForVariant(String countryCode, String salesLine, int storeNo,
			                                            Long mmsArtNo, Long variantNo){
		List<MovieAdapter> movieStockAdapterList = new ArrayList<MovieAdapter>();
		List<StockDetails> stockDetailsList = movieClient.getStockDetailsForArticleVariant(countryCode, salesLine, storeNo, mmsArtNo, variantNo);
		if(!stockDetailsList.isEmpty()){
			for (StockDetails stockDetails : stockDetailsList) {
				if(stockDetails.getReturnCode() == 0) {
					movieStockAdapterList.add(this.transformStockDetailToMovieAdapter(stockDetails));
				}
			}
		}
		return movieStockAdapterList;
	}
	
	public MovieAdapter getArticleStockForVariantBundle(String countryCode, String salesLine, int storeNo,
            Long articleNumber, Long variant, Long bundleNumber){
         List<StockDetails> stockDetailsList = movieClient.getStockDetailsForArticleVariantBundle(countryCode, salesLine, storeNo, articleNumber, variant, bundleNumber);
         if(stockDetailsList != null && !stockDetailsList.isEmpty()){
             for (StockDetails stockDetails : stockDetailsList) {
                 if(stockDetails.getReturnCode() == 0) {
                     return this.transformStockDetailToMovieAdapter(stockDetails);
                 }
             }
         }
         return null;
    }
}
package net.metrosystems.inventoryaggregator.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleBundle;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticlePlannedDeliveryDate;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.model.ArticleVariant;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieAdapter;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.model.MovieOpenOrder;
import net.metrosystems.inventoryaggregator.common.stock_information.movie.service.MovieService;


// Implements the Callable interface adapted for a list of MovieAdapter objects
public class MovieServiceExecutableTask implements Callable<ArticleVariant> {

	MovieService movieService;        // autowired MovieService object to call the Movie Web Service
	String countryCode;               // country code to be used in the request to the Movie Web Service
	String movieSalesLine;            // sales line to be used in the request to the Movie Web Service
	Integer storeNo;                  // store number to be used in the request to the Movie Web Service
	Long articleNumber;               // article number to be used in the request to the Movie Web Service
	ArticleVariant articleVariant;    // article variant number to be enhanced with Movie Data

	private static final Logger logger = Logger.getLogger("MovieServiceExecutableTask");      // handler for logging messages

	// Public constructor that sets the attributes for the call to Movie Web Service
	public MovieServiceExecutableTask(MovieService movieService,
			                          String countryCode,
			                          String movieSalesLine,
			                          Integer storeNo,
			                          Long articleNumber,
			                          ArticleVariant articleVariant) {
		this.movieService = movieService;
		this.countryCode = countryCode;
		this.movieSalesLine = movieSalesLine;
		this.storeNo = storeNo;
		this.articleNumber = articleNumber;
		this.articleVariant = articleVariant;
	}


	// Getters section
	public String getCountryCode() {
		return countryCode;
	}

	public String getMovieSalesLine() {
		return movieSalesLine;
	}

	public Integer getStoreNo() {
		return storeNo;
	}

	public Long getArticleNumber() {
		return articleNumber;
	}

	public ArticleVariant getArticleVariant() {
		return articleVariant;
	}


	public String getIdAsString() {
		return this.countryCode + "-" + this.movieSalesLine + "-" + this.storeNo.toString() + "-" +
				this.articleNumber.toString() + "-" + this.articleVariant.getVariantNumber().toString();
	}


	// Implementation of the mandatory method for the Callable interface
	// Calls the Movie Web Service for the attributes of the current object
	// Returns a list of MovieAdapter objects
	@Override
	public ArticleVariant call() throws Exception{
		List<MovieAdapter> movieAdapterList = new ArrayList<>();
		try {
			logger.info("Thread " + this.getIdAsString() + " has started");
			movieAdapterList = movieService.getArticleStockForVariant(
					this.countryCode, this.movieSalesLine, this.storeNo,
					this.articleNumber, this.articleVariant.getVariantNumber());
			processMovieAdapterListIntoArticleVariant(movieAdapterList);
			return this.articleVariant;
		} catch(Exception e) {
			return null;
		}
	}


	// Take the list of movie adapters received from the Movie Web Service
	// and map them to the appropriate attributes in the current article variant
	private void processMovieAdapterListIntoArticleVariant(List<MovieAdapter> movieAdapterList) {

		// Loop through the list of movie adapters
		for(MovieAdapter movieAdapter : movieAdapterList) {

			// Search for the current bundle in the article variant
			// Initialize the bundles index; needed later for updating the list of bundles
			int bundleIndex = 0;

			// Loop though the list of bundles to find the current one
			for(ArticleBundle articleBundle : this.articleVariant.getBundles()) {

				// Check if we found the current bundle
				if(articleBundle.getBundleNumber().equals(movieAdapter.getBundleId())) {
					// This is the current bundle
					// Map the attributes to the values from the current movie adapter
					articleBundle.setStock(movieAdapter.getStock());
					articleBundle.setStockMarkdown(movieAdapter.getStockMarkDown());
					articleBundle.setReservedStock(movieAdapter.getReservedStock());
					articleBundle.setUnavailableStock(movieAdapter.getUnavailableStock());
					articleBundle.setLastGRDate(movieAdapter.getLastGoodReceivingDate());
					articleBundle.setLastSalesDate(movieAdapter.getLastSalesDate());
					if(movieAdapter.getOpenOrders() != null) {
						articleBundle.setPlannedDeliveryDates(new ArrayList<ArticlePlannedDeliveryDate>());
						for(MovieOpenOrder movieOpenOrder : movieAdapter.getOpenOrders()) {
							articleBundle.getPlannedDeliveryDates().add(new ArticlePlannedDeliveryDate(movieOpenOrder.getDeliveryDate(), movieOpenOrder.getQuantity()));
						}
					}
					// Update the bundle in the bundle list of the variant to include the added data
					this.articleVariant.getBundles().set(bundleIndex, articleBundle);
				}

				// Move to the next bundle index
				bundleIndex++;
			}
		}
	}



	// Method to return a string output of all attributes
	@Override
	public String toString() {
		return "MovieServiceExecutableTask [countryCode=" + countryCode + ", movieSalesLine=" + movieSalesLine
				+ ", storeNo=" + storeNo + ", articleNumber=" + articleNumber + ", variantNumber=" + articleVariant.getVariantNumber()
				+ "]";
	}

}

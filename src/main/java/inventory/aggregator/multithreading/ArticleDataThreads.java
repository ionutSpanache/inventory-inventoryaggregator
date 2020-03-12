package inventory.aggregator.multithreading;

import inventory.aggregator.art_attributes.model.ArticleData;
import inventory.aggregator.articlestoremasterdata.service.AclArticleStoreMasterDataService;
import inventory.aggregator.common.article_information.articlecache.client.ArticleCacheClient;
import inventory.aggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import inventory.aggregator.common.article_information.articlecache.model.ArticleVariant;
import inventory.aggregator.common.stock_information.movie.service.MovieService;
import inventory.aggregator.common.stock_location.acl.service.AclStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ArticleDataThreads {
	

	private MovieService movieService;                              // service to call Movie service end points
	private AclStockService aclStockService;        // service to call ACL daily mean sales service end points
	private AclArticleStoreMasterDataService aclArticleStoreMasterDataService;
	private ArticleCacheClient articleCacheClient;
	private ThreadExecutor threadExecutor;                          // thread pool task executor
	
	@Autowired
	public ArticleDataThreads(
            MovieService movieService,
            AclStockService aclStockService,
            AclArticleStoreMasterDataService aclArticleStoreMasterDataService,
            @Qualifier("articleCacheAAAClient") ArticleCacheClient articleCacheClient,
            ThreadExecutor threadExecutor
			) {
		this.movieService = movieService;
		this.aclStockService = aclStockService;
		this.aclArticleStoreMasterDataService = aclArticleStoreMasterDataService;
		this.articleCacheClient = articleCacheClient;
		this.threadExecutor = threadExecutor;
		
	}


	// Method to get Article Cache documents for all URLs of the current article
	// creating one execution thread for each document
    public List<ArticleCacheIhdsTree> getArticleCacheDocuments(List<String> articleCacheDocumentURLs) {
    	List<ArticleCacheIhdsTree> articleCacheDocuments = new ArrayList<>();
		int availableThreadIdx;
		
		try {
			
			// Create a new thread pool to run movie service executable tasks in parallel
			ArticleCacheDocumentThreadResultTask[] threadResultTasks = createArticleCacheDocumentThreadPool(articleCacheDocumentURLs);
			
			// Create tasks and assign them to threads in the thread pool
			// Then send each ThreadResultTask to the executor using the submit() method
			for (String articleCacheDocumentURL : articleCacheDocumentURLs) {
				// Create a new task for the current article document URL
				ArticleCacheDocumentServiceTask ArticleCacheDocumentServiceTask = new ArticleCacheDocumentServiceTask(
						articleCacheClient,
						articleCacheDocumentURL);
				// Get an available thread index in the thread pool to execute the new task
				availableThreadIdx = getAvailableThreadIndex(threadResultTasks);
				// Check the status of the available thread in the pool and take appropriate actions
				if ((threadResultTasks[availableThreadIdx] != null)
						&& (threadResultTasks[availableThreadIdx].isDone())){
					// The thread completed a previous task
					// Retrieve the result from the thread
					articleCacheDocuments.add(getResultsFromThreadPool(threadResultTasks, availableThreadIdx));
				}
				
				// The thread is ready to receive a new task
				threadResultTasks[availableThreadIdx] = new ArticleCacheDocumentThreadResultTask(
						ArticleCacheDocumentServiceTask);           // create a new movie executable task
				// Start the execution of the thread
				threadExecutor.submitThread(threadResultTasks[availableThreadIdx]);
			}
			
			articleCacheDocuments = getResultsFromThreadPool(articleCacheDocuments, threadResultTasks);
			
		
		// Actions to be performed in case of error
		}catch(Exception e) {
			
		}
		
        // Return article data enhanced with Movie data
		return articleCacheDocuments;
	}
	
	
	// Method to call the Movie Web Service
	// creating one execution thread for each article variant
	public ArticleData addMovieAdapterDataToArticleData(ArticleData articleData, String countryCode, String salesLine, int storeNo) {
		ArticleData articleDataWithMovieAdapterData = articleData;
		int availableThreadIdx;
		
		try {
			
			// Create a new thread pool to run movie service executable tasks in parallel
			ThreadResultTask[] threadResultTasks = createThreadPool(articleData);
			
			// Create tasks and assign them to threads in the thread pool
			// Then send each ThreadResultTask to the executor using the submit() method
			Integer iArticleVariant = 0;
			for(ArticleVariant articleVariant : articleData.getVariantList()) {
				
				// Create a new task for the current article variant
				MovieServiceExecutableTask movieServiceExecutableTask = new MovieServiceExecutableTask(
						movieService, countryCode, salesLine, storeNo,
						articleData.getArticleNumber(), articleVariant);
				// Get an available thread index in the thread pool to execute the new task
				availableThreadIdx = getAvailableThreadIndex(threadResultTasks);
				// Check the status of the available thread in the pool and take appropriate actions
				if ((threadResultTasks[availableThreadIdx] != null)
						&& (threadResultTasks[availableThreadIdx].isDone())){
					// The thread completed a previous task
					// Retrieve the result from the thread
					articleDataWithMovieAdapterData = getResultsFromThreadPool(
							articleDataWithMovieAdapterData, threadResultTasks, availableThreadIdx);
				}
				
				// The thread is ready to receive a new task
				threadResultTasks[availableThreadIdx] = new ThreadResultTask(
						movieServiceExecutableTask,                // create a new movie executable task
						iArticleVariant);                          // for the current variant index
				// Start the execution of the thread
				threadExecutor.submitThread(threadResultTasks[availableThreadIdx]);
				
				// Move to the next article variant index
				iArticleVariant++;
			}
			
			articleDataWithMovieAdapterData = getResultsFromThreadPool(articleDataWithMovieAdapterData, threadResultTasks);
		
		// Actions to be performed in case of error
		}catch(Exception e) {
			
		}
		
        // Return article data enhanced with Movie data
		return articleDataWithMovieAdapterData;
	}
      
	// Method to call the ACL stock location Web Service
	// creating one execution thread for each article variant
	public ArticleData addAclStockLocationDataToArticleData(ArticleData articleData, String countryCode, String salesLine, int storeNo) {
		ArticleData articleDataWithAclStockLocationData = articleData;
		int availableThreadIdx;
		
		try {
			
			// Create a new thread pool to run movie service executable tasks in parallel
			ThreadResultTask[] threadResultTasks = createThreadPool(articleData);
			
			// Create tasks and assign them to threads in the thread pool
			// Then send each ThreadResultTask to the executor using the submit() method
			Integer iArticleVariant = 0;
			for(ArticleVariant articleVariant : articleData.getVariantList()) {
				
				// Create a new task for the current article variant
				AclStockLocationServiceExecutableTask aclStockLocationServiceExecutableTask =
						new AclStockLocationServiceExecutableTask(
                                aclStockService, countryCode, salesLine, storeNo,
						articleData.getArticleNumber(), articleVariant);
				// Get an available thread index in the thread pool to execute the new task
				availableThreadIdx = getAvailableThreadIndex(threadResultTasks);
				// Check the status of the available thread in the pool and take appropriate actions
				if ((threadResultTasks[availableThreadIdx] != null)
						&& (threadResultTasks[availableThreadIdx].isDone())){
					// The thread completed a previous task
					// Retrieve the result from the thread
					articleDataWithAclStockLocationData = getResultsFromThreadPool(
							articleDataWithAclStockLocationData, threadResultTasks, availableThreadIdx);
				}
				
				// The thread is ready to receive a new task
				threadResultTasks[availableThreadIdx] = new ThreadResultTask(
						aclStockLocationServiceExecutableTask,                // create a new ACL daily mean sales executable task
						iArticleVariant);                                     // for the current variant index
				// Start the execution of the thread
				threadExecutor.submitThread(threadResultTasks[availableThreadIdx]);
				
				// Move to the next article variant index
				iArticleVariant++;
			}
			
			articleDataWithAclStockLocationData = getResultsFromThreadPool(articleDataWithAclStockLocationData, threadResultTasks);
		
		// Actions to be performed in case of error
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
        // Return article data enhanced with Acl ordering data
		return articleDataWithAclStockLocationData;
	}


	public ArticleData addAclArticleStoreMasterDataToArticleData(ArticleData articleData, String countryCode, String salesLine, int storeNo) {
		ArticleData articleDataWithAclArticleStoreMasterData = articleData;
		int availableThreadIdx;
		
		try {
			
			// Create a new thread pool to run movie service executable tasks in parallel
			ThreadResultTask[] threadResultTasks = createThreadPool(articleData);
			
			// Create tasks and assign them to threads in the thread pool
			// Then send each ThreadResultTask to the executor using the submit() method
			Integer iArticleVariant = 0;
			for(ArticleVariant articleVariant : articleData.getVariantList()) {
				
				// Create a new task for the current article variant
				AclArticleStoreMasterDataServiceExecutableTask aclArticleStoreMasterDataServiceExecutableTask =
						new AclArticleStoreMasterDataServiceExecutableTask(
								aclArticleStoreMasterDataService, countryCode, salesLine, storeNo, articleData, articleVariant);
				// Get an available thread index in the thread pool to execute the new task
				availableThreadIdx = getAvailableThreadIndex(threadResultTasks);
				// Check the status of the available thread in the pool and take appropriate actions
				if ((threadResultTasks[availableThreadIdx] != null)
						&& (threadResultTasks[availableThreadIdx].isDone())){
					// The thread completed a previous task
					// Retrieve the result from the thread
					articleDataWithAclArticleStoreMasterData = getResultsFromThreadPool(
							articleDataWithAclArticleStoreMasterData, threadResultTasks, availableThreadIdx);
				}
				
				// The thread is ready to receive a new task
				threadResultTasks[availableThreadIdx] = new ThreadResultTask(
						aclArticleStoreMasterDataServiceExecutableTask,                // create a new ACL Article Store Master executable task
						iArticleVariant);                                      // for the current variant index
				// Start the execution of the thread
				threadExecutor.submitThread(threadResultTasks[availableThreadIdx]);
				
				// Move to the next article variant index
				iArticleVariant++;
			}
			
			articleDataWithAclArticleStoreMasterData = getResultsFromThreadPool(articleDataWithAclArticleStoreMasterData, threadResultTasks);
		
		// Actions to be performed in case of error
		}catch(Exception e) {
			
		}
		
        // Return article data enhanced with Acl Article Store Master Data
		return articleDataWithAclArticleStoreMasterData;
	}
   
    
    // Creates a thread pool sized according
    // to the number of article variants in the ArticleData object
    private ThreadResultTask[] createThreadPool(ArticleData articleData) {
    	int threadPoolMaxSize;
    	
		// Establish the size of the thread pool based on the number of variants contained in the ArticleData object
		// but not higher than 5 threads
		if (articleData.getVariantList().size() > 5) {
			threadPoolMaxSize = 5;
		}else {
			threadPoolMaxSize = articleData.getVariantList().size();
		}
		
		// Create and return the thread pool
		return new ThreadResultTask[threadPoolMaxSize];
    }
    
    
    // Creates a thread pool sized according
    // to the number of article document URLs
    private ArticleCacheDocumentThreadResultTask[] createArticleCacheDocumentThreadPool(List<String> articleCacheDocumentURLs) {
    	int threadPoolMaxSize;
    	
		// Establish the size of the thread pool based on the number of variants contained in the ArticleData object
		// but not higher than 5 threads
		if (articleCacheDocumentURLs.size() > 5) {
			threadPoolMaxSize = 5;
		}else {
			threadPoolMaxSize = articleCacheDocumentURLs.size();
		}
		
		// Create and return the thread pool
		return new ArticleCacheDocumentThreadResultTask[threadPoolMaxSize];
    }
    
    
    // Searches for an available thread index in the thread pool
    // that can be used to assign a new task
    private int getAvailableThreadIndex(ThreadResultTask[] threadResultTasks) {
    	int threadPoolIdx = 0;                // initialize the thread pool index
    	boolean availableIdxFound = false;    // initialize the available index search status
    	
		// Search for an available thread in the thread pool
		// to execute the task for the current article variant
		do {
			// Check the initialization status of the current thread
			if (threadResultTasks[threadPoolIdx] == null) {
				// The current thread was not initialized yet, so it is available to receive a task
				// Set the search status to found
				availableIdxFound = true;
				
			}else if (threadResultTasks[threadPoolIdx].isDone()){
				// The current thread was previously initialized
				// and completed the previously assigned task execution so it is available to receive a new task
				// Set the search status to found
				availableIdxFound = true;
				
			}else {
				// The current thread exists but it is still running a previously assigned task
				// Try the next thread in the pool
				threadPoolIdx++;   
			}
			
			if (threadPoolIdx >= threadResultTasks.length) {
				// The entire thread pool was checked, no available slot was found for the current task,
				// so we reset the thread pool index and start again until a thread completes its assigned task
				// and becomes available
				threadPoolIdx = 0;
			}
		} while (!availableIdxFound);     // Exit when an available index was found
		
    	return threadPoolIdx;
    }
    
    
    // Searches for an available thread index in the thread pool
    // that can be used to assign a new task
    private int getAvailableThreadIndex(ArticleCacheDocumentThreadResultTask[] threadResultTasks) {
    	int threadPoolIdx = 0;                // initialize the thread pool index
    	boolean availableIdxFound = false;    // initialize the available index search status
    	
		// Search for an available thread in the thread pool
		// to execute the task for the current article variant
		do {
			// Check the initialization status of the current thread
			if (threadResultTasks[threadPoolIdx] == null) {
				// The current thread was not initialized yet, so it is available to receive a task
				// Set the search status to found
				availableIdxFound = true;
				
			}else if (threadResultTasks[threadPoolIdx].isDone()){
				// The current thread was previously initialized
				// and completed the previously assigned task execution so it is available to receive a new task
				// Set the search status to found
				availableIdxFound = true;
				
			}else {
				// The current thread exists but it is still running a previously assigned task
				// Try the next thread in the pool
				threadPoolIdx++;   
			}
			
			if (threadPoolIdx >= threadResultTasks.length) {
				// The entire thread pool was checked, no available slot was found for the current task,
				// so we reset the thread pool index and start again until a thread completes its assigned task
				// and becomes available
				threadPoolIdx = 0;
			}
		} while (!availableIdxFound);     // Exit when an available index was found
		
    	return threadPoolIdx;
    }
    
    
    // Retrieves the results from all threads in the pool
    private ArticleData getResultsFromThreadPool(
    		ArticleData articleData,
    		ThreadResultTask[] threadResultTasks) {
    	boolean allThreadsDone;     // task completion marker
    	
    	// Wait until all threads are done
    	do {
    		allThreadsDone = true;
    		for (int i=0; i<threadResultTasks.length; i++) {
				// Check the status of the current thread
				if (!threadResultTasks[i].isDone()) {
					// The thread did not complete its task
					// Set the completion marker to false
					allThreadsDone = false;
				}
    		}
    	}while (!allThreadsDone);
    	
		for (int i=0; i<threadResultTasks.length; i++) {
			try {
				// The thread completed its task
				// Retrieve the result from the thread in the corresponding article variant from the list
				articleData.getVariantList().set(
						threadResultTasks[i].getArticleVariantIndex(), // variant index for the thread
						threadResultTasks[i].get());                   // variant with Movie data		
			}catch(Exception e) {
				
			}
		}
		return articleData;
    }
    
    
    // Retrieves the results from all threads in the pool
    private List<ArticleCacheIhdsTree> getResultsFromThreadPool(
    		List<ArticleCacheIhdsTree> articleDocuments,
    		ArticleCacheDocumentThreadResultTask[] threadResultTasks) {
    	boolean allThreadsDone;     // task completion marker
    	
    	// Wait until all threads are done
    	do {
    		allThreadsDone = true;
    		for (int i=0; i<threadResultTasks.length; i++) {
				// Check the status of the current thread
				if (!threadResultTasks[i].isDone()) {
					// The thread did not complete its task
					// Set the completion marker to false
					allThreadsDone = false;
				}
    		}
    	}while (!allThreadsDone);
    	
		for (int i=0; i<threadResultTasks.length; i++) {
			try {
				// The thread completed its task
				// Retrieve the result from the thread in the corresponding article variant from the list
				articleDocuments.add(threadResultTasks[i].get());
			}catch(Exception e) {
				
			}
		}
		return articleDocuments;
    }
    
    
    // Retrieves the result of a specified thread from the pool
    private ArticleData getResultsFromThreadPool(
    		ArticleData articleData,
    		ThreadResultTask[] threadResultTasks,
    		int threadPoolIdx) {
		try {
			// Check the status of the requested thread
			if (threadResultTasks[threadPoolIdx].isDone()) {
				// The thread completed its task
				// Retrieve the result from the thread in the corresponding article variant from the list
				articleData.getVariantList().set(
						threadResultTasks[threadPoolIdx].getArticleVariantIndex(), // variant index for the thread
						threadResultTasks[threadPoolIdx].get());                   // variant with Movie data		
			}
		}catch(Exception e) {
			
		}

		return articleData;
    }
    
    
    // Retrieves the result of a specified thread from the pool
    private ArticleCacheIhdsTree getResultsFromThreadPool(
    		ArticleCacheDocumentThreadResultTask[] threadResultTasks,
    		int threadPoolIdx) {
    	ArticleCacheIhdsTree articleCacheDocument = new ArticleCacheIhdsTree();
		try {
			// Check the status of the requested thread
			if (threadResultTasks[threadPoolIdx].isDone()) {
				// The thread completed its task
				// Retrieve the result from the thread
				articleCacheDocument = threadResultTasks[threadPoolIdx].get();
			}
		}catch(Exception e) {
			
		}

		return articleCacheDocument;
    }

}

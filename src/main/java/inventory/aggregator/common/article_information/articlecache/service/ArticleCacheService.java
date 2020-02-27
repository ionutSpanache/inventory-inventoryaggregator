package inventory.aggregator.common.article_information.articlecache.service;

import inventory.aggregator.articleId.client.article_cache_aaa.document.model.Documents;
import inventory.aggregator.articleId.client.article_cache_aaa.document.service.DocumentsService;
import inventory.aggregator.articleId.model.ArticleId;
import inventory.aggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.ArticleCacheStoreExceptionsClient;
import inventory.aggregator.common.article_information.articlecache.client.articleCacheStoreExceptions.model.ArticleCacheStoreExceptionsIhdsTree;
import inventory.aggregator.common.article_information.articlecache.client.article_cache_aaa.ArticleCacheAAAClient;
import inventory.aggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheDataGroup;
import inventory.aggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheIhdsTree;
import inventory.aggregator.common.article_information.articlecache.client.article_cache_model.ArticleCacheReaderDigest;
import inventory.aggregator.common.article_information.articlecache.model.*;
import inventory.aggregator.common.model.ArticleNotFoundException;
import inventory.aggregator.multithreading.ArticleDataThreads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class ArticleCacheService {

	@Value("${ARTICLE_CACHE_STORE_EXCEPTIONS_PATH}")
	private String articleCacheStoreExceptionsPath;

	@Autowired
	DocumentsService documentsService;
    
	private ArticleDataThreads articleDataThreads;
	//@Qualifier("articleCacheAAAClient")
	@Autowired
	private ArticleCacheAAAClient articleCacheClient;
	private ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient;
	@Autowired
	//public ArticleCacheService(ArticleCacheClient pArticleCacheClient, ArticleDataThreads articleDataThreads) {
//		this.articleCacheClient = pArticleCacheClient;
	public ArticleCacheService(ArticleDataThreads articleDataThreads, ArticleCacheStoreExceptionsClient articleCacheStoreExceptionsClient) {
		this.articleDataThreads = articleDataThreads;
		this.articleCacheStoreExceptionsClient = articleCacheStoreExceptionsClient;
	}
	
	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForEan(
			String ean,
			String countryCode,
			String salesLine,
			String languageCode) throws Exception {
		return getArticleVariantBundleIdentifierListFromFilterUrl(articleCacheClient.getArticleUrlPathByEan(ean, countryCode, salesLine, languageCode));
		
	}
	
	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForSubsys(
			Long subsysArtNo,
			String countryCode,
			String salesLine,
			String languageCode) throws Exception {
		return getArticleVariantBundleIdentifierListFromFilterUrl(
				articleCacheClient.getArticleUrlPathBySubsysArtNo(subsysArtNo, countryCode, salesLine, languageCode)
		);
		
	}
	
	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForArticleVariantBundle(
			Long articleNumber,
			Long variantNumber,
			Long bundleNumber,
			String countryCode,
			String salesLine,
			String languageCode
	) throws Exception {
		return getArticleVariantBundleIdentifierListFromFilterUrl(
				articleCacheClient.getArticleUrlPathByArtVarBundle(articleNumber, variantNumber, bundleNumber, countryCode, salesLine, languageCode)
		);
		
	}
	
	public List<ArticleVariantBundleIdentifier> getArticleVariantBundleListForArticleNumber(
			Long articleNumber,
			String countryCode,
			String salesLine,
			String languageCode
	) throws Exception {

		return getArticleVariantBundleIdentifierListFromFilterUrl(
				articleCacheClient.getArticleUrlPathByMmsArtNo(articleNumber, countryCode, salesLine, languageCode)
		);
		
	}

	private List<ArticleVariantBundleIdentifier> getArticleVariantBundleIdentifierListFromFilterUrl(Documents filterUrlString) {
		if (filterUrlString.getArticleKeys().isEmpty()) {
			throw new ArticleNotFoundException();
		}
		
		List<ArticleVariantBundleIdentifier> articleVariantBundleIdentifierList =
				new ArrayList<ArticleVariantBundleIdentifier>();
		for (String articleUrlPath : filterUrlString.getArticleKeys()) {
			ArticleId articleId = documentsService.tranformDocumentInArticleId(articleUrlPath);
			ArticleVariantBundleIdentifier articleVariantBundleIdentifier = new ArticleVariantBundleIdentifier();
			articleVariantBundleIdentifier.setArticleNumber(articleId.getArticleNumber());
			articleVariantBundleIdentifier.setVariantNumber(articleId.getVariantNumber());
			articleVariantBundleIdentifier.setBundleNumber(articleId.getBundleNumber());
			articleVariantBundleIdentifierList.add(articleVariantBundleIdentifier);
		}
		
		return articleVariantBundleIdentifierList;
	}
	
	public List<ArticleCacheFullData> getArticleCacheFullDataByEan(String ean,
																   String countryCode, String salesLine, String languageCode, Integer storeNo) throws Exception {
		Documents articleUrlPathList = articleCacheClient.getArticleUrlPathByEan(ean, countryCode, salesLine, languageCode);
		if (articleUrlPathList.getArticleKeys().isEmpty()) {
			throw new ArticleNotFoundException();
		}
		List<ArticleCacheFullData> articleFullDataList = new ArrayList<ArticleCacheFullData>();
		for (String articleUrlPath : articleUrlPathList.getArticleKeys()) {
			articleFullDataList.add(
					transformArticleCacheDocumentToArticleFullData(
							articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath),
							                                                storeNo));
		}
		return articleFullDataList;
	}
	
	public ArticleCacheFullData getArticleCacheFullDataByArtVarBundle(
			Long articleNumber,
			Long variantNumber,
			Long bundleNumber,
			String countryCode,
			String salesLine,
			String languageCode,
			Integer storeNo) throws Exception {
		Documents articleUrlPathList = articleCacheClient.getArticleUrlPathByArtVarBundle(
																articleNumber,
																variantNumber,
																bundleNumber,
																countryCode,
				salesLine,
																languageCode
																);
		if (articleUrlPathList.getArticleKeys().isEmpty()) {
			throw new ArticleNotFoundException();
		}
		String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
		ArticleCacheFullData articleCacheFullData =
				transformArticleCacheDocumentToArticleFullData(
												articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath),
												storeNo);
		return articleCacheFullData;
	}
	
	private ArticleCacheFullData transformArticleCacheDocumentToArticleFullData (
																			ArticleCacheIhdsTree articleCacheDocument,
			                                                                Integer storeNo) {
		Integer hierarchySequenceStoreNo = articleCacheDocument.getTransaction().getArticleCacheIncident().getHierarchySequenceStoreNo(storeNo);
		if(hierarchySequenceStoreNo == null){
			throw new ArticleNotFoundException();
		}
		ArticleCacheReaderDigest articleCacheReaderDigest = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest();
		ArticleCacheDataGroup articleCacheDataGroup100 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(100);
		ArticleCacheDataGroup articleCacheDataGroup310 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithoutStore(310);
		ArticleCacheDataGroup articleCacheDataGroup320 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithStore(320, hierarchySequenceStoreNo);
		ArticleCacheDataGroup articleCacheDataGroup325 = articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheDataGroupWithStoreRef(325, hierarchySequenceStoreNo);
		
		return new ArticleCacheFullData(
				articleCacheReaderDigest.getMmsArtNo(),
				articleCacheReaderDigest.getVariantNo(),
				articleCacheReaderDigest.getBundleNo(),
				articleCacheReaderDigest.getArticleDescription(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBarcodeForBundle(),
				articleCacheReaderDigest.getProducingSupplier(),
				articleCacheDataGroup100.getPresentationMerchandiseGroup(),
				articleCacheDataGroup320.getPromotionId(),
				articleCacheDataGroup310.getSellingBundleContent(),
				articleCacheDataGroup320.getWeightArticle(),
				articleCacheReaderDigest.getSubsysNo(),
				articleCacheDataGroup100.getVatPercent(),
				articleCacheDataGroup320.getPieceNetSellPrice(),
				articleCacheDataGroup320.getBundleNetSellPrice(),
				articleCacheDataGroup320.getPromotionBundleNetPrice(),
				articleCacheReaderDigest.getVariantText(),
				articleCacheDataGroup325.getArticleClass(),
				articleCacheReaderDigest.getMerchGroupMain(),
				articleCacheReaderDigest.getMerchGroupSub(),
				articleCacheDataGroup320.getAssignedStoreSupplier(),
				articleCacheDataGroup325.getProcurementCd()
				);
	}

	public ArticleBasicData getArticleBasicDataByEan(String ean, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		return processDocumentsReceivedFromArticleCache(
				articleCacheClient.getArticleUrlPathByEan(ean, countryCode, salesLine, languageCode)
				, storeNo);
	}

	public ArticleBasicData getArticleBasicDataByMmsArtNo(Long mmsArtNo, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		return processDocumentsReceivedFromArticleCache(
				articleCacheClient.getArticleUrlPathByMmsArtNo(mmsArtNo, countryCode, salesLine, languageCode)
				, storeNo);
	}

	public ArticleBasicData getArticleBasicDataBySubsysArtNo(Long subsysArtNo, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		return processDocumentsReceivedFromArticleCache(
				articleCacheClient.
						getArticleUrlPathBySubsysArtNo(subsysArtNo, countryCode, salesLine, languageCode), storeNo);
	}

	private ArticleBasicData processDocumentsReceivedFromArticleCache(Documents articleUrlPathList, int storeNo) {
		if (articleUrlPathList.getArticleKeys().isEmpty()) {
            throw new ArticleNotFoundException();
        }
		String articleUrlPath = articleUrlPathList.getArticleKeys().get(0);
        ArticleId articleId = new ArticleId();
        String[] articleUrlPathObtaineArticleIdComponent = articleUrlPath.split("&");
		for (int i = 0; i < articleUrlPathObtaineArticleIdComponent.length; i++) {
			if (articleUrlPathObtaineArticleIdComponent[i].contains("bundleno")) {
				articleId.setBundleNumber(Long.valueOf(articleUrlPathObtaineArticleIdComponent[i].substring(articleUrlPathObtaineArticleIdComponent[i].indexOf("=") + 1)));
			}
			if (articleUrlPathObtaineArticleIdComponent[i].contains("variantno")) {
				articleId.setVariantNumber(Long.valueOf(articleUrlPathObtaineArticleIdComponent[i].substring(articleUrlPathObtaineArticleIdComponent[i].indexOf("=") + 1)));
			}
			if (articleUrlPathObtaineArticleIdComponent[i].contains("mmsartno")) {
				articleId.setArticleNumber(Long.valueOf(articleUrlPathObtaineArticleIdComponent[i].substring(articleUrlPathObtaineArticleIdComponent[i].indexOf("=") + 1)));
			}

		}

        ArticleCacheIhdsTree articleCacheDocument = articleCacheClient.getArticleCacheDocumentByURL(articleUrlPath);
        return transformArticleCacheDocumentToArticleBasicData(articleCacheDocument, storeNo, articleId);

    }



	private ArticleBasicData transformArticleCacheDocumentToArticleBasicData(ArticleCacheIhdsTree articleCacheDocument,
                                                                             int storeNo, ArticleId articleId) {
		return new ArticleBasicData(
				articleCacheDocument.getTransaction().getId(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMmsArtNo(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getArticleDescription(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getAssignedStoreSupplierTypeForArticle(storeNo),
				//articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMerchGroup(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getOwnBrand(),
                Math.toIntExact(articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getProducingSupplier()),
                articleId
				);
	}
	private List<ArticleVariant> addArticleCacheDocumentToArticleVariantList(List<ArticleVariant> articleVariantList,
                                                                             ArticleCacheIhdsTree articleCacheDocument,
                                                                             int storeNo) {
    	List<ArticleVariant> newArticleVariantList = articleVariantList;
    	boolean variantAlreadyExists = false;
    	if(!newArticleVariantList.isEmpty()) {
        	for(int i=0; i<newArticleVariantList.size(); i++) {
        		if(newArticleVariantList.get(i).getVariantNumber() == articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getVariantNo()){
        			newArticleVariantList.get(i).getBundles().add(transformArticleCacheDocumentToArticleBundle(articleCacheDocument, storeNo));
        			variantAlreadyExists = true;
        		}
        	}
    	}
    	
    	if(!variantAlreadyExists) {
    		try {
    			newArticleVariantList.add(transformArticleCacheDocumentToArticleVariant(articleCacheDocument, storeNo));	
    		}catch (ArticleNotFoundException e) {
    			
    		}
    		
    	}
    	return newArticleVariantList;
    }
	private ArticleVariant transformArticleCacheDocumentToArticleVariant(ArticleCacheIhdsTree articleCacheDocument,
			                                                             int storeNo) throws ArticleNotFoundException{
		Integer hierarchySequenceStoreNo = articleCacheDocument.getTransaction().getArticleCacheIncident().getHierarchySequenceStoreNo(storeNo);
		if (hierarchySequenceStoreNo != null){
			return new ArticleVariant(
					articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getVariantNo(),
					articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getVariantText(),
					transformArticleCacheDocumentToArticleBundle(articleCacheDocument, storeNo));
		}else {
			throw new ArticleNotFoundException();
		}

	}
	
	private ArticleBundle transformArticleCacheDocumentToArticleBundle(ArticleCacheIhdsTree articleCacheDocument,
                                                                       int storeNo) {
		Integer hierarchySequenceStoreNo = articleCacheDocument.getTransaction().getArticleCacheIncident().getHierarchySequenceStoreNo(storeNo);
		Integer hierarchySequenceStoreNoForProcessing;
		if (hierarchySequenceStoreNo == null) {
			hierarchySequenceStoreNoForProcessing = -1;
		}else {
			hierarchySequenceStoreNoForProcessing = hierarchySequenceStoreNo;
		}
		return new ArticleBundle(
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getBundleNo(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getSubsysNo(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBarcodeForBundle(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMerchGroup(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBundleProcurementCodeForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getBundleBlockingCodeForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getPromotionIdForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getSellingBundleIndicator(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getSellingBundleContent(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getPriceCheck(),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getStatusForStore(hierarchySequenceStoreNoForProcessing),
				articleCacheDocument.getTransaction().getArticleCacheIncident().getWeightArticle(hierarchySequenceStoreNoForProcessing),
				Math.toIntExact(articleCacheDocument.getTransaction().getArticleCacheIncident().getArticleCacheReaderDigest().getMerchGroupMain())
				);
	}

	public List<ArticleVariant> getArticleVariantsByArticleNumber(ArticleBasicData articleMasterData, String countryCode, String salesLine, String languageCode, int storeNo) throws Exception {
		List<ArticleVariant> articleVariantList = new ArrayList<ArticleVariant>();
		Documents articleCacheDocumentURLs = articleCacheClient.findArticleUrlPathByMmsArtNo(articleMasterData.getArticleNumber(), countryCode, salesLine, languageCode);
        List<String> articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition = new ArrayList<>();
        articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition.add(null);
		List<CompletableFuture> articleVariantTasks = new ArrayList<>();
		for (String articleCacheDocumentURL : articleCacheDocumentURLs.getArticleKeys()
        ) {
			ArticleId articleId = documentsService.tranformDocumentInArticleId(articleCacheDocumentURL);
			if (articleId.getArticleNumber().equals(articleMasterData.getArticleId().getArticleNumber())
					&& articleId.getVariantNumber().equals(articleMasterData.getArticleId().getVariantNumber())
					&& articleId.getBundleNumber().equals(articleMasterData.getArticleId().getBundleNumber())) {
                articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition.set(0, articleCacheDocumentURL);
            } else {
                articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition.add(articleCacheDocumentURL);
            }

            //todo: check if this approach has better performance
/*			Boolean added = false;


			while (!added) {
				if (TaskExecutor.getNoTasks() < TaskExecutor.getExecutor().getMaxPoolSize() - 1) {
					TaskExecutor.addTask();
					added = true;
					CompletableFuture articleVariantCompletableFuture = CompletableFuture.supplyAsync(() ->
					{
						try {
							return transformArticleCacheDocumentToArticleVariant(
									articleCacheClient.getArticleCacheDocumentByURL(articleCacheDocumentURL)
									, storeNo);
						} catch (Exception e) {
							throw e;
						}
					}, TaskExecutor.getExecutor());
					articleVariantCompletableFuture.thenRun(() -> {
						TaskExecutor.removeTask();
					});
					articleVariantTasks.add(articleVariantCompletableFuture);
				} else {
					try {

						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}*/
        }
        //todo: check if this approach has better performance
        //todo:check here error for art 931988 001 001 in list are 5 but here 2 or 3 are readded
/*
		List<ArticleVariant> finalArticleVariantList = articleVariantList;
		articleVariantTasks.forEach(
				articleVariantTask -> {
					try {
						articleVariantTask.join();
						finalArticleVariantList.add((ArticleVariant) articleVariantTask.get());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
		);
*/


        for (ArticleCacheIhdsTree articleCacheDocument : articleDataThreads.getArticleCacheDocuments(articleCacheDocumentURLsWithsearchedDBundleOnFirstPosition)) {

            articleVariantList = addArticleCacheDocumentToArticleVariantList(articleVariantList, articleCacheDocument, storeNo);
        }

		return articleVariantList;
	}

	private List<ArticleCacheIhdsTree> getArticleCacheDocumentsByURLs(List<String> articleCacheDocumentURLs) {
		List<ArticleCacheIhdsTree> articleCacheDocuments = new ArrayList<>(); 		
		for (String articleCacheDocumentURL : articleCacheDocumentURLs) {
			 articleCacheDocuments.add(articleCacheClient.getArticleCacheDocumentByURL(articleCacheDocumentURL));
		}
		return articleCacheDocuments;
	}

	public ArticleCacheStoreExceptionsIhdsTree getArticleCacheStoreExceptions(
			Long articleNumber,
			Long variantNumber,
			Long bundleNumber,
			String countryCode,
			String salesLine,
			String languageCode,
			Integer storeNo) {

		String articleUrlPath = String.format(articleCacheStoreExceptionsPath,
				countryCode.toLowerCase(), bundleNumber, salesLine.toLowerCase(), languageCode, articleNumber, storeNo, variantNumber);
		System.out.println("articleUrlPath: " + articleUrlPath);
		ArticleCacheStoreExceptionsIhdsTree articleCacheStoreExceptionsIhdsTree = articleCacheStoreExceptionsClient.getArticleCacheDocumentByURL(articleUrlPath);
		return articleCacheStoreExceptionsIhdsTree;
	}
	
}

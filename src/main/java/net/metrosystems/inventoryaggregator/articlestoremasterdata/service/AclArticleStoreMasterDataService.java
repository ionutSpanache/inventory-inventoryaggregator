package net.metrosystems.inventoryaggregator.articlestoremasterdata.service;

import java.util.List;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.client.AclArticleStoreMasterDataClient;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.ArticleStoreMasterData;
import net.metrosystems.inventoryaggregator.common.model.StoreId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AclArticleStoreMasterDataService {

  private final AclArticleStoreMasterDataClient aclArticleStoreMasterDataClient;

  @Autowired
  public AclArticleStoreMasterDataService(
      AclArticleStoreMasterDataClient aclArticleStoreMasterDataClient) {
    this.aclArticleStoreMasterDataClient = aclArticleStoreMasterDataClient;
  }

  public List<ArticleStoreMasterData> getArticleStoreMasterDataByEan(
      StoreId storeId, String barcode) {

    return aclArticleStoreMasterDataClient.getArticleStoreMasterDataByEan(
        storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), barcode);
  }

  public List<ArticleStoreMasterData> getArticleStoreMasterDataByArtVarBundle(
      StoreId storeId, List<ArticleId> articleIdList) {

    return aclArticleStoreMasterDataClient.getArticleStoreMasterDataByArtVarBundle(
        storeId.getCountryCode(), storeId.getSalesLine(), storeId.getStoreNumber(), articleIdList);
  }
}

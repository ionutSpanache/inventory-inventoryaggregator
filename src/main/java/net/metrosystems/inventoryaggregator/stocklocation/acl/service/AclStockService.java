package net.metrosystems.inventoryaggregator.stocklocation.acl.service;

import java.util.Collections;
import java.util.List;
import net.metrosystems.inventoryaggregator.articleattributes.model.ArticleId;
import net.metrosystems.inventoryaggregator.articleattributes.model.articleattributeslist.ArticleLocationForArticleId;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclArticleStockLocation;
import net.metrosystems.inventoryaggregator.stocklocation.acl.model.AclBundleStockLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AclStockService {
  private final AclStockClient aclStockClient;

  @Autowired
  public AclStockService(AclStockClient aclStockClient) {
    this.aclStockClient = aclStockClient;
  }

  public List<AclBundleStockLocation> getAclBundleStockLocations(
      String countryCode, String salesLine, Integer storeNo, Long articleNo, Long variantNo) {
    try {
      return aclStockClient.getStockLocationsForArticleVariant(
          countryCode, salesLine, storeNo, articleNo, variantNo);
    } catch (Exception e) {
      return null;
    }
  }

  public List<AclArticleStockLocation> getStockLocationsForArticleList(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {
    try {
      return aclStockClient.getStockLocationsForArticleList(
          countryCode, salesLine, storeNo, articleIdList);
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  public List<ArticleLocationForArticleId> getAclArticleLocationsForArticleList(
      String countryCode, String salesLine, Integer storeNo, List<ArticleId> articleIdList) {
    try {
      return aclStockClient.getArticleLocationsForArticleList(
          countryCode, salesLine, storeNo, articleIdList);
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }
}

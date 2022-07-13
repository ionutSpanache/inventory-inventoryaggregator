package net.metrosystems.inventoryaggregator.articleattributes.model;

public enum DataType {
  ARTICLE_COMPONENT("articleComponent"),
  ARTICLE_MASTER_DATA("articleMasterData"),
  IDU_ITEM_DERIVED_STANDARDS("iduItemDerivedStandards"),
  ARTICLE_STORE_MASTER("articleStoreMaster"),
  ARTICLE_STORE_EXCEPTIONS("articleStoreExceptions"),
  STOCK_DATA("stockData"),
  ARTICLE_STOCK_LOCATION("articleStockLocation"),
  ARTICLE_LOCATION("articleLocation"),
  ;

  private String dataType;

  private DataType(String dataType){
    this.dataType = dataType;
  }

  public String getDataType(){
    return dataType;
  }
}

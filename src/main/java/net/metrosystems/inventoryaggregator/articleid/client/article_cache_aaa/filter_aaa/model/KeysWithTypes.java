package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

public enum KeysWithTypes {
    salesBarcode("salesBarcode", "long"),
    mmsArtNo("mmsArtNo", "long"),
    variantNo("variantNo", "long"),
    bundleNo("bundleNo", "long"),
    subsysNo("subsysNo", "long"),
    articleNumber("articleNumber", "string"),
    variantNumber("variantNumber", "string"),
    bundleNumber("bundleNumber", "string"),
    storeSubsystemArticleNumber("storeSubsystemArticleNumber", "string"),
    company("company", "string");
    private final String code;
    private final String type;

    KeysWithTypes(String code, String type) {
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}

package net.metrosystems.inventoryaggregator.articleid.client.article_cache_aaa.filter_aaa.model;

public enum KeysWithTypes {
    salesBarcode("salesBarcode", "long"),
    mmsArtNo("mmsArtNo", "long"),
    variantNo("variantNo", "long"),
    bundleNo("bundleNo", "long"),
    subsysNo("subsysNo", "long");
    private String code;
    private String type;

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

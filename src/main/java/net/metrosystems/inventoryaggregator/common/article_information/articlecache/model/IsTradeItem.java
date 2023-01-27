package net.metrosystems.inventoryaggregator.common.article_information.articlecache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_idu_item_derived_standard.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsTradeItem {
    private Boolean value;
    private Date validFrom;


    public IsTradeItem(IsTradeItemExcludedFromDiscounts isTradeItemExcludedFromDiscounts) {
        this.value = isTradeItemExcludedFromDiscounts.getValue();
        this.validFrom =isTradeItemExcludedFromDiscounts.getValidFrom();
    }

    public IsTradeItem(IsTradeItemInSellOut isTradeItemInSellOut) {
        this.value = isTradeItemInSellOut.getValue();
        this.validFrom =isTradeItemInSellOut.getValidFrom();
    }

    public IsTradeItem(IsTradeItemSellableInPFSShop isTradeItemSellableInPFSShop) {
        this.value = isTradeItemSellableInPFSShop.getValue();
        this.validFrom =isTradeItemSellableInPFSShop.getValidFrom();
    }

    public IsTradeItem(IsTradeItemDiscountMachine isTradeItemDiscountMachine) {
        this.value = isTradeItemDiscountMachine.getValue();
        this.validFrom =isTradeItemDiscountMachine.getValidFrom();
    }

    public IsTradeItem(MerchandiseBrand merchandiseBrand) {
        this.value = merchandiseBrand.getIsMerchandiseOwnBrand();
        this.validFrom =merchandiseBrand.getValidFrom();
    }
}

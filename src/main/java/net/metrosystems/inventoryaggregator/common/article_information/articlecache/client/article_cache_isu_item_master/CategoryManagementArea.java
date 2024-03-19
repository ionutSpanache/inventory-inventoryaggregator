package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "categoryManagementArea")
public class CategoryManagementArea implements Serializable {
    public Long getCategoryManagementAreaNumber() {
        return categoryManagementAreaNumber;
    }

    @XmlElement(name = "categoryManagementAreaNumber")
    public void setCategoryManagementAreaNumber(Long categoryManagementAreaNumber) {
        this.categoryManagementAreaNumber = categoryManagementAreaNumber;
    }

    private Long categoryManagementAreaNumber;
}

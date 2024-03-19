package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "presencePeriodV3")
public class PresencePeriodV3 implements Serializable {

    private Long presencePeriodEndYearV2;
    private Long presencePeriodEndWeekV2;
    private Long presencePeriodStartYearV2;
    private Long presencePeriodStartWeekV2;

    public Long getPresencePeriodEndYearV2() {
        return presencePeriodEndYearV2;
    }

    @XmlElement(name = "presencePeriodEndYearV2")
    public void setPresencePeriodEndYearV2(Long presencePeriodEndYearV2) {
        this.presencePeriodEndYearV2 = presencePeriodEndYearV2;
    }

    public Long getPresencePeriodEndWeekV2() {
        return presencePeriodEndWeekV2;
    }

    @XmlElement(name = "presencePeriodEndWeekV2")
    public void setPresencePeriodEndWeekV2(Long presencePeriodEndWeekV2) {
        this.presencePeriodEndWeekV2 = presencePeriodEndWeekV2;
    }

    public Long getPresencePeriodStartYearV2() {
        return presencePeriodStartYearV2;
    }

    @XmlElement(name = "presencePeriodStartYearV2")
    public void setPresencePeriodStartYearV2(Long presencePeriodStartYearV2) {
        this.presencePeriodStartYearV2 = presencePeriodStartYearV2;
    }

    public Long getPresencePeriodStartWeekV2() {
        return presencePeriodStartWeekV2;
    }

    @XmlElement(name = "presencePeriodStartWeekV2")
    public void setPresencePeriodStartWeekV2(Long presencePeriodStartWeekV2) {
        this.presencePeriodStartWeekV2 = presencePeriodStartWeekV2;
    }
}

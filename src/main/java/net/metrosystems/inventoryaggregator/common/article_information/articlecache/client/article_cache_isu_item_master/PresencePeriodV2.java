package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "presencePeriodV2")
public class PresencePeriodV2 implements Serializable {

    private Long presencePeriodEndYear;
    private Long presencePeriodEndWeek;
    private Long presencePeriodStartYear;
    private Long presencePeriodStartWeek;

    public Long getPresencePeriodEndYear() {
        return presencePeriodEndYear;
    }

    @XmlElement(name = "presencePeriodEndYear")
    public void setPresencePeriodEndYear(Long presencePeriodEndYear) {
        this.presencePeriodEndYear = presencePeriodEndYear;
    }

    public Long getPresencePeriodEndWeek() {
        return presencePeriodEndWeek;
    }

    @XmlElement(name = "presencePeriodEndWeek")
    public void setPresencePeriodEndWeek(Long presencePeriodEndWeek) {
        this.presencePeriodEndWeek = presencePeriodEndWeek;
    }

    public Long getPresencePeriodStartYear() {
        return presencePeriodStartYear;
    }

    @XmlElement(name = "presencePeriodStartYear")
    public void setPresencePeriodStartYear(Long presencePeriodStartYear) {
        this.presencePeriodStartYear = presencePeriodStartYear;
    }

    public Long getPresencePeriodStartWeek() {
        return presencePeriodStartWeek;
    }

    @XmlElement(name = "presencePeriodStartWeek")
    public void setPresencePeriodStartWeek(Long presencePeriodStartWeek) {
        this.presencePeriodStartWeek = presencePeriodStartWeek;
    }
}

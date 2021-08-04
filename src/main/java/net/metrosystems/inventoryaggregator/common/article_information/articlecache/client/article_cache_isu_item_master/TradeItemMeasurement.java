package net.metrosystems.inventoryaggregator.common.article_information.articlecache.client.article_cache_isu_item_master;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "tradeItemMeasurements")
public class TradeItemMeasurement implements Serializable {

    private Double width;
    private Double height;
    private Double depth;

    public Double getWidth() {
        return width;
    }

    @XmlElement(name = "width")
    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    @XmlElement(name = "height")
    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getDepth() {
        return depth;
    }

    @XmlElement(name = "depth")
    public void setDepth(Double depth) {
        this.depth = depth;
    }
}

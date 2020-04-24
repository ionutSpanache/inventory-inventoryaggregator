package net.metrosystems.inventoryaggregator.rawarticledata.model.articlelocation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacemanArticleLocation {

    private String locationId;
    private String aisle;
    private String bay;
    private String shelf;
    private String position;


    public Boolean isEmpty() {

        return this.locationId == null && this.aisle == null && this.bay == null && this.shelf == null && this.position == null;

    }

}

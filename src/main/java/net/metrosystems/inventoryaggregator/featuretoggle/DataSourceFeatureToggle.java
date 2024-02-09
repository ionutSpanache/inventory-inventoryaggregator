package net.metrosystems.inventoryaggregator.featuretoggle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSourceFeatureToggle {

  @Value("#{'${countriesV2DataSource:null}'}")
  private List<String> countriesV2DataSource;

  public boolean isDataSourceActive(String countryCode) {
    return !isEmpty(countriesV2DataSource)
        && (countriesV2DataSource.contains(countryCode)
            || "ALL".equals(countriesV2DataSource.get(0)));
  }
}

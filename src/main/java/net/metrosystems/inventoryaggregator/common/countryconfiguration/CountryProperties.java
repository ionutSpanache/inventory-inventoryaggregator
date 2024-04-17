package net.metrosystems.inventoryaggregator.common.countryconfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.common.countryconfiguration.model.CountrySalesLineCode;
import net.metrosystems.inventoryaggregator.config.ObjectMapperCustom;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CountryProperties {

  public Optional<CountrySalesLineCode> getCountrySalesLineCode(
      String countryCode, String salesLine) {

    try (InputStream inputStream =
        getClass().getClassLoader().getResourceAsStream("countrySalesLineCodesJson")) {
      if (inputStream == null) {
        return Optional.empty();
      }
      List<CountrySalesLineCode> countrySalesLineCodes =
          ObjectMapperCustom.getMapper()
              .readValue(inputStream, new TypeReference<List<CountrySalesLineCode>>() {});
      return countrySalesLineCodes.stream()
          .filter(
              countrySalesLineCode ->
                  countryCode.equals(countrySalesLineCode.getCountryCode())
                      && salesLine.equals(countrySalesLineCode.getSalesLine()))
          .findFirst();

    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }
}

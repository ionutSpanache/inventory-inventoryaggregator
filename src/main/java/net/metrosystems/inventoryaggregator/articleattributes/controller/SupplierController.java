package net.metrosystems.inventoryaggregator.articleattributes.controller;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.SupplierInformation;
import net.metrosystems.inventoryaggregator.articleattributes.service.SupplierService;
import net.metrosystems.inventoryaggregator.config.logging.AddLogs;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.AddTimer;
import net.metrosystems.inventoryaggregator.config.monitoring.annotation.TagField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.metrics.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Timed("ArticleData")
@RequestMapping(value = "/inventoryaggregator")
public class SupplierController {

  private SupplierService supplierService;

  @Autowired
  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  @AddTimer
  @AddLogs
  @GetMapping(
      value =
          "/supplier-information/countrycode/{countrycode}/salesline/{salesline}/supplier/{supplier}/commercialsupplier/{commercialsupplier}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public SupplierInformation getSupplierInformation(
      @TagField(name = "countrycode") @PathVariable("countrycode") String countryCode,
      @TagField(name = "salesline") @PathVariable("salesline") String salesLine,
      @TagField(name = "supplier") @PathVariable("supplier") Long supplierNumber,
      @TagField(name = "commercialsupplier") @PathVariable("commercialsupplier")
          Long commercialSupplier,
      @RequestParam(name = "environment", required = false) Optional<String> environment,
      @RequestParam(name = "storeNo", required = false) Optional<Integer> storeNo)
      throws Exception {

    return supplierService.getSupplierInformation(
        countryCode, salesLine, environment, supplierNumber, commercialSupplier, storeNo);
  }
}

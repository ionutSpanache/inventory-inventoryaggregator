package net.metrosystems.inventoryaggregator.articleattributes.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.metrosystems.inventoryaggregator.articleattributes.model.generaldata.Department;
import net.metrosystems.inventoryaggregator.articlestoremasterdata.model.AclStoreMasterData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeneralDataRepository {

  private final WebClient webClient;

  @Value("${mstore.generaldata.host}")
  private String generalDataHost;

  @Value("${mstore.generaldata.department.path}")
  private String inventoryGeneralDataDepartmentPath;

  private Department getDepartment(
      String countryCode, String salesLine, Integer storeNumber, Integer departmentNumber) {
    return webClient
        .get()
        .uri(
            generalDataHost + inventoryGeneralDataDepartmentPath,
            countryCode,
            salesLine,
            storeNumber,
            departmentNumber)
        .retrieve()
        .bodyToMono(Department.class)
        .onErrorResume(
            error -> {
              log.error(
                  "Could not get the department name based on department number = {}",
                  departmentNumber,
                  error);
              return Mono.empty();
            })
        .block();
  }

  public void enhanceRawArticleStoreMasterDataWithDepartmentName(
      String countryCode,
      String salesLine,
      Integer storeNo,
      AclStoreMasterData aclStoreMasterData) {
    if (aclStoreMasterData != null && aclStoreMasterData.getDepartmentNumber() != null) {
      Department department =
          getDepartment(countryCode, salesLine, storeNo, aclStoreMasterData.getDepartmentNumber());
      if (department != null) {
        aclStoreMasterData.setDepartmentName(department.getDepartmentDescription());
      }
    }
  }
}

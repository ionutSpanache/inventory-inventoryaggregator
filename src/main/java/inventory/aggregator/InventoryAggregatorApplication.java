package inventory.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties
public class InventoryAggregatorApplication {

	public static void main(String[] args) {
        
		SpringApplication.run(InventoryAggregatorApplication.class, args);
	}
	
    @Bean
    public Docket simpleDiffServiceApi() {
      return new Docket(DocumentationType.SWAGGER_2)
      .groupName("aggregator")
      .apiInfo(apiInfo())
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.any())
      .build()
      .pathMapping("/");
     
    }
    
    private ApiInfo apiInfo() {
             return new ApiInfoBuilder()
             .title("Article data aggregator service")
             .description("REST service to collect data on articles from several sources")
             .version("1.0")
             .build();
    }             

}

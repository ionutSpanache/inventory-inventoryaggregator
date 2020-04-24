package net.metrosystems.inventoryaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class InventoryAggregatorApplication {

	public static void main(String[] args) {
        
		SpringApplication.run(InventoryAggregatorApplication.class, args);
	}
}

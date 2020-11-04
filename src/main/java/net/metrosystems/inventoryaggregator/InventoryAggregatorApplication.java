package net.metrosystems.inventoryaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync
public class InventoryAggregatorApplication {

	public static void main(String[] args) {

		SpringApplication.run(InventoryAggregatorApplication.class, args);
	}
}

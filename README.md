# Inventory-Aggregator Project

## Overview

The inventory-inventoryaggregator project is a Spring Boot application designed to manage and retrieve article details for inventory purposes. It interacts with various services such as Article Cache to fetch article data.

## Features
- Retrieve article data
 
## Installation

### Technologies Used

- Java
- Spring Boot
- Maven
- SpringDoc for Swagger API documentation

## Configuration

### Environment Variables

The application can be configured using the `application.properties`. Below are some key configuration properties:

`ARTICLE_CACHE_PASSWORD`: The password for Article Cache.

## Steps

1. Prerequisites
    - Ensure you have Java 17 or higher installed.
    - Install Maven for building the project.

2. Clone the repository:

```bash
git clone https://github.com/metro-digital-inner-source/inventory-inventoryaggregator.git
```

3. Navigate to the project directory:

``` bash
cd inventory-inventoryaggregator
```

4. Build the project

   The build system used is Maven.

```bash
mvn clean install
```

5. Run the project

   To start the microservice, run:

```bash
mvn spring-boot:run
```

6. Running tests

```bash
mvn clean test
```

## Usage
### API Endpoints

### Article Data

- `GET /inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/stores/{storeno}/articles/articlenumber/{articlenumber}/variant/{variant}/bundlenumber/{bundlenumber}`: Get article data
  taking

### Article Store Master Data 

- `GET /inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/stores/{storeNo}/articleNumber/{artNo}/variantNumber/{varNo}/bundleNumber/{bundleNo}/articlemasterdatabyartvarbundle`: Get article store master data
- `GET /inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/stores/{storeNo}/barcode/{barcode}/articlemasterdatabyean`: Get article store master data by EAN
 
### Article Master Data

- `GET /inventoryaggregator/api/v1/countrycode/{countryCode}/languagecode/{languageCode}/storenumber/{storeNumber}/articlemasterdata/articlenumber/{articleNumber}/variantnumber/{variantNumber}/bundlenumber/{bundleNumber}`: Get article master data
- `POST /inventoryaggregator/api/v1/countrycode/{countryCode}/languagecode/{languageCode}/storenumber/{storeNumber}/articlemasterdata/articleids`: Get article master data for multiple articles
 
### Article Id

- `POST /inventoryaggregator/countries/{countryCode}/salesline/{salesLine}/languages/{languageCode}/storeNumber/{storeNumber}`: Get active articles for a certain store
- `GET /inventoryaggregator/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/subsys-article-nunmber/{subsys-article-number}`: Get article id by article subsystem number
- `GET /inventoryaggregator/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/barcode/{barcode}`: Get article id by barcode
- `GET /inventoryaggregator/countries/{countryCode}/saleslines/{salesline}/languages/{languageCode}/stores/{storeno}/article-id/article-number/{articleNumber}`: Get article id by article number
- `GET /inventoryaggregator/countries/{countryCode}/saleslines/{salesLine}/languages/{languageCode}/articlenumber/{articleNumber}/variant/{variant}/bundlenumber/{bundleNumber}`: Get active stores by article number
 
## Error Handling

The application uses custom exception handling:

- `ArticleNotFoundException` : Signals that an article was not found

## External dependencies

* [IDAM PP](https://idam-pp.metrosystems.net)
* [IDAM PROD](https://idam.metrosystems.net)

## Contact Information

For any questions or suggestions, please contact: Mstore-inventory-quantity@metro.digital 
# StockX True-to-Size Sample
True-To-Size Sneaker software sample for StockX

## Pre-Requisites
This application requires Docker to be installed on the target machine. Either a local installation of `gradle` or the 
provided `gradlew` executable can be used to interface with this project's build cycle.
 
## Installation
Clone this repository and download dependencies from Maven Central, ideally by loading the project in a Java IDE. Once 
fully downloaded, the application can be executed by running the command:
```bash
$ gradle bootrun
```
This will pull a postgres image which will automatically load via `docker-compose`. Database connection options can be 
configured in the configuration profile `src/main/resources/application.yml` <sup>1</sup>.

---

## Overview
This project is a web server written in Java 11 which exposes two endpoints:

### 1. `[POST]  /sneakers/crowdsource`
This endpoint expects the following JSON Request Body:

```
{
  "id": string,
  "trueToSizeValue": 1 | 2 | 3 | 4 | 5
}
```

When this endpoint is called with valid data, that data will be persisted to the database's `sneaker-crowdsource-data` 
table; if no data has ever been recorded for the provided `id` (the sneaker identifier), a record will be created in the 
`Sneakers` table for this entity. At this time, `id` is expected to be the name of the shoe, provided in the following 
template<sup>2</sup>:

`<manufacturer> <product name>`  
e.g. `adidas Yeezy`

This endpoint will return the following statuses in the provided scenarios:

* `200` : Operation is successful
* `400` : User has provided a malformed request
* `500` : Error communicating with database

### 2. `[GET]   /sneakers/{ productId }`

This endpoint is called with no request body; the product ID is provided via route parameter. Calling this endpoint will
return:

* `200 [Double]` : The true-to-size value calculated as the average of all crowdsourced data for the 
given `productId`
* `204` : The operation was successful, but no data exists for the provided `productId`
* `500` : Error communicating with database  

---

## Tooling

### Development Framework
This application is built with [Spring Framework](https://github.com/spring-projects/spring-framework), which eases the
development process by providing dependency injection and annotation-driven abstractions for interacting with both the 
database and HTTP Requests. 

### Dependency Manager
This project is using Gradle to execute builds, run automated tests, and manage dependencies.

### Testing
This project uses [JUnit 4](https://junit.org/junit4/) with [AssertJ](https://github.com/joel-costigliola/assertj-core) 
and [Mockito](https://github.com/mockito/mockito). All automated tests can be run by executing the following command:
```bash
$ gradle test
```

### Metrics
This project uses [Prometheus](https://github.com/prometheus/prometheus) for reporting metrics. Provided metrics are 
exposed using Spring AOP.

### Persistence
This application uses Postgres, a SQL database system. Internally, the source code leverages Spring Data JPA for all
database interactions. Currently, database creation configuration is stored in `docker-compose.yml`<sup>1</sup>; 
database connection configuration is stored in `src/main/resources/application.yml`. 

### Containerization
This project is configured to use Jib as part of its build process. First, a container registry must be defined in 
`build.gradle` by configuring `jib.to.image`; all subsequent `gradle build`s will automatically containerize this
application and push to the registry.

Additionally, this project uses a Postgres instance. In order to make this codebase portable to other developer 
machines, `docker-compose` is used to automatically pull and load the Postgres image. To run just the Postgres image,
for example to work with certain IDEs or workflows, run:
```bash
$ docker-compose up
```

---

## Additional Considerations
<sup>1</sup> In an actual project, the `application.yml` containing database connection credentials, as well as the 
credentials stored in `docker-compose.yml` would not be committed to version control. Additionally, a more secure 
password than `postgres` would be used.

<sup>2</sup> Persisting database information with a key as loosely defined as a product name is risky, and properly done
requires data normalization, error approximation, similar-key-lookups, etc. For this type of system, a more digital 
product identifier would be ideal, such as an external UPC or an internal SKU; a product lookup could be provided to 
users via a front-end application. For this project, I've opted out of a complex normalization strategy due to lack of
specification, though the code is written with this type of refactoring in mind.
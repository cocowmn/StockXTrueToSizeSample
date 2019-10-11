# StockX True-to-Size Sample
True-To-Size Sneaker software sample for StockX

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
template:

`<manufacturer> <product name>`  
e.g. `adidas Yeezy`

This endpoint will return the following statuses in the provided scenarios:

* `200` : Operation is successful
* `400` : User has provided a malformed request
* `500` : Error communicating with database

### 2. `[GET]   /sneakers/{ productId }`

This endpoint is called with no request body; the product ID is provided via route parameter. Calling this endpoint will
return:

* `200, { sneaker: String, trueToSizeValue: Number }` : The true-to-size value calculated as the average of all crowdsourced data for the 
given `productId`. This response is formatted as JSON.
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
This project uses [JUnit 4](https://junit.org/junit4/) with [AssertJ](https://github.com/joel-costigliola/assertj-core) and [Mockito](https://github.com/mockito/mockito).

### Metrics
This project uses [Prometheus](https://github.com/prometheus/prometheus) for reporting metrics.

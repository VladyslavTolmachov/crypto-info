# Crypto Investment Recommendations App

## Required software
* Java 11
* Install maven https://maven.apache.org/install.html

## Application configuration:
- Cryptos allowed to use in application can be managed in supported_cryptos.properties file
- File with prices for each crypto should be .csv file with name {crypto.name}_values.csv
- Folder for price files can be managed in supported_cryptos.properties file

## How to build and run the project

```
mvn compile exec:java
```
- default port: 8090
- context path: /app

mvn clean package

docker build --tag=crypto-info-server:latest .

docker run -p8090:8090 crypto-info-server:latest

## Documentation endpoint
````
/v2/api-docs
````

## Endpoints
````
/crypto/{cryptoName}
````
Return all data for {cryptoName} cryptocurrency

---

````
/crypto/limits
````
Return oldest/newest/min/max data for each crypto. 
Result can be managed by request parameters(days, month, years). Values are subtracted from the current date.

---
````
/crypto/limits/{cryptoName}
````
Return oldest/newest/min/max data for {cryptoName} cryptocurrency.
Result can be managed by request parameters(days, month, years). Values are subtracted from the current date.

---
````
/crypto/normalizedRange
````
Return a descending sorted list of all the cryptos, comparing the normalized range (i.e. (max-min)/min)

---
````
/crypto/normalizedRange/highest
````
Return the crypto with the highest normalized range for a specific day.
Request should be with request parameters(days, month, years).


# crypto-average-price

## How to run

```$sh
$ ./gradlew build
$ docker-compose up
```
### Info
The project consists of two applications

* crypto-producer - takes btc price data from exchanges (default from "hitbtc", "bitfinex" and "okcoin") and puts them in
 the 
kafka
* crypto-consumer - calculates the average btc price, sends the result to the topic "btc_weight_average"

All messages in Kafka can be found here: localhost:3030 ([read more](https://github.com/Landoop/fast-data-dev))

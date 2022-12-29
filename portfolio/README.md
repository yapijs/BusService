# BusService

---
### Project Description
1. Ticket draft app is a Spring REST API application that calculates bus ticket prices.
2. It requires API call with ticket and route information. Other data like VAT for current day and route base price (depending on route) are fetched using default service implementations.
3. Child passengers receive 50% discount. Luggage price is 30% of base price.
---
### Configuration
1. Clone this project: `git clone git@github.com:yapijs/BusService.git`
2. Run the project in console: `./gradlew run`
---
### Requirements:
1. Have JRE/JDK 17 installed.
2. If you would like to test API calls, use some tool like Postman.

---
### Endpoint description
1. `http://localhost:8080/api/ticket-prices` - as a request body object of following structure is required:
   * `from` and `to` - case-insensitive start and to locations of for bus ticket (i.e. "Riga").
   * `passangers` - list containing passengers and their luggage count in the following structure:
     * `passengerType` - currently valid values (`adult`, `child`, `pensioner`);
     * `luggageCount` - count of luggage items
   * Example:
```json
{
  "from": "Riga",
  "to": "Vilnius",
  "passengers": [
    {
      "passengerType": "adult",
      "luggageCount": 2
    },
    {
      "passengerType": "child",
      "luggageCount": 1
    }
  ]
}
```
  * This endpoint returns list of strings as ticket information and total price. Example: 
```json
{
    "ticketPrices": [
        "Adult (10.00 EUR + 21%) = 12.10 EUR",
        "2 bags (2 x 10.00 EUR x 30% + 21%) = 7.26 EUR",
        "Child (10.00 EUR x 50% + 21%) = 6.05 EUR",
        "1 bag (10.00 EUR x 30% + 21%) = 3.63 EUR"
    ],
    "totalPrice": "29.04 EUR"
}
```

---
### Tests
1. There are unit tests to verify correctness of the app.
2. Run tests in console: `./gradlew test`
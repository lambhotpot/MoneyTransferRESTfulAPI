# MoneyTransferRESTfulAPI
A Java RESTful API for money transfers between users accounts

## How to run demo app

```
git clone https://github.com/lambhotpot/MoneyTransferRESTfulAPI.git
```

```
mvn clean install
```

```
mvn exec:java
```


# Available Services

The demo app runs the on the below location:
```
http://localhost:8083/money-app
```


| HTTP METHOD        | PATH           |  USAGE |
| ------------- |:-------------|:-----|
| GET     | /money-app/user/{userName} | get user by user name |
| GET     | /money-app/user/all  |   get all users |
| PUT     | /money-app/user/create     |  create a new user |
| POST      | /money-app/user/{userId} | update user |
| DELETE      | /money-app/user/{userId}     | remove user |
| GET     | /money-app/account/{accountId} | get account by accountId |
| GET     | /money-app/account/all  |   get all accounts |
| GET     | /money-app/account/{accountId}/balance  |   get account balance by accountId |
| PUT     | /money-app/account/create     |  create a new account |
| DELETE      | /money-app/account/{accountId}     | remove account by accountId |
| PUT     | /money-app/account/{accountId}/withdraw/{amount}    |  withdraw money from account |
| PUT     | /money-app/account/{accountId}/deposit/{amount}     |  deposit money to account |
| POST     | /money-app//money-app/transaction    |  perform transaction between 2 user accounts |

## Http Status 

```
200 OK: The request has succeeded
```

```
400 Bad Request: The request could not be understood by the server 
```

```
404 Not Found: The requested resource cannot be found
```

```
500 Internal Server Error: The server encountered an unexpected condition 
```

## Sample JSON for User and Account

##### User:
 ```json
{  
   "userName":"yangluo",
   "emailAddress":"yangluo@gmail.com"
} 
```

##### User Account:
```json
{  
   "userName":"yangluo",
   "balance":10.0000,
   "currencyCode":"GBP"
}
```

##### User Transaction:
```json
{  
   "currencyCode":"EUR",
   "amount":100000.0000,
   "fromAccountId":3,
   "toAccountId":4
}
```

## Future Improvements

- Enriched functionality on transaction with fx involved
- Persist user transactions
- Exception handling and error code enhancement
- More Java Docs, Comments, test cases on edge cases
- Adding more logs for monitoring and performance analysis




# clj-havelock

A Clojure library providing support for the [Havelock Investments](https://www.havelockinvestments.com/index.php) [Trading API](https://www.havelockinvestments.com/apidoc.php).

This is alpha software. Do not use in production.

## Usage

### In REPL

    (use 'clj-havelock.core)

### Dependency in project.clj 

    [clj-havelock "0.0.4"]

### In namespace

    (:require [clj-havelock :as hl])

## General API Usage

The following functions do not require an API key.

    (get-ticker) ; basic ticker info (last and units) for all listings 
    (get-ticker :sym "hif") ; only for specific symbol
    (get-ticker-full) ; detailed ticker info for all listings
    (get-ticker-full :sym "hif") ; only for specified symbol
    (get-orderbook)
    (get-orderbook :sym "hif")
    (get-orderbook-full)
    (get-orderbook-full :sym "hif")
    (get-dividends :sym "hif")

## API Authentication

Account and Trading-related functions DO require authentication with a valid API key.

Since Havelock allows granulary key permissions, this library lets you either set a global api key (if you use one), or you can specify the appropriate key when you call a function.  

To set a global api key (for example in REPL):

    (use '[clj-havelock])
    (configure :apikey "abcd")
    (get-portfolio)

Or you can use a specific key, per-function instead:

    (require '[clj-havelock :as hl])
    (get-portfolio :apikey "efgh")

## Account API Usage

All of the following functions accept an optional keyword paramenter :apikey. This key takes precedence over a global apikey if present

    (get-portfolio)
    (get-open-orders)
    (get-balance)
    (deposit-bitcoins)

    (get-transaction-history :limit 5 :sortby "ASC" :sinceid transactionid :sincets timestamp)
    (withdraw-bitcoins :amount amount :address address)

## Trading API Usage

All of the following functions accept an optional keyword paramenter :apikey. This key takes precedence over a global apikey if present

All parameters are required.

    (create-order :sym "hif" :action :buy :price 0.00001 :units 1)
    (create-order :sym "hif" :action :sell :price 10 :units 1)

    (cancel-order :id orderid)

## Bugs

- No tests
- API subject to change

## License

Copyright © 2013 John P. Hackworth

Distributed under the Mozilla Public License Version 2.0

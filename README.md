# clj-havelock

A Clojure library providing support for the [Havelock Investments](https://www.havelockinvestments.com/index.php) [Trading API](https://www.havelockinvestments.com/apidoc.php).

## Usage

### In REPL

    (use 'clj-havelock.core)

### Dependency in project.clj 

    [clj-havelock "0.0.1"]

### In namespace

    (:require [clj-havelock :as hl])

## General API Usage

The following functions do not require an API key.

    (get-ticker) ; basic ticker info (last and units) for all listings 
    (get-ticker :symbol "hif") ; only for specific symbol
    (get-ticker-full) ; detailed ticker info for all listings
    (get-ticker-full :symbol "hif") ; only for specified symbol
    (get-orderbook)
    (get-orderbook :symbol "hif")
    (get-orderbook-full)
    (get-orderbook-full :symbol "hif")
    (get-dividends :symbol "hif")

## API Authentication

Account and Trading-related functions DO require authentication with a valid API key.

Since Havelock allows granulary key permissions, this library lets you either set a global api key (if you use one), or you can specify the appropriate key when you call a function.  

To set a global api key (for example in REPL):

    (require '[clj-havelock :as hl])
    (configure :apikey "abcd")
    (get-portfolio)

Or you can use a specific key, per-function instead:

    (require '[clj-havelock :as hl])
    (get-portfolio :apikey "efgh")

## Account API Usage

_All of the following functions accept an optional keyword paramenter :apikey. This key takes precedence over a global apikey if present_

    (get-portfolio)
    (get-open-orders)
    (get-balance)
    (deposit-bitcoins)

    (get-transaction-history :limit 5 :sortby "ASC|DESC" :sinceid transactionid :sincets timestamp)
    (withdraw-bitcoins :amount amount :address address)

## Trading API Usage

_All of the following functions accept an optional keyword paramenter :apikey. This key takes precedence over a global apikey if present_

    (create-order)       






FIXME

## License

Copyright © 2013 John P. Hackworth

Distributed under the Mozilla Public License Version 2.0

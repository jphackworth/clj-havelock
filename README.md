# clj-havelock

A Clojure library providing support for the [Havelock Investments](https://www.havelockinvestments.com/index.php) [Trading API](https://www.havelockinvestments.com/apidoc.php).

This is alpha software. Do not use in production.

Changes in 0.1.0

- Using cheshire for JSON parsing
- Using (parse-numbers) from cryptick to ensure all returned numbers are numbers
- Removed option for setting global apikey. API key must now be included with all authenticated requests
- Withdraw bitcoins functionality has been disabled until I implement address validation
- Most functions are documented

## Usage

API calls now use http-kit promises. Use @(function) to block for results.

Example:

```clojure
user=> (use `clj-havelock.core)
nil
user=> (get-ticker "HIF")
#<core$promise$reify__6310@65a8d7fd: :pending>
user=> @(get-ticker "HIF")
{:HIF {:last 5.2002E-4, :units 1717201.0}}
user=> 
```

### In REPL

    (use 'clj-havelock.core)

### Dependency in project.clj 

    [clj-havelock "0.1.0"]

## General API Usage

The following functions do not require an API key.

```clojure
@(get-ticker) ; basic ticker info (last and units) for all listings 
@(get-ticker "HIF") ; only for specific symbol
@(get-ticker-full) ; detailed ticker info for all listings
@(get-ticker-full "HIF") ; only for specified symbol
@(get-orderbook)
@(get-orderbook "HIF")
@(get-orderbook-full)
@(get-orderbook-full "HIF")
@(get-dividends "HIF")
@(get-trade-history "HIF")
```

## API Authentication

Account and Trading-related functions require authentication with an API key with appropriate permissions.

All authenticated requests must include the api key as the first argument.

## Account API Usage

```clojure
(def apikey "1234...")
@(get-portfolio apikey)
@(get-open-orders apikey)
@(get-balance apikey)
@(deposit-bitcoins apikey)

@(get-transaction-history apikey {:limit 5 :sortby "DESC"})
```

## Trading API Usage

```clojure
(def apikey "1234...")
@(create-order apikey "HIF" {:action :buy :price 0.00001 :units 1})
@(create-order apikey "HIF" {:action :sell :price 10 :units 1})
@(cancel-order apikey :id 1234)
```

**Example**:

```clojure
user=> (use `clj-havelock.core)
nil
user=> (def apikey "1234...")
#'user/k
user=> @(create-order k "PETA" {:action :sell :price 0.4 :units 5})
{:status "ok", :id 1, :apirate 1.0}
user=> @(cancel-order apikey 1)
{:status "ok", :apirate 3.0}

```

## Bugs

- No tests
- API subject to change

## License

Copyright © 2014 John P. Hackworth

Distributed under the Mozilla Public License Version 2.0

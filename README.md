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

- (get-ticker) ; basic ticker info (last and units) for all listings 
- (get-ticker :symbol "hif") ; only for specific symbol
- (get-ticker-full) ; detailed ticker info for all listings
- (get-ticker-full :symbol "hif") ; only for specified symbol
- (get-orderbook)
- (get-orderbook :symbol "hif")
- (get-orderbook-full)
- (get-orderbook-full :symbol "hif")





FIXME

## License

Copyright © 2013 John P. Hackworth

Distributed under the Mozilla Public License Version 2.0

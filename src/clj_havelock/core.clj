; Copyright (C) 2014 John. P Hackworth
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns clj-havelock.core
 (:require [cheshire.core :refer :all]
           [clojure.string :refer [upper-case lower-case]]
           [org.httpkit.client :as http]))

(defn parse-numbers
  "JSON reader helper which implements string to double decoding. This
  function is used to normalize string encoded numerics into raw JVM
  numerics for better use from Clojure clients."
  [m]
  (into {}
        (for [[k v] m]
          (if (= :id k) 
            [k (if (string? v) (read-string v) v)] ; specific to handle order ids
            [k
             (cond (map? v)
                     (parse-numbers v)
                     
                   (vector? v)
                     (into [] (map parse-numbers v))

                   (or (nil? v)
                       (number? v))
                     v

                   (and (string? v)
                        (->> v
                             (re-matches #"^(\d*\.?\d*)$")))
                     (Double/parseDouble v)

                   true
                     v)]))))

(defn url-for [base-url cmd] (format "%s/%s" base-url cmd))
 
(defn default-options []
  {:url "https://www.havelockinvestments.com/r"
   :http-options 
     {:method :post
      :content-type "application/json"
      :user-agent "clj-havelock 0.1.0"
      :insecure? false 
      :keepalive 300
      :form-params nil}}) 

(defn callback
  [{:keys [status headers body error opts] :as response}]

  (if error
    (-> "Request failed, error: %s"
        (format error)
        (Exception.)
        (throw))

    (case status
      200 (let [data (parse-string body true)]
            (if-not (nil? data)
              (parse-numbers data)))

      (-> "Request failed, response code: %s"
          (format status)
          (Exception.)
          (throw)))))
 
(defn api-call 
  "api-call is used for authenticated, trade api calls.

  It takes an optional map of parameters as an argument.

  You should not use api-call directly, as it requires correctly formatted parameters.
  "
  [{:keys [cmd params] :as request} & [options]]
  (let [options (if (nil? options) (default-options) options)
        http-options (assoc (get options :http-options) :form-params params)
        url (url-for (get options :url) cmd)]
    
    (http/post url http-options callback)))

;
; General API 
;

(defn get-ticker [& [sym]]
  "Get basic ticker information from Havelock for one or all listed funds 
  
  Optional parameters:
  - fund symbol: symbol for specific fund you want information for
  
  Example:
  
    @(get-ticker)
    @(get-ticker \"HIF\")
  
  Returns a vector of basic ticker information for all funds (or only specific fund if requested)" 
  (api-call {:cmd "ticker" 
             :params 
               {:symbol sym}}))

(defn get-ticker-full [& [sym]]
  "Get full ticker information from Havelock for one or all listed funds
  
  Optional parameters:
  - fund symbol: symbol for specific fund you want information for
  
  Example:
  
    @(get-ticker-full)
    @(get-ticker-full \"HIF\")
  
  Returns a vector of basic ticker information for all funds (or only specific fund if requested)"  
  (api-call {:cmd "tickerfull" 
             :params 
               {:symbol sym}}))

(defn get-orderbook [sym]
  "Get basic order book for specific fund
  
  Required parameters:
  - fund symbol: symbol for specific fund you want information for
  
  Example:
  
    @(get-order-book \"HIF\")
  
  Returns a map with following keys:
  - :status ('ok' or 'error') 
  - :apirate API usage for past 600 seconds 
  - :message error message if applicable 
  - :bids vector of bids as price/amount pairs 
  - :asks vector of asks as price/amount pairs" 
  (api-call {:cmd "orderbook" 
             :params 
               {:symbol sym}}))

(defn get-orderbook-full [sym]
  "Get full order book for specific fund
  
  Required parameters:
  - fund symbol: symbol for specific fund you want information for
  
  Example:
  
    @(get-orderbook-full \"HIF\")
  
  Returns a map with following keys:
  - :status ('ok' or 'error') 
  - :apirate API usage for past 600 seconds 
  - :message error message if applicable 
  - :bids vector of bids including id, price and amount 
  - :asks vector of asks including id, price and amount" 
  (api-call {:cmd "orderbookfull" 
             :params 
               {:symbol sym}}))

(defn get-dividends [sym]
  "Get dividend history for the specified fund 
  
  Required parameters:
  - fund symbol: symbol for specific fund you want information for 
  
  Example:
  
    @(get-dividends \"AM1\")
  
  Returns a map with following keys:
  - :status ('ok' or 'error')
  - :message error message if applicable 
  - :apirate API usage for past 600 seconds 
  - :dividends vector of dividend information"
  (api-call {:cmd "dividends" 
             :params 
               {:symbol sym}}))

;
; Account API 
;

(defn get-portfolio
  "Returns your Havelock Investments Portfolio
  
  Required argument:
  - Havelock API key (string) with Portfolio permissions 
  
  See: https://www.havelockinvestments.com/api.php 
  
  Example:
  
    @(get-portfolio apikey)
  
  Returns a map with following keys:
  - :status ('or' or 'error')
  - :message Error message if applicable
  - :apirate API usage rate for past 600 seconds
  - :portfolio vector of your fund investments as maps  "
   
  [apikey]
  (api-call {:cmd "portfolio" 
             :params 
               {:key apikey}}))

(defn get-open-orders
  "Returns your open orders
  
  Required argument:
  - Havelock API key (string) with Open Orders permissions (string) 
  
  See: https://www.havelockinvestments.com/api.php
  
  Example:
  
    @(get-open-orders apikey)
  
  Returns a map with following keys:
  - :status ('or' or 'error')
  - :message Error message if applicable
  - :apirate API usage rate for past 600 seconds
  - :orders vector of open orders as maps "
  [apikey]
  (api-call {:cmd "orders" 
             :params 
               {:key apikey}}))

(defn get-balance
  "Retrieve your Havelock account balance (bitcoin)
  
  Required argument:
  - Havelock API key (string) with Balance permissions 
  
  See: https://www.havelockinvestments.com/api.php
  
  Example:
  
    @(get-balance apikey)
  
  Returns a map with following keys:
  - :status ('or' or 'error')
  - :message Error message if applicable
  - :apirate API usage rate for past 600 seconds
  - :balance your balance in bitcoin"
  [apikey]
  (api-call {:cmd "balance" 
             :params 
               {:key apikey}}))


(defn deposit-bitcoins [apikey]
  "Request the (bitcoin) address to use when making a deposit
  
  Required argument:
  - Havelock API key (string) with Deposit Bitcoins permissions 
  
  See: https://www.havelockinvestments.com/api.php
  
  Example:
  
    @(deposit-bitcoins apikey)
  
  Returns a map with following keys:
  - :status ('or' or 'error')
  - :message Error message if applicable
  - :apirate API usage rate for past 600 seconds
  - :address The address for depositing bitcoin into your Havelock account"
  
   (api-call {:cmd "deposit" 
              :params 
                {:key apikey}}))


(defn get-trade-history 
  "Retrieve trade history for a specific fund
  
  Required arguments:
  - symbol: Fund symbol you want trade history for
  
  See: https://www.havelockinvestments.com/api.php
  
  Optional argument - a map with following keys:
  - :dtstart Date/Time to start from (yyyy-mm-dd hh:mm:ss) 
  - :dtend Date/Time to end at (yyyy-mm-dd hh:mm:ss) 
  
  See: https://www.havelockinvestments.com/apidoc.php
  
  Examples:
  
    @(get-trade-history \"HIF\")
    @(get-trade-history \"HIF\" {:dtstart \"2014-01-01 00:00:00\"})
    @(get-trade-history \"HIF\" {:dtstart \"2014-01-01 00:00:00\"
                                :dtend \"2014-02-01 00:00:00\"})
  
  Returns a map with following keys:
  - :status ('or' or 'error')
  - :message Error message if applicable
  - :apirate API usage rate for past 600 seconds
  - trades vector of trades as maps"
  [sym & [{:keys [dtstart dtend] :as opts}]]
  (api-call {:cmd "trades"
             :params {:symbol sym
                      :dtstart dtstart
                      :dtend dtend}}))

(defn get-transaction-history
  "Retrieve your transaction history 
  
  Required argument:
  - Havelock API key (string) with Transaction History permissions
  
  See: https://www.havelockinvestments.com/api.php
  
  Optional arguments:
  - A parameter map with optional keys: 
    - limit: Max results returned 
    - sortby: ASC or DESC (default DESC) 
    - sinceid: Show transactions since (but not including) transaction id
    - sincets: Show transactions since and including specific time 
  
  See: https://www.havelockinvestments.com/apidoc.php
  
  Examples:
  
    @(get-transaction-history apikey)
    @(get-transaction-history apikey {:limit 5})
    @(get-transaction-history apikey {:limit 5 :sortby ASC})
  
  Returns a map with following keys:
  - :status ('or' or 'error')
  - :message Error message if applicable
  - :apirate API usage rate for past 600 seconds
  - transactions vector of transactions as maps "
  [apikey & [{:keys [limit sortby sinceid sincets] :as opts}]]
  (api-call {:cmd "transactions" :params {:key apikey 
                                          :limit limit 
                                          :sort sortby 
                                          :sinceid sinceid 
                                          :sincets sincets}}))
;
; Trading API
;

(defn create-order
  "Create an order on Havelock 
  
  Required arguments:
  - apikey - Havelock API key (string) with Create Order permissions
  - fund symbol (string)
  - action (string) 'buy' or 'sell'
  - price (float) price in bitcoins per unit 
  - units: quantity of units 
  
  Example: 
  
    @(create-order apikey \"HIF\" \"buy\" 0.000001 1)

  Returns a map with following keys: 
  - :status ('ok' or 'error') 
  - :message error message if application 
  - :apirate API usage rate for past 600 seconds
  - :id Order ID if successful "
    
  [apikey sym {:keys [action price units] :as opts}]
  (api-call {:cmd "ordercreate" 
             :params 
               {:key apikey 
                :symbol sym 
                :action (name action)  
                :price price 
                :units units}}))

(defn cancel-order
  "Cancel a specific open order on Havelock 
  
  Required arguments:
  - apikey: API key (string) with Cancel Order permissions 
  - id: Open order ID to cancel (number)
  
  Example:
  
    @(cancel-order apikey 12345)
  
  Returns a map with the following keys:
  - :status ('ok' or 'error')
  - :apirate API usage rate for past 600 seconds 
  - :message error message if applicable   "
  [apikey id] 
  (api-call {:cmd "ordercancel" 
             :params 
               {:key apikey
                :id id}}))

; FIXME: Disabled till better testing/validation of addresses added
; (defn withdraw-bitcoins
;   [apikey amount address address_confirmation] 
;   (let [params {:key apikey 
;    :amount amount 
;    :address address}]
;   (api-call {:cmd "withdraw" :params params)))



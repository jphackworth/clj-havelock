; Copyright (C) 2013 John. P Hackworth
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns clj-havelock.core
 (:require [clojure.data.json :as json]
  [org.httpkit.client :as http])
 (:use [clojure.java.io]
       [clojure.string :only [upper-case lower-case]]))

(def url "https://www.havelockinvestments.com/r")
(def config (atom {:apikey nil :url nil}))

(defn url-for [cmd] (format "%s/%s" url cmd))
 
(defn post-data 
  "post-data is used for authenticated, trade api calls.

  It takes an optional map of parameters as an argument.

  You should not use post-data directly, as it requires correctly formatted parameters.
  "
  [& {:keys [cmd params]
    :or {params nil}}]

    (let [options {:method :post
      :content-type "application/json"
      :user-agent "clj-havelock 0.0.1"
      :insecure? false
      :form-params (into {} (filter second params))
      :keepalive 30000}]
      (let [response @(http/post (url-for cmd) options)]
        (case (response :status)
          200 (json/read-str (response :body) :key-fn keyword)
          nil))))


; General API 
;
; General Request assumes unauthenticated request, 
; and only optional parameter is symbol

(defn get-ticker [& {:keys [symbol]}] 
  (post-data :cmd "ticker" :params {:symbol symbol}))
(defn get-ticker-full [& {:keys [symbol]}] 
  (post-data :cmd "tickerfull" :params {:symbol symbol}))
(defn get-orderbook [& {:keys [symbol]}]
  (post-data :cmd "orderbook" :params {:symbol symbol}))
(defn get-orderbookfull [& {:keys [symbol]}]
  (post-data :cmd "orderbookfull" :params {:symbol symbol}))
(defn get-dividends [& {:keys [symbol]}]
  (post-data :cmd "dividends" :params {:symbol symbol}))

; Account API 

(defn get-portfolio 
  [& {:keys [apikey]
      :or {apikey (@config :apikey)}}]
  (post-data :cmd "portfolio" :params {:key apikey}))
(defn get-open-orders
  [& {:keys [apikey]
      :or {apikey (@config :apikey)}}] 
  (post-data :cmd "orders" :params {:key apikey}))
(defn get-balance
  [& {:keys [apikey]
      :or {apikey (@config :apikey)}}]
  (post-data :cmd "balance" :params {:key apikey}))
(defn deposit-bitcoins [& {:keys [apikey]
      :or {apikey (@config :apikey)}}]
   (post-data :cmd "deposit" :params {:key apikey}))

(defn get-transaction-history
  [& {:keys [apikey limit sortby sinceid sincets]
      :or {apikey (@config :apikey)}}]
  (let [params {:key apikey
   :limit limit 
   :sort sortby 
   :sinceid sinceid 
   :sincets sincets}]   
  (post-data :cmd "transactions" :params params)))

(defn withdraw-bitcoins
  [& {:keys [apikey amount address] 
       :or {apikey (@config :apikey)}}]
  (let [params {:key apikey 
   :amount amount 
   :address address}]
  (post-data :cmd "withdraw" :params params)))

; Trading API

(defn create-order
  [& {:keys [apikey asset action price units]
       :or {apikey (@config :apikey)}}]
  (let [params {:key apikey
    :symbol (upper-case (name asset)) 
    :action (lower-case (name action))
    :price price
    :units units}]
  (post-data :cmd "ordercreate" :params params)))

(defn cancel-order
  [& {:keys [apikey id] 
       :or {apikey (@config :apikey)}}]
  (let [params {:key apikey  
    :id id}]
  (post-data :cmd "ordercancel" :params params)))

(defn configure [& {:keys [apikey]}]
  (if-not (nil? apikey) (swap! config assoc :apikey apikey)))


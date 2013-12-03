; Copyright (C) 2013 John. P Hackworth
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns clj-havelock.core
   (:require [clojure.data.json :as json])
  (:require [org.httpkit.client :as http])
  (:use [clojure.java.io]))

(def url "https://www.havelockinvestments.com/r")

(defn url-for [cmd] (format "%s/%s" url cmd))
(defn api-get 
  [& {:keys [cmd params]
    :or {params nil}}]
  (def options {:method :post 
    :content-type "application/json"
    :user-agent "clj-havelock 0.0.1"
    :form-params params
    :insecure? false })
    @(http/post (url-for cmd) options))

; General API 
;
; General Request assumes unauthenticated request, 
; and only optional parameter is symbol

(defn general-request
  [cmd asset]
    (if (not (empty? asset))
      (json/read-str ((api-get :cmd cmd 
        :params {:symbol (clojure.string/upper-case (name (nth asset 0)))}) :body))
      (json/read-str ((api-get :cmd cmd) :body))))

(defn get-ticker [& args] (general-request "ticker" args))
(defn get-ticker-full [& args] (general-request "tickerfull" args))
(defn get-orderbook [& args] (general-request "orderbook" args))
(defn get-orderbookfull [& args] (general-request "orderbookfull" args))
(defn get-dividends [& args] (general-request "dividends" args))

; Account API 

(defn account-request
  [cmd params]
  (json/read-str ((api-get :cmd cmd :params params) :body)))

(defn get-portfolio [& {:keys [apikey]}] (account-request "portfolio" {:key apikey}))
(defn get-orders [& {:keys [apikey]}] (account-request "orders" {:key apikey})) 
(defn get-balance [& {:keys [apikey]}] (account-request "balance" {:key apikey}))
(defn deposit-bitcoins [& {:keys [apikey]}] (account-request "deposit" {:key apikey}))

(defn get-transaction-history
  [& {:keys [apikey limit sortby sinceid sincets]}]
  (json/read-str ((api-get :cmd "orders" 
    :params {:key apikey :limit limit :sort sortby :sinceid sinceid :sincets sincets}) 
    :body)))

(defn withdraw-bitcoins
  [& {:keys [apikey amount address]}]
  (json/read-str ((api-get :cmd "withdraw" 
    :params {:key apikey :amount amount :address address}) 
    :body)))

; Trading API

(defn create-order
  [& {:keys [apikey asset action price units]}]
  (json/read-str ((api-get :cmd "ordercreate" 
    :params {:key apikey :symbol (clojure.string/upper-case (name asset)) :action (clojure.string/lower-case (name action)) :price price :units units}) 
    :body)))

(defn cancel-order
  [& {:keys [apikey id]}]
  (json/read-str ((api-get :cmd "ordercancel" 
    :params {:key apikey :id id}) 
    :body)))


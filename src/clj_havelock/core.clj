; Copyright (C) 2013 John. P Hackworth
;
; This Source Code Form is subject to the terms of the Mozilla Public
; License, v. 2.0. If a copy of the MPL was not distributed with this
; file, You can obtain one at http://mozilla.org/MPL/2.0/.

(ns clj-havelock.core
   (:require [clojure.data.json :as json])
  (:require [org.httpkit.client :as http])
  (:use [clojure.java.io])

  )

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
    @(http/post (url-for cmd) options)
    )

(defn get-ticker [& args] 
  (if (not (seq args))
    (json/read-str ((api-get :cmd "ticker") :body))
    (json/read-str ((api-get :cmd "ticker" :params {:symbol (clojure.string/upper-case (name (nth args 0)))} ) :body)))
  )

(defn get-ticker-full [& args]
  (if (not (seq args))
    (json/read-str ((api-get :cmd "tickerfull") :body))
    (json/read-str ((api-get :cmd "tickerfull" :params {:symbol (clojure.string/upper-case (name (nth args 0)))} ) :body)))
  )
  

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

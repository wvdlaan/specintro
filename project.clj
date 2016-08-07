(defproject specintro "0.1.0-SNAPSHOT"
  :description "Introduction to clojure.spec"
  :url "https://github.com/wvdlaan/specintro"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.3"
  
  :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                 [org.clojure/clojurescript "1.9.89"]
                 [devcards "0.2.1-7"]
                 [sablono "0.7.3"]
                 
                 ;; need to specify this for sablono
                 ;; when not using devcards
                 [cljsjs/react "15.3.0-0"]
                 [cljsjs/react-dom "15.3.0-0"]
                 [cljsjs/react-dom-server "15.3.0-0"]
                 ]

  :plugins [[lein-figwheel "0.5.4-7"]
            [lein-cljsbuild "1.1.3" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]
  
  :source-paths ["src"]

  :cljsbuild {
              :builds [{:id "devcards"
                        :source-paths ["src"]
                        :figwheel { :devcards true } ;; <- note this
                        :compiler { :main       "specintro.core"
                                    :asset-path "js/compiled/devcards_out"
                                    :output-to  "resources/public/js/compiled/specintro_devcards.js"
                                    :output-dir "resources/public/js/compiled/devcards_out"
                                    :source-map-timestamp true }}
                       {:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main       "specintro.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/specintro.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true }}
                       {:id "hostedcards"
                        :source-paths ["src"]
                        :compiler {:main       "specintro.core"
                                   :devcards   true
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/specintro.js"
                                   :optimizations :advanced}}]}

  :figwheel { :css-dirs ["resources/public/css"] })

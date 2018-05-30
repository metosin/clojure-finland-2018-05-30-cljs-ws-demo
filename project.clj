(defproject jarppe/clojure-finland-cljs-ws-demo "0.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]

                 ; Server:
                 [jarppe/syksy "0.0.4"]
                 [org.clojure/core.async "0.4.474"]
                 [prismatic/plumbing "0.5.5"]
                 [metosin/potpuri "0.5.1"]

                 ; Eines:
                 [metosin/eines "0.0.9"]

                 ; ClojureScript:
                 [org.clojure/clojurescript "1.10.238"]
                 [reagent "0.8.1"]
                 [binaryage/devtools "0.9.7"]
                 [metosin/reagent-dev-tools "0.1.0"]]

  :min-lein-version "2.8.1"
  :source-paths ["src/clj" "src/cljs"]
  :test-paths ["test/clj"]
  :target-path "target/dev"
  :auto-clean false

  :sass {:source-paths ["src/sass"]
         :source-map true
         :output-style :compressed}

  :figwheel {:css-dirs ["target/dev/resources/public/css"]
             :repl false}

  :plugins [[lein-pdo "0.1.1"]
            [deraen/lein-sass4clj "0.3.1"]
            [lein-figwheel "0.5.15"]
            [lein-cljsbuild "1.1.7"]]

  :profiles {:dev {:dependencies [[metosin/testit "0.2.2"]]
                   :resource-paths ["target/dev/resources"]
                   :sass {:target-path "target/dev/resources/public/css"}}
             :uberjar {:target-path "target/prod"
                       :source-paths ^:replace ["src/clj" "src/cljs"]
                       :resource-paths ["target/prod/resources"]
                       :sass {:target-path "target/prod/resources/public/css"}
                       :main syksy.main
                       :aot :all
                       :uberjar-name "app.jar"}}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel true
                        :compiler {:main "app.front.main"
                                   :asset-path "asset/js/out"
                                   :output-to "target/dev/resources/public/js/main.js"
                                   :output-dir "target/dev/resources/public/js/out"
                                   :closure-defines {goog.DEBUG true}
                                   :preloads [devtools.preload]
                                   :external-config {:devtools/config {:features-to-install [:formatters :hints]}}
                                   :optimizations :none
                                   :source-map true
                                   :cache-analysis true
                                   :pretty-print true}}
                       {:id "prod"
                        :source-paths ["src/cljc" "src/cljs"]
                        :compiler {:main "app.front.main"
                                   :output-to "target/prod/resources/public/js/main.js"
                                   :closure-defines {goog.DEBUG false}
                                   :optimizations :advanced}}]}

  :aliases {"dev" ["do"
                   ["clean"]
                   ["pdo"
                    ["sass4clj" "auto"]
                    ["figwheel"]]]
            "uberjar" ["with-profile" "uberjar" "do"
                       ["clean"]
                       ["sass4clj" "once"]
                       ["cljsbuild" "once" "prod"]
                       ["uberjar"]]})

(defproject shoreleave/demo-shoreleave-solr "0.1.0"
  :description "An example application using Noir, Shoreleave, and SOLR"
  :url "http://github.com/shoreleave"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "See the notice in README.mkd or details in LICENSE_epl.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/core.memoize "0.5.1"]
                 [noir "1.3.0-beta10"]
                 [enlive "1.0.1"]
                 [amalloy/ring-gzip-middleware "0.1.2"]
                 [ring-anti-forgery "0.1.3"]
                 ;[shoreleave "0.2.2-SNAPSHOT"]
                 [shoreleave/shoreleave-remote-noir "0.2.2-SNAPSHOT"]
                 [enfocus "0.9.1-SNAPSHOT"]
                 [jayq "0.1.0-alpha4"]]
  :dev-dependencies [[vimclojure/server "2.3.3" :exclusions [org.clojure/clojure]] 
                     ;[cdt "1.2.6.2"]
                     ;[lein-cdt "1.0.0"] ; use lein cdt to attach
                     ;[lein-autodoc "0.9.0"]
                     [lein-marginalia "0.7.1"]]
  :plugins  [[lein-cljsbuild "0.2.4"]
             [jonase/kibit "0.0.4"]
             [lein-catnip "0.1.0"]]
  :cljsbuild {:builds [{:source-path "src",
                        :compiler {:output-dir "resources/public/cljs",
                                   :output-to "resources/public/cljs/findit.js",
                                   :externs  ["externs/jquery.js"],
                                   :optimizations :simple,;:advanced ;:whitespace
                                   :pretty-print true}}]} 
  :warn-on-reflection false
  ; JVM options for Debugging with CDT
  ;:jvm-opts ["-agentlib:jdwp=transport=dt_socket" "server=y" "suspend=n" "address=8030"]

  ; Different JVM options for performance
  ;:jvm-opts ["-Xmx1g"]
  ; JDK 1.7
  ;:jvm-opts ["-d64" "-server" "-XX:+UseG1GC" "-XX:+ExplicitGCInvokesConcurrent" "-XX:+UseCompressedStrings"]
  ; JDK 1.6
  ;:jvm-opts ["-d64" "-server" "-XX:+UseConcMarkSweepGC" "-XX:+UseParNewGC" "-XX:+UseCompressedOops" "-XX:+ExplicitGCInvokesConcurrent"]
  ;:jvm-opts ["-server" "-Xmx1g" "-XX:+UseConcMarkSweepGC" "-XX:+UseParNewGC" "-XX:+UseCompressedOops"]
  ;:jvm-opts ["-server" "-Xmx50mb" "-XX:+UseConcMarkSweepGC" "-XX:+UseParNewGC" "-XX:+UseCompressedOops"]
  :main findit.server)


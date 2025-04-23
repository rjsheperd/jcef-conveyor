(ns jcef-conveyor.setup
  (:import [me.friwi.jcefmaven CefAppBuilder]
           [java.nio.file Paths])
  (:require [clojure.string :as str]))

(defn- get-jcef-dir []
  (let [app-dir (System/getProperty "app.dir")
        os      (str/lower-case (System/getProperty "os.name"))]
    (if (nil? app-dir)
      ;; Dev mode
      (Paths/get ".jcef-bundle")
      ;; Packaged with Conveyor
      (let [app-dir-path (Paths/get app-dir)
            jcef-dir (cond
                       (str/starts-with? os "mac")
                       (.. app-dir-path (resolve "../Frameworks") (normalize)) 

                       (str/starts-with? os "windows")
                       (.resolve app-dir-path "jcef")

                       :else
                       (.resolve app-dir-path "jcef"))]
        (cond
          (str/starts-with? os "mac")
          (when-not (.exists (.resolve jcef-dir "jcef Helper.app"))
            (throw (IllegalStateException. "jcef Helper.app not found")))

          (str/starts-with? os "windows")
          (when-not (.exists (.resolve jcef-dir "jcef.dll"))
            (throw (IllegalStateException. "jcef.dll not found")))

          :else
          (when-not (.exists (.resolve jcef-dir "libjcef.so"))
            (throw (IllegalStateException. "libjcef.so not found"))))
        jcef-dir))))

(defn jcef-builder []
  (let [jcef-dir (get-jcef-dir)
        builder  (CefAppBuilder.)]
    (.setInstallDir builder (.toFile jcef-dir))
    builder))

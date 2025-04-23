(ns jcef-conveyor.core
  (:require [jcef-conveyor.setup :refer [jcef-builder]])
  (:import [me.friwi.jcefmaven MavenCefAppHandlerAdapter]
           [org.cef CefApp CefApp$CefAppState]
           [org.cef.browser CefBrowser CefFrame CefMessageRouter]
           [org.cef.handler CefDisplayHandlerAdapter CefFocusHandlerAdapter]
           [java.awt BorderLayout Component KeyboardFocusManager]
           [java.awt.event ActionListener WindowAdapter]
           [javax.swing JFrame JTextField]))

(defn create-sample-frame [start-url use-osr is-transparent]
  (let [builder (jcef-builder)
        _       (set! (.. builder -cefSettings -windowless_rendering_enabled) use-osr)
        _       (.setAppHandler (proxy [MavenCefAppHandlerAdapter] []
                            (stateHasChanged [state]
                              (when (= state CefApp$CefAppState/TERMINATED)
                                (System/exit 0)))))
        cef-app       (.build builder)
        client        (.createClient cef-app)
        msg-router    (CefMessageRouter/create)
        _             (.addMessageRouter client msg-router)
        browser       (.createBrowser client start-url use-osr is-transparent)
        browser-ui    (.getUIComponent browser)
        address       (doto (JTextField. start-url 100)
                        (.addActionListener (proxy [ActionListener] []
                                              (actionPerformed [_]
                                                (.loadURL browser start-url)))))
        browser-focus (atom true)]
    
    (.addDisplayHandler client (proxy [CefDisplayHandlerAdapter] []
                                 (onAddressChange [_ _ url]
                                   (.setText address url))))
    
    (.addFocusHandler client (proxy [CefFocusHandlerAdapter] []
                               (onGotFocus [_]
                                 (when-not @browser-focus
                                   (reset! browser-focus true)
                                   (.clearGlobalFocusOwner (KeyboardFocusManager/getCurrentKeyboardFocusManager))
                                   (.setFocus browser true)))
                               (onTakeFocus [_ _]
                                 (reset! browser-focus false))))
    
    (doto (JFrame. "Sample")
      (.getContentPane)
      (.add address BorderLayout/NORTH)
      (.add browser-ui BorderLayout/CENTER)
      (.pack)
      (.setSize 1024 768)
      (.setVisible true)
      (.addWindowListener (proxy [WindowAdapter] []
                            (windowClosing [_]
                              (.dispose (CefApp/getInstance))))))))

(defn -main [& args]
  (create-sample-frame "https://www.google.com" false false)

  (def my-frame (JFrame. "Sample"))
  (def content-pane (.getContentPane my-frame))
  (.pack my-frame)
  (.setSize my-frame 1024 768)
  (.pack my-frame)
  (.setVisible my-frame true)
  (.. my-frame
    
    (.add ))

  )

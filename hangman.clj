#!/usr/bin/env bb

;; Adapted from Graham Hutton's Haskell book (10.6)

(defn match [word guess]
  (apply str
    (for [ch word]
      (if ((set guess) ch) ch \-))))

(defn play [word]
  (print "? ")
  (let [guess (read-line)]
    (if (= guess word)
      (println "You got it!")
      (do
        (println (match word guess))
        (play word)))))

(defn hangman []
  (println "(First person) Think of a word:")
  (let [word (apply str (.readPassword (System/console)))]
    (println "(Second person) Try to guess it:")
    (play word)))

(hangman)

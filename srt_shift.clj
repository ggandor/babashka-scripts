#!/usr/bin/env bb

(def msecs-in-sec 1000)
(def msecs-in-min (* 60 1000))
(def msecs-in-hour (* 60 60 1000))
(def secs-in-min 60)
(def mins-in-hour 60)
(def timecode-pattern #"(\d\d):(\d\d):(\d\d),(\d\d\d)")
(def err-msg "The given time shift would result in negative timecode values.")

(defn timecode->millisecs [match]
  (let [[hours mins secs msecs] (map #(Integer/parseInt %) (rest match))]
    (+ (* hours msecs-in-hour) (* mins msecs-in-min) (* secs msecs-in-sec) msecs)))

(defn millisecs->timecode [n]
  (when-not (pos? n)
    (throw (IllegalArgumentException.))
  (let [hours (-> n (/ msecs-in-hour) int str (format "%02d"))
        mins (-> n (/ msecs-in-min) int (mod mins-in-hour) str (format "%02d"))
        secs (-> n (/ msecs-in-sec) int (mod secs-in-min) str (format "%02d"))
        msecs (-> n (mod msecs-in-sec) str (format "%03d"))]
    (str hours ":" mins ":" secs "," msecs))))

(defn shifted-timecode [shift timecode-match]
  (-> timecode-match timecode->millisecs (+ shift) millisecs->timecode))

(defn shifted-srt [shift text]
  (clojure.string/replace text timecode-pattern #(shifted-timecode shift %)))

(let [[src-path shift out-path] *command-line-args*
      src-text (slurp src-path)
      shift (Integer/parseInt shift)
      out-path (or out-path src-path)]
  (when-let [out-text (try 
                        (shifted-srt shift src-text)
                        (catch IllegalArgumentException _ (println err-msg)))] 
    (spit out-path out-text)))

(ns components-test
  (:require [cljs.test :refer [deftest is testing]]
            [simulator.components :as comp]))

;; pure functions
(deftest rotate-test
  (testing "turn left"
    (is (= 1 (comp/rotate 0 1)))
    (is (= 2 (comp/rotate 1 1)))
    (is (= 3 (comp/rotate 2 1)))
    (is (= 0 (comp/rotate 3 1))))

  (testing "turn right"
    (is (= 3 (comp/rotate 0 -1)))
    (is (= 2 (comp/rotate 3 -1)))
    (is (= 1 (comp/rotate 2 -1)))
    (is (= 0 (comp/rotate 1 -1)))))

(deftest move-test
  (testing "valid move"
    (is (= [1 0] (comp/move [0 0] [1 0])))
    (is (= [3 1] (comp/move [3 0] [0 1])))
    (is (= [3 2] (comp/move [3 3] [0 -1])))
    (is (= [1 0] (comp/move [2 0] [-1 0]))))

  (testing "move on boundary"
    (is (= [4 0] (comp/move [4 0] [1 0])))
    (is (= [0 0] (comp/move [0 0] [0 -1])))
    (is (= [0 2] (comp/move [0 2] [-1 0])))
    (is (= [3 4] (comp/move [3 4] [0 1])))))

(deftest index-to-position-test
  (testing "index on the board"
    (is (= [0 0] (comp/index-to-position [0 0])))))


;; TODO:
;; add tests for event listeners and check for the correct states

;; add server tests for the API and db states

;; add smoke-tests using cypress or something (testing the UI interactions agress with the program specs)
(ns jasentaa.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [jasentaa.parser.basic-test]))

(doo-tests 'jasentaa.parser.basic-test)

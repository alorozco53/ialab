(defrule push-value
  ?push-value <- (push-value ?name ?value)
  ?stack <- (stack ?name ?rest)
  =>
  (retract ?push-value ?stack)
  (assert (stack ?value $?rest))
  (printout t "Pushing value " ?value crlf))

(defrule pop-value-valid
  ?pop-value <- (pop-value ?name)
  ?stack <- (stack ?name ?value $?rest)
  =>
  (retract ?pop-value ?name ?stack)
  (assert (stack ?name ?rest))
  (printout t "Popping value " ?value crlf))
  
(defrule pop-value-invalid
  ?pop-value <- (pop-value ?name)
  (stack ?name)
  =>
  (retract ?pop-value ?name)
  (printout t "Popping from empty stack " ?name crlf))
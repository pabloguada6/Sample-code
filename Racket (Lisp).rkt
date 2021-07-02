#lang racket

(module+ test (require rackunit))

(define (get-initial-state) (cons 1 1))
(module+ test (check-equal? (get-initial-state) (cons 1 1)))

(define (get-end-state rows columns) (cons (- rows 2) (- columns 2)))
(module+ test (check-equal? (get-end-state 10 10) (cons 8 8)))

(define (initialize rows columns blocks doors)
  (let* ([rand1 (pick-random-recursive (generate-positions rows columns) blocks)]
         [rand2 (pick-random-recursive (car (reverse rand1)) doors)]
         [free-positions (car (reverse rand2))]
         [block-positions (cdr (reverse rand1))]
         [door-positions (cdr (reverse rand2))]
         [room (generate-room-recursive rows columns 0 0 block-positions door-positions)])
    (values free-positions block-positions door-positions room)))

(module+ test(test-begin
              (define-values (free-positions block-positions door-positions room) (initialize 5 6 8 3))
              (check-equal? (length free-positions) 17)
              (check-equal? (length block-positions) 8)
              (check-equal? (length door-positions) 3)
              (check-equal? (length room) 5)
              (check-equal? (length (car room)) 6)))

(define (generate-positions rows columns)
  (define all-positions (for*/list ([i rows][j columns]) (cons i j)))
  (let ([initial-state (get-initial-state)]
        [end-state (get-end-state rows columns)])
    (remove initial-state (remove end-state all-positions))))

(module+ test (test-begin
               (define positions (generate-positions 5 5))
               (check-equal? (length positions) 23)
               (check-false (member (get-initial-state) positions))
               (check-false (member (get-end-state 5 5) positions))))

(define (pick-random list n)
  (for/fold
   ([source list]
    [picked '()])
   ([i n])
    (let* ([r (random (length source))]
           [value (list-ref source r)])
      (values (remove value source) (cons value picked)))))

(module+ test (test-begin
               (define-values (remaining picked) (pick-random (list 1 2 3) 2))
               (check-equal? (length remaining) 1)
               (check-equal? (length picked) 2)
               (check-not-false (or (member 1 remaining) (member 1 picked)))
               (check-not-false (or (member 2 remaining) (member 2 picked)))
               (check-not-false (or (member 3 remaining) (member 3 picked)))))

(define (pick-random-recursive source n)
  (if (= n 0)
      (cons source empty)
      (let* ([r (random (length source))]
             [value (list-ref source r)])
        (cons value (pick-random-recursive (remove value source) (- n 1))))))

(module+ test (test-begin
               (define picked (pick-random-recursive (list 3 4 5 7) 3))
               (check-equal? (length picked) 4)
               (check-not-false (or (or (member 3 picked) (member 4 picked)) (member 5 picked)))
               (check-not-false (or (or (member 3 picked) (member 4 picked)) (member 7 picked)))
               (check-not-false (or (or (member 3 picked) (member 5 picked)) (member 7 picked)))
               (check-not-false (or (or (member 4 picked) (member 5 picked)) (member 7 picked)))))
               
               

(define (generate-room rows columns blocks doors)
  (define-values (free-positions block-positions) (pick-random (generate-positions rows columns) blocks))
  (define-values (final-free-positions door-positions) (pick-random free-positions doors))
  (reverse (for*/fold
            ([superlist '()]
             [sublist '()]
             #:result superlist)
            ([i rows][j columns])
             (define character
               (cond [(member (cons i j) block-positions) "#"]
                     [(member (cons i j) door-positions) ":"]
                     [(equal? (cons i j) (get-initial-state)) "x"]
                     [(equal? (cons i j) (get-end-state rows columns)) "X"]
                     [else "_"]))                  
             (if (= j (- columns 1))
                 (values (cons (reverse (cons character sublist)) superlist) empty)
                 (values superlist (cons character sublist))))))

(module+ test (test-begin
               (define room (generate-room 4 4 5 2))
               (check-equal? (length room) 4)
               (check-equal? (length (car room)) 4)
               (check-equal? (check-number-simbols "x" room) 1)
               (check-equal? (check-number-simbols "X" room) 1)
               (check-equal? (check-number-simbols "#" room) 5)
               (check-equal? (check-number-simbols ":" room) 2)))
               
(define (check-number-simbols simbol room)
  (let ([rows (length room)]
        [columns (length (car room))])
    (for*/fold ([counter 0]) ([i rows][j columns])
      (if (equal? (list-ref (list-ref room i) j) simbol)
          (+ counter 1)
          (+ counter 0)))))

(module+ test (check-equal? (check-number-simbols "#" '(("#" "_" "#" ":") (":" "_" "#" ":"))) 3))

(define (generate-room-recursive rows columns row column block-positions door-positions)
  (define character
    (cond [(member (cons row column) block-positions) "#"]
          [(member (cons row column) door-positions) ":"]
          [(equal? (cons row column) (get-initial-state)) "x"]
          [(equal? (cons row column) (get-end-state rows columns)) "X"]
          [else "_"]))      
  (if (= column (- columns 1))
      (cons character empty)
      (let([room (cons character (generate-room-recursive rows columns row (+ column 1) block-positions door-positions))])
        (cond [(and (= column 0) (< row (- rows 1))) (cons room (generate-room-recursive rows columns (+ row 1) 0 block-positions door-positions))]
              [(and (= column 0) (= row (- rows 1))) (cons room empty)]
              [else room]))))

(module+ test (test-begin
               (define room (generate-room-recursive 4 4 0 0 (cons (cons 2 3) (cons (cons 1 2) empty)) (cons (cons 2 1) (cons (cons 3 2) empty))))
               (check-equal? (length room) 4)
               (check-equal? (length (car room)) 4)
               (check-equal? (check-number-simbols "x" room) 1)
               (check-equal? (check-number-simbols "X" room) 1)
               (check-equal? (check-number-simbols "#" room) 2)
               (check-equal? (check-number-simbols ":" room) 2)))

(define (simple-path actual open-ways open-and-closed-nodes room)
  (define successors (successors-calculation (car actual) open-and-closed-nodes room))
  (if (equal? (square-value (car actual) room) "X")
      actual
      (let* ([new-open (reverse(for/fold ([list (reverse open-ways)]) ([i (length successors)])
                                 (cons (cons (list-ref successors i) actual) list)))])
        (if (equal? new-open empty)
            #f
            (simple-path (car new-open) (cdr new-open) (for/fold ([open open-and-closed-nodes]) ([i (length successors)]) (cons (list-ref successors i) open)) room)))))

(module+ test (test-begin
               (define simple-path-check (simple-path (cons (cons 1 1) empty) '() (cons (cons 1 1) empty) (generate-room-recursive 10 10 0 0 (cons (cons 2 3) (cons (cons 1 2) empty)) (cons (cons 2 1) (cons (cons 3 2) empty)))))
               (check-equal? (cons 1 1) (car (reverse simple-path-check)))
               (check-equal? (cons 8 8) (car simple-path-check))))

(define (successors-calculation square open-and-closed room)
  (let* ([row (car square)]
         [column (cdr square)]
         [up (cons (- row 1) column)]
         [down (cons (+ row 1) column)]
         [left (cons row (- column 1))]
         [right (cons row (+ column 1))]
         [successors (cons up (cons left (cons right (cons down empty))))])
    (filter (lambda(x) (and (and (>= (car x) 0) (>= (cdr x) 0)) (and (< (car x) (length room)) (< (cdr x) (length (car room)))) (not (equal? (square-value x room) "#")) (not-exists? x open-and-closed)))
            successors)))

(module+ test (test-begin
               (define successors1 (successors-calculation (cons 0 7) '() (generate-room-recursive 10 10 0 0 (cons (cons 2 3) (cons (cons 1 2) empty)) (cons (cons 2 1) (cons (cons 3 2) empty)))))
               (define successors2 (successors-calculation (cons 5 9) (cons (cons 3 7) (cons (cons 5 8) empty)) (generate-room-recursive 10 10 0 0 (cons (cons 2 3) (cons (cons 1 2) empty)) (cons (cons 2 1) (cons (cons 3 2) empty)))))
               (check-false (or (or (member (cons -1 7) successors1) (member (cons -1 6) successors1)) (member (cons -1 8) successors1)))
               (check-false (or (or (member (cons 5 8) successors2) (member (cons 5 10) successors2)) (or (member (cons 6 10) successors2) (member (cons 4 10) successors2))))))
               
(define (not-exists? square list)
  (for/and ([i (length list)]) (not (equal? square (list-ref list i)))))

(module+ test (test-begin
               (check-not-false (not-exists? (cons 3 7) '()))
               (check-false (not-exists? (cons 4 3) (cons (cons 4 3) (cons (cons 5 3) empty))))))                                           
  
(define (square-value square room)
  (let* ([row (car square)]
         [column (cdr square)])
    (list-ref (list-ref room row) column)))

(module+ test (check-equal? (square-value  (cons 1 2) '(("#" "_" "#" ":") (":" "_" "#" ":"))) "#")) 
               

(define (a*-path actual open-ways open-and-closed-nodes room)
  (define successors (successors-calculation (car actual) open-and-closed-nodes room))
  (if (equal? (square-value (car actual) room) "X")
      actual
      (let* ([successors-open (for/fold ([list (reverse open-ways)]) ([i (length successors)])
                                (cons (cons (list-ref successors i) actual) list))]
             [successors-open-heuristic (let* ([rows (length room)]
                                               [columns (length (car room))]
                                               [finish-square (cons (- rows 2) (- columns 2))])
                                          (for/list ([i (length successors-open)])
                                            (let* ([square-list (cons (caar (list-ref successors-open i)) (cdar (list-ref successors-open i)))]
                                                   [heuristic (+ (+ (abs (- (car finish-square) (car square-list)))                                                                     
                                                                    (abs (- (cdr finish-square) (cdr square-list))))
                                                                 (way-load-calculation (list-ref successors-open i) room))])
                                              (cons heuristic (list-ref successors-open i)))))]
             [new-open (open-list-sort successors-open-heuristic '() (length successors-open-heuristic) (length successors-open-heuristic))])
        (if (equal? new-open empty)
            #f
            (a*-path (car new-open) (cdr new-open) (for/fold ([open open-and-closed-nodes]) ([i (length successors)]) (cons (list-ref successors i) open)) room)))))

(module+ test (test-begin
               (define a*-path-check (a*-path (cons (cons 1 1) empty) '() (cons (cons 1 1) empty) (generate-room-recursive 10 10 0 0 (cons (cons 2 3) (cons (cons 1 2) empty)) (cons (cons 2 1) (cons (cons 3 2) empty)))))
               (check-equal? (cons 1 1) (car (reverse a*-path-check)))
               (check-equal? (cons 8 8) (car a*-path-check))))

(define (way-load-calculation way room)
  (for/fold ([load 0]) ([i (length way)])
    (let ([square (square-value (list-ref way i) room)])
      (if (equal? square ":")
          (+ load 4)
          (+ load 1)))))

(module+ test (check-equal? (way-load-calculation (cons (cons 0 1) (cons (cons 1 0) empty)) '(("#" "_" "#" ":") (":" "_" "#" ":"))) 5))

(define (open-list-sort open-list greater-heuristic original-list-length actual-elements)
  (if (= original-list-length (length open-list))
      (for/fold ([new-open '()]) ([i original-list-length])
        (let ([rest-of-open (delete-elements-onlist open-list new-open)])
          (if (= i (- original-list-length 1))
              (cons (cdar rest-of-open) new-open)
              (cons (open-list-sort (cdr rest-of-open) (car rest-of-open) original-list-length (- (length rest-of-open) 1)) new-open))))
      
      (cond [(and (= actual-elements 1) (> (caar open-list) (car greater-heuristic))) (cdar open-list)]
            [(and (= actual-elements 1) (<= (caar open-list) (car greater-heuristic))) (cdr greater-heuristic)]
            [(and (> (length open-list) 1) (> (caar open-list) (car greater-heuristic))) (open-list-sort (cdr open-list) (car open-list) original-list-length (- actual-elements 1))]
            [else (open-list-sort (cdr open-list) greater-heuristic original-list-length (- actual-elements 1))])))

(module+ test(test-begin
              (define list-sort (open-list-sort '((17 (4 3)) (13 (1 9)) (15 (5 6))) '() 3 3))
              (check-equal? (car list-sort) '((1 9)))
              (check-equal? (car (reverse list-sort)) '((4 3)))
              (check-equal? (cadr list-sort) '((5 6)))))
              
(define (delete-elements-onlist open-list new-open)
  (filter (lambda(x) (not-exists? (cdr x) new-open))
          open-list))

(module+ test (check-equal? (delete-elements-onlist (cons (cons 17 (cons 4 3)) (cons (cons 15 (cons 5 3)) empty)) (cons (cons 1 3) (cons (cons 5 3) empty)) )
                            (cons (cons 17 (cons 4 3)) empty)))


(define (room-actualization actualization room)
  (if (equal? actualization #f)
      (values #f #f)
      (for/fold ([cost 1] [new-room '()]) ([row (length room)])
        (let* ([new-row (for/fold ([list '()]) ([column (length (list-ref room row))])
                          (if (or (not-exists? (cons row column) actualization) (or (equal? (square-value (cons row column) room) "x") (equal? (square-value (cons row column) room) "X")))
                              (cons (square-value (cons row column) room) list)
                              (if (equal? (square-value (cons row column) room) ":")
                                  (cons "ç" list)
                                  (cons "o" list))))]
               [new-row-cost (for/fold ([row-cost 0]) ([element (length new-row)])
                               (cond [(equal? (list-ref new-row element) "ç") (+ 4 row-cost)]
                                     [(equal? (list-ref new-row element) "o") (+ 1 row-cost)]
                                     [else (+ 0 row-cost)]))])
          (values (+ new-row-cost cost) (cons (reverse new-row) new-room))))))

(module+ test (test-begin
               (define-values (cost1 room1) (room-actualization (cons (cons 1 3) (cons (cons 0 1) empty)) '(("#" "_" "#" ":") (":" "_" "#" ":"))))
               (define-values (cost2 room2) (room-actualization #f '(("#" "_" "#" ":") (":" "_" "#" ":"))))
               (check-equal? (list-ref (list-ref room1 0) 3) "ç")
               (check-equal? (list-ref (list-ref room1 1) 1) "o")
               (check-equal? (list-ref (list-ref room1 1) 0) "#")
               (check-equal? cost1 6)
               (check-equal? room2 #f)
               (check-equal? cost2 #f)))
                           
(define (run)
  (define-values (free-positions block-positions door-positions room) (initialize 10 10 20 10))
  (define-values (cost-simple new-room-simple) (room-actualization (simple-path (cons (cons 1 1) empty) '() (cons (cons 1 1) empty) room) room))
  (define-values (cost-a* new-room-a*) (room-actualization (a*-path (cons (cons 1 1) empty) '() (cons (cons 1 1) empty) room) room))
  (if (equal? new-room-simple #f)
      (for ([i (+ (length room) 1)]) (if (= i (length room))
                                         (display "No path and no cost available")
                                         (displayln (list-ref room i))))
      (print-solution room cost-simple new-room-simple cost-a* new-room-a*)))
  
(define (print-solution room cost-simple new-room-simple  cost-a* new-room-a*)
  (displayln "ROOM")
  (for ([i (length room)]) (displayln (list-ref room i)))
  (newline)
  (displayln "SIMPLE PATH")
  (display "Cost: ")
  (displayln cost-simple)
  (for ([i (length new-room-simple)]) (displayln (list-ref new-room-simple (- (length new-room-simple) (+ i 1)))))
  (newline)
  (displayln "A* PATH")
  (display "Cost: ")
  (displayln cost-a*)
  (for ([i (length new-room-a*)]) (displayln (list-ref new-room-a* (- (length new-room-a*) (+ i 1))))))
  
(run)

  
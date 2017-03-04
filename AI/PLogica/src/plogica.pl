%% Práctica 6
%% Inteligencia Artificial
%% Author: AlOrozco53

%% Ejercicio 1
word(astante, a,s,t,a,n,t,e).
word(astoria, a,s,t,o,r,i,a).
word(baratto, b,a,r,a,t,t,o).
word(cobalto, c,o,b,a,l,t,o).
word(pistola, p,i,s,t,o,l,a).
word(statale, s,t,a,t,a,l,e).

crossword(V1, V2, V3, H1, H2, H3) :-
    %% Each character will repeat in the position where
    %% there is an intersection of words in the crossword
    word(V1, _, I1, _, I4, _, I7, _),
    word(V2, _, I2, _, I5, _, I8, _),
    word(V3, _, I3, _, I6, _, I9, _),
    word(H1, _, I1, _, I2, _, I3, _),
    word(H2, _, I4, _, I5, _, I6, _),
    word(H3, _, I7, _, I8, _, I9, _).

%% Ejercicio 2
lower_bound(_, []).
lower_bound(M, [H | T]) :-
    M =< H, lower_bound(M, T).

list_size([], 0).
list_size([_ | T], N) :-
    list_size(T, M),
    N is M + 1.

elem(H, [H | _]).
elem(E, [_ | T]) :-
    elem(E, T).

concatenate([], L, L).
concatenate([H | T], L, [H | R]) :-
    concatenate(T, L, R).

subset([], _).
subset([H | T], L) :-
    elem(H, L),
    subset(T, L).

equiv_list(L1, L2) :-
    subset(L1, L2),
    subset(L2, L1).

delete_all([], _, []).
delete_all([H | T], M, L) :-
    equiv_list(H, M),
    delete_all(T, H, L).
delete_all([H | T], M, [H | RS]) :-
    delete_all(T, M, RS).

count(_, [], 0).
count(H, [H | T], N) :-
    count(H, T, M),
    N is M + 1.
count(E, [H | T], N) :-
    E \= H,
    count(E, T, N).

is_sublist_aux(_, [], _).
is_sublist_aux(L1, [H | T], L2) :-
    is_sublist_aux(L1, T, L2),
    count(H, L1, N1),
    count(H, L2, N2),
    N1 =< N2.

is_sublist(L1, L2) :-
    is_sublist_aux(L1, L1, L2).

sum_elems([], 0).
sum_elems([H | T], N) :-
    sum_elems(T, M),
    N is H + M.

listize([], []).
listize([H | T], [[H] | R]) :-
    listize(T, R).

new_sublist([], NewElem, [NewElem], CoinList) :-
    is_sublist([NewElem], CoinList).
new_sublist([H | T], NewElem, [NewElem | [H | T]], CoinList) :-
    is_sublist([NewElem | [H | T]], CoinList).

iteration(_, [], [], _, _, false).
iteration(PowElem, _, [CoinH | _], CoinList, Target, true) :-
    new_sublist(PowElem, CoinH, NewSubList, CoinList),
    sum_elems(NewSubList, N),
    N is Target.
iteration(PowElem, [NewSubList | PowAcc], [CoinH | CoinT], CoinList, Target, Res) :-
    new_sublist(PowElem, CoinH, NewSubList, CoinList),
    sum_elems(NewSubList, N),
    N < Target,
    iteration(PowElem, PowAcc, CoinT, CoinList, Target, Res).
iteration(PowElem, PowAcc, [CoinH | CoinT], CoinList, Target, Res) :-
    new_sublist(PowElem, CoinH, NewSubList, CoinList),
    sum_elems(NewSubList, N),
    N > Target,
    iteration(PowElem, PowAcc, CoinT, CoinList, Target, Res).
iteration(PowElem, PowAcc, [CoinH | CoinT], CoinList, Target, Res) :-
    not(new_sublist(PowElem, CoinH, _, CoinList)),
    iteration(PowElem, PowAcc, CoinT, CoinList, Target, Res).

generation([], [], _, _, false).
generation([], [], _, _, true).
generation([PowLH | _], _, [CoinH | CoinT], Target, true) :-
    iteration(PowLH, _, [CoinH | CoinT], [CoinH | CoinT], Target, true).
generation([PowLH | PowLT], NewPowAcc, [CoinH | CoinT], Target, Res) :-
    iteration(PowLH, RemLists, [CoinH | CoinT], [CoinH | CoinT], Target, true),
    Res.
generation([PowLH | PowLT], NewPowAcc, [CoinH | CoinT], Target, Res) :-
    iteration(PowLH, RemLists, [CoinH | CoinT], [CoinH | CoinT], Target, false),
    concatenate(RemLists, PowAcc, NewPowAcc),
    generation(PowLT, PowAcc, [CoinH | CoinT], Target, Res).

run_algorithm(_, _, _, NumberOfIterations, false) :-
    NumberOfIterations < 0.
run_algorithm(CoinList, InitialPowList, ExactAmount, NumberOfIterations, Res) :-
    NumberOfIterations >= 0,
    generation(InitialPowList, PowAcc, CoinList, ExactAmount, Res),
    NextIter is NumberOfIterations - 1,
    run_algorithm(CoinList, PowAcc, ExactAmount, NextIter, Res).
run_algorithm(_, _, _, _, true).

verdict(CoinList, 0, true).
verdict(CoinList, ExactAmount, Res) :-
    listize(CoinList, InitialPowList),
    list_size(CoinList, NumberOfIterations),
    run_algorithm(CoinList, InitialPowList, ExactAmount, NumberOfIterations, Res).

yes(true).

cantex(CoinList, ExactAmount) :-
    yes(Res),
    verdict(CoinList, ExactAmount, Res).

%% Ejercicio 3
colorea(Reg1, Reg2, Reg3, Reg4, Reg5, Reg6) :-
\+ Reg1 is Reg2,
\+ Reg1 is Reg3,
\+ Reg1 is Reg4,
\+ Reg3 is Reg2,
\+ Reg5 is Reg3,
\+ Reg5 is Reg4,
\+ Reg4 is Reg6,
\+ Reg1 is Reg5.

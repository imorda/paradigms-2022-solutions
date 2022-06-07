mark_composites(N, M, MAX_N) :-
		NM is N * M, NM =< MAX_N,
		assertz(composite(NM, N)),
		M1 is M + 1, mark_composites(N, M1, MAX_N).

walk_primes(N, MAX_N) :-
		prime(N), 
		mark_composites(N, N, MAX_N).
walk_primes(N, MAX_N) :-
		N1 is N + 1, N1 * N1 =< MAX_N,
		walk_primes(N1, MAX_N).

init(MAX_N) :- \+ walk_primes(2, MAX_N).

composite(N) :- composite(N, _).
prime(N) :- N > 1, \+ composite(N).

ascending([_]).
ascending([H1, H2 | T]) :- H1 =< H2.

prime_divisors(1, []) :- !.
prime_divisors(N, Divisors) :-
		number(N), !,
		(composite(N, X); X = N), !,
		NX is N / X,
		prime_divisors(NX, TDivs),
		Divisors = [X | TDivs].
prime_divisors(N, [HDivs | TDivs]) :-
		prime(HDivs), ascending([HDivs | TDivs]),
		prime_divisors(X, TDivs), N is HDivs * X.

to_base_rev(0, _, []) :- !.
to_base_rev(N, K, R) :-
		NDiv is div(N, K), NMod is mod(N, K),
		to_base_rev(NDiv, K, Tail),
		R = [NMod | Tail].
		
prime_palindrome(N, K) :-
		prime(N),
		to_base_rev(N, K, NK),
		reverse(NK, NKRev), NK = NKRev.

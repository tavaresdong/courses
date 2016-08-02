CFLAGS = -Wall -std=c11 -g

trie.o: trie.h lib.h trie.c
	gcc $(CFLAGS) -c trie.c -o trie.o
 
lib.o: lib.c lib.h
	gcc $(CFLAGS) -c lib.c -o lib.o

test: test.c lib.o trie.o
	gcc $(CFLAGS) test.c lib.o trie.o -o test

t9: t9.c lib.o trie.o lib.h trie.h
	gcc $(CFLAGS) t9.c lib.o trie.o -o t9

clean:
	rm *.o test -rf

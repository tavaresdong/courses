CFLAGS = -Wall -std=c11

trie.o: trie.h lib.h trie.c
	gcc $(CFLAGS) -c trie.c -o trie.o
 
lib.o: lib.c lib.h
	gcc $(CFLAGS) -c lib.c -o lib.o

test: test.c lib.o trie.o
	gcc $(CFLAGS) test.c lib.o trie.o -o test

clean:
	rm *.o test -rf

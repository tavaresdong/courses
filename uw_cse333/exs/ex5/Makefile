CFLAGS = -Wall -std=c11
ex5: vector.o ex5.o
	gcc $(CFLAGS) -o ex5 vector.o ex5.o

vector.o: vector.c vector.h
	gcc  $(CFLAGS) -c vector.c vector.h

ex5.o: ex5.c vector.h
	gcc $(CFLAGS) -c ex5.c vector.h

clean:
	rm *.o ex5

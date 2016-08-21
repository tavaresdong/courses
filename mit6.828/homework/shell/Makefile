CFLAGS=--std=c++11 -Wall -g

easysh: easysh.cc parse.o command.o
	g++ -o easysh $(CFLAGS) easysh.cc parse.o command.o

parse.o: parse.cc
	g++ -c $(CFLAGS) parse.cc

command.o: command.h command.cc
	g++ -c $(CFLAGS) command.cc command.h



.PHONY: clean

clean:
	rm easysh *.o

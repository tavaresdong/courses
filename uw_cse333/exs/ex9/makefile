ex9: ex9.o IntPair.o
	g++ -o ex9 $^

ex9.o: ex9.cc IntPair.h
	g++ -c $<

IntPair.o: IntPair.cc IntPair.h
	g++ -c $<

clean:
	-rm *.o ex9


# define the commands we will use for compilation and library building
AR = ar
ARFLAGS = rcs
CC = g++

# define useful flags to cc/ld/etc.
#CFLAGS += -std=c11 -g -Wall -Wpedantic -I. -I../jansson/include -O0
CXXFLAGS += --std=c++11 -O3 -Wall -Wpedantic -I. -I../jansson/include -O0
LDFLAGS += -L. -L../jansson/lib -lhw2 -l:libjansson.a

#replace these definitions with lists of your source and header files
TESTSRC=hw2.cc
LIBOBJS=array2d.o
HEADERS=array2d.h

all: hw2 libhw2.a 

libhw2.a: $(LIBOBJS) $(HEADERS)
	$(AR) $(ARFLAGS) libhw2.a $(LIBOBJS)

hw2:  libhw2.a $(TESTSRC) $(HEADERS)
	$(CC) $(CXXFLAGS) $(TESTSRC)  -o hw2 $(LDFLAGS)

run: hw2
	valgrind --leak-check=full ./hw2 test.json

%.o: %.c $(HEADERS)
	$(CC) $(CXXFLAGS) -c $<

clean:
	/bin/rm -f *.o *~ hw2 libhw2.a json.out



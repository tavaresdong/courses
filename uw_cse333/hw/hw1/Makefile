# define the commands we will use for compilation and library building
AR = ar
ARFLAGS = rcs
CC = gcc
CXX = g++

# define useful flags to cc/ld/etc.
CFLAGS += -g -Wall -Wpedantic -I. -I.. -O0
LDFLAGS += -L. -lhw1
CPPUNITFLAGS =

# define common dependencies
OBJS = LinkedList.o Verify333.o
HEADERS = LinkedList.h  CSE333.h
TESTOBJS = test_linkedlist.o test_suite.o ../gtest/src/gtest-all.o
TESTHEADERS = test_linkedlist.h

# compile everything; this is the default rule that fires if a user
# just types "make" in the same directory as this Makefile
all: test_suite example_program_ll FORCE

example_program_ll: example_program_ll.o libhw1.a $(HEADERS) FORCE
	$(CC) $(CFLAGS) -o $@ example_program_ll.o $(LDFLAGS)

libhw1.a: $(OBJS) $(HEADERS) FORCE
	$(AR) $(ARFLAGS) $@ $(OBJS)

test_suite: $(TESTOBJS) $(TESTHEADERS) libhw1.a FORCE
	$(CXX) $(CFLAGS) -o $@ $(TESTOBJS) \
	$(CPPUNITFLAGS) $(LDFLAGS) -lpthread $(LDFLAGS)

%.o: %.cc $(HEADERS) FORCE
	$(CXX) $(CFLAGS) -std=c++11 -c $< -o $@

%.o: %.c $(HEADERS) FORCE
	$(CC) $(CFLAGS) -c -std=c11 $< -o $@

clean: FORCE
	/bin/rm -f *.o ../gtest/src/*.o *~ *.gcno *.gcda *.gcov test_suite libhw1.a \
    example_program_ll

FORCE:

#include "GetPrime.h"
#include <stdio.h>
#include <inttypes.h>
#include <stdlib.h>

int main(int argc, char **argv) {

	for(int ind = 1; ind <= 10; ++ind) 
		printf("%llu ", GetPrime(ind));

	return EXIT_SUCCESS;
}

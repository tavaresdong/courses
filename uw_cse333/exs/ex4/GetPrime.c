#include "GetPrime.h"

int isPrime(uint64_t val);

uint64_t GetPrime(uint16_t n) {

	if(n == 0) return 0;

	uint64_t start = 1;
	int count = 0;
	while(count != n) {
		start += 1;
		if(start % 2 == 0 && start != 2) {
			continue;
		}

		if(isPrime(start)) {
			count ++;
		}
	}

	return start;
}


int isPrime(uint64_t val) {

	uint64_t start = 2;
	int isPrime = 1;
	while(start < val / 2) {
		if(val % start == 0) {
			isPrime = 0;
			break;
		}
		start ++;
	}
	return isPrime;
}


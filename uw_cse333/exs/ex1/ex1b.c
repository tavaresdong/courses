#include <stdint.h>	// for uint64_t
#include <stdio.h>	// for printf
#include <inttypes.h>	// for exntended integer print formats
#include <stdlib.h>	// for EXIT_SUCCESS

#define SIZE 11

void copyAndSort(uint64_t *source, uint64_t* dest, int length) {

	if(length == 0) return;
	dest[0] = source[0];
	for(uint64_t i = 1; i < length; i++) {
		uint64_t j = i;
		while(j > 0 && dest[j - 1] > source[i]) {	
			dest[j] = dest[j - 1];
			j--;
		}
		dest[j] = source[i];
	}
}


void printArray(uint64_t *arr, int len) {

	if(len == 0) return;
	for(uint64_t i = 0; i < len; ++i) {
		printf("%" PRIu64 "%c", arr[i], i == (len - 1) ? '\n' : ' ');
	}
}

int main(int argc, char **argv) {

	uint64_t unsorted[SIZE] = {3, 2, 5, 7, 10, 4, 1, 7, 9, 8, 6};
	uint64_t sorted[SIZE];

	copyAndSort(unsorted, sorted, SIZE);

	printArray(sorted, SIZE);

	return EXIT_SUCCESS;
}

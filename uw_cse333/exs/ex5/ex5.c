#include "vector.h"

#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>


static void PrintIntVector(vector_t v);


#define INIT_VEC_SIZE 4
#define FINAL_VEC_SIZE 10

int main(int argc, char *argv[]) {

	size_t i;
	vector_t v = VectorCreate(INIT_VEC_SIZE);
	if(v == NULL)
		return EXIT_FAILURE;

	for(i = 0; i < FINAL_VEC_SIZE; ++i) {
		int *x = (int*) malloc(sizeof(int));
		if(x == NULL)
			return EXIT_FAILURE;
		*x = i;
		element_t old;
		bool ok = VectorSet(v, i ,x, &old);
		if(!ok)
			return EXIT_FAILURE;
	}

	PrintIntVector(v);
	printf("\n");

	for(i = 0; i < VectorLength(v); ++i)
		free(VectorGet(v, i));
	VectorFree(v);
	return EXIT_SUCCESS;
}


static void PrintIntVector(vector_t v) {

	size_t i;
	size_t length;

	assert(v != NULL);
	
	length = VectorLength(v);
	printf("[");
	if(length > 0) {
		printf("%d", *((int*)VectorGet(v, 0)));
		for(i = 1; i < VectorLength(v); ++i)
			printf(",%d", *((int*)VectorGet(v, i)));
		
	}
	printf("]");
}

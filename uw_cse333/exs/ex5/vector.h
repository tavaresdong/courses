#ifndef VECTOR_H
#define VECTOR_H
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>

typedef void* element_t;

typedef struct vector_t {
	size_t length;
	element_t *array;
} *vector_t;


vector_t VectorCreate(size_t n);

void VectorFree(vector_t v);

bool VectorSet(vector_t v, uint32_t index, element_t e, element_t *prev);

element_t VectorGet(vector_t v, size_t index);

size_t VectorLength(vector_t v);




#endif

#include "vector.h"
#include <assert.h>
#include <stdlib.h>


static element_t *ResizeArray(element_t *array, size_t oldLen, size_t newLen) {

	element_t* newArray = malloc(newLen * sizeof(element_t));
	if(newArray == NULL) return NULL;

	for(size_t i = 0; i < oldLen; i++)
		newArray[i] = array[i];
	for(size_t j = oldLen; j < newLen; j++)
		newArray[j] = NULL;

	return newArray;
}

vector_t VectorCreate(size_t n) {
	if(n == 0) return NULL;

	vector_t vec = malloc(sizeof(struct vector_t));
	if(vec == NULL) return NULL;
	vec->array = malloc(sizeof(element_t) * n);
	if(vec->array == NULL) {
		free(vec);
		return NULL;
	}
	vec->length = n;
	for(size_t i = 0; i < n; i++) {
		vec->array[i] = NULL;
	}

	return vec;
}

void VectorFree(vector_t v) {
	assert(v != NULL);
	free(v->array);
	free(v);
}

bool VectorSet(vector_t v, uint32_t index, element_t e, element_t *prev) {
	if(v == NULL) return false;
	if(index >= v->length) {
		size_t newLength = index + 1;
		element_t *newArray = ResizeArray(v->array, v->length, newLength);
		if(newArray == NULL)
			return false;
		free(v->array);
		v->array = newArray;
		v->length = newLength;
		prev = NULL;
		v->array[index] = e;
		
	} 
	else {
		*prev = v->array[index];
		v->array[index] = e;
	}
	return true;
}

element_t VectorGet(vector_t v, size_t index) {
	if(v == NULL || index >= v->length) return NULL;

	return v->array[index];
}


size_t VectorLength(vector_t v) {
	if(v == NULL) return -1;
	return v->length;
}

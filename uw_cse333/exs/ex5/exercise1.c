#include <stdio.h>
#include <stdlib.h>
#include <err.h>
#include <inttypes.h>
#include <assert.h>
#include <errno.h>

char* GetNextLine(FILE *f);

int *ReadIntArray(FILE* f, int *arrlen);

int IntComparator(const void *el1, const void *el2);

void usage(void) {
	fprintf(stderr, "usage: ./ex1 filename\n");
	
	// in stdlib.h
	exit(EXIT_FAILURE);
}




int main(int argc, char **argv) {
	FILE* f;
	int *array, arraylen;

	if(argc != 2)
		usage();

	f = fopen(argv[1], "r");
	if(f == NULL) {
		fprintf(stderr, "Couldn't open '%s:", argv[1]);

		// stdio.h
		perror(NULL);
		usage();
	}
	
	array = ReadIntArray(f, &arraylen);
	if(array == NULL) {
		fprintf(stderr, "File '%s' doesn't contain integers\n", argv[1]);
		exit(EXIT_FAILURE);
	}

	qsort(array, arraylen, sizeof(int), &IntComparator);

	{
		int i;
		for(i = 0; i < arraylen; i++) {
			printf("%d\n", array[i]);
		}

	}

	fclose(f);
	free(array);
	return EXIT_SUCCESS;
}



int *ReadIntArray(FILE* f, int *arrlen) {

	int *array = NULL, len = 0;
	while(1) {
		char *nextline;
		int nextint, res;

		nextline = GetNextLine(f);
		if(nextline == NULL) {
			break;
		}
		
		res = sscanf(nextline, "%d", &nextint);
		if(res == 0) {
			fprintf(stderr, "file doesn't contain integers; quitting.\n");
			exit(EXIT_FAILURE);
		}

		array = (int *) realloc(array, (len + 1) * sizeof(int));
		array[len] = nextint;
		len++;
		free(nextline);
	}

	*arrlen = len;
	return array;
}


char *GetNextLine(FILE *f) {
	char *linestr = NULL;
	int count = 0;

	linestr = (char*) malloc(1 * sizeof(char));
	if(linestr == NULL)
		return NULL;
	linestr[0] = '\0';

	while(1) {
		linestr = realloc(linestr, count + 2);
		if(linestr == NULL) {
			free(linestr);
			return NULL;
		}

		if(fgets(&(linestr[count]), 2, f) == NULL) {
			if(count == 0) {
				free(linestr);
				return NULL;
			}
		}
		if(linestr[count] == '\n') {
			linestr[count] = '\0';
			return linestr;
		}
		count++;
	}
}


int IntComparator(const void *el1, const void *el2) {
	int *int1ptr, *int2ptr;
	
	int1ptr = (int *) el1;
	int2ptr = (int *) el2;

	if(*int1ptr > *int2ptr)
		return 1;
	if(*int1ptr < *int2ptr)
		return -1;
	return 0;

}

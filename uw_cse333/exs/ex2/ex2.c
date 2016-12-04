#include <stdint.h>
#include <stdio.h>
#include <inttypes.h>
#include <stdlib.h>
#include <string.h>

void DumpHex(void *pData, int byteLen) {
	if(byteLen <= 0) return;
	uint8_t *head = (uint8_t*) pData;

	printf("The %d bytes starting at 0x%"PRIxPTR " are:", byteLen, (uintptr_t)pData);
	for(int ind = 0; ind < byteLen; ++ind) {

		printf(" %02" PRIx8, head[ind]);
	}
	printf("\n");

}


int main(int argc, char **argv) {
	char charVal = '0';
	int32_t intVal = 1;
	float floatVal = 1.0;
	double doubleVal = 1.0;
	
	typedef struct {
		char charVal;
		int32_t intVal;
		float floatVal;
		double doubleVal;
	} Ex2Struct;
	Ex2Struct tmp = {'0', 1, 1.0, 1.0};
	unsigned char buf[sizeof(tmp)];
	memset(buf, 0, sizeof(buf));
	size_t offset = 0;
	memcpy(buf + offset, &tmp.charVal, sizeof(tmp.charVal));
	offset += sizeof(tmp.charVal);
	memcpy(buf + offset, &tmp.intVal, sizeof(tmp.intVal));
	offset += sizeof(tmp.intVal);
	memcpy(buf + offset, &tmp.floatVal, sizeof(tmp.floatVal));
	offset += sizeof(tmp.floatVal);
	memcpy(buf + offset, &tmp.doubleVal, sizeof(tmp.doubleVal));
	

	DumpHex(&charVal, sizeof(char));
	DumpHex(&intVal, sizeof(int32_t));
	DumpHex(&floatVal, sizeof(float));
	DumpHex(&doubleVal, sizeof(double));
	DumpHex(&buf, sizeof(tmp));

	return EXIT_SUCCESS;

}

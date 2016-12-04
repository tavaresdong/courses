#include <string.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>


typedef struct {
	int16_t x;
	int16_t y;
	int16_t z;
	
} Point3d;

Point3d* AllocatePoint3d(int16_t x, int16_t y, int16_t z) {
	Point3d* buf = (Point3d*)malloc(sizeof(Point3d));
	if(buf == NULL) return NULL;
	
	memset(buf, 0, sizeof(Point3d));
	buf->x = x;
	buf->y = y;
	buf->z = z;

	return buf;
}

int main(int argc, char **argv) {

	Point3d *pt  = AllocatePoint3d(1,2,3);
	if(pt == NULL) {
		printf("cannot allocate memory\n");
		return EXIT_FAILURE;
	}
	else
		free(pt);

	return EXIT_SUCCESS;
}

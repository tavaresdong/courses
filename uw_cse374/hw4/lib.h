#ifndef _LIB_H_
#define _LIB_H_
#include <stdlib.h>

extern void* guarded_malloc(size_t len);

extern void guarded_free(void* ptr);

#endif

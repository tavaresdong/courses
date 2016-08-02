#include "lib.h"
#include <stdio.h>

void* guarded_malloc(size_t sz)
{
    void* ret = malloc(sz);
    if (ret == NULL)
    {
        fprintf(stderr, "Failed allocating memory\n");
        exit(EXIT_FAILURE);
    }
    return ret;
}

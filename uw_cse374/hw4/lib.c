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

void guarded_free(void* ptr)
{
    if (ptr == NULL)
    {
        fprintf(stderr, "The memory to be freed is NULL\n");
        exit(EXIT_FAILURE);
    }
    free(ptr);
}

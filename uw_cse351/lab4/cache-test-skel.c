/*
YOUR NAME HERE
CSE 351 - Autumn 2015
Lab 4 - Mystery Caches

Mystery Cache Geometries:
mystery0:
    block size =
    cache size =
    associativity =
mystery1:
    block size =
    cache size =
    associativity =
mystery2:
    block size =
    cache size =
    associativity =
mystery3:
    block size =
    cache size =
    associativity =
*/

#include <stdlib.h>
#include <stdio.h>

#include "mystery-cache.h"

/*
 * NOTE: When using access_cache() you do not need to provide a "real" memory
 * addresses. You can use any convenient integer value as a memory address.
 * You should not be able to cause a segmentation fault by providing a memory
 * address out of your programs address space as the argument to access_cache.
 */

/*
   Returns the size (in B) of each block in the cache.
*/
int get_block_size(void) {
  /* YOUR CODE GOES HERE */
  int addr = 0;
  int block_size = 0;
  access_cache(addr);
  while (TRUE)
  {
    block_size ++;
    addr++;
    if (!access_cache(addr))
      break;
  }
  return block_size;
}

/*
   Returns the size (in B) of the cache.
*/
int get_cache_size(int block_size) {
  /* YOUR CODE GOES HERE */
  int sz = 0;
  flush_cache();
  access_cache(0);
  while (access_cache(0))
  {
    sz += block_size;
    int i = block_size;
    while (i <= sz)
    {
      access_cache(i);
      i += block_size;
    }
    
  }
  return sz;
}

/*
   Returns the associativity of the cache.
*/
int get_cache_assoc(int cache_size) {
  /* YOUR CODE GOES HERE */
  flush_cache();
  access_cache(0);
  int assoc = 0;
  while (access_cache(0))
  {
    assoc++;
    int addr = 0;
    int i = 1;
    while (i <= assoc)
    {
      addr += cache_size;
      access_cache(addr);     
      i += 1;
    }
  }
  return assoc;
}

int main(void) {
  int size;
  int assoc;
  int block_size;

  cache_init(0,0);

  block_size=get_block_size();
  printf("Cache block size: %d bytes\n", block_size);
  size=get_cache_size(block_size);
  printf("Cache size: %d bytes\n", size);
  assoc=get_cache_assoc(size);
  printf("Cache associativity: %d\n", assoc);


  return EXIT_SUCCESS;
}

/*
 * Copyright 2011 Steven Gribble
 *
 *  This file is part of the UW CSE 333 course project sequence
 *  (333proj).
 *
 *  333proj is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  333proj is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with 333proj.  If not, see <http://www.gnu.org/licenses/>.
 */

// Feature test macro for strtok_r (c.f., Linux Programming Interface p. 63)
#define _XOPEN_SOURCE 600

#include "libhw1/CSE333.h"
#include "memindex.h"
#include "filecrawler.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>


static void Usage(void);

int main(int argc, char **argv) {
  if (argc != 2)
    Usage();

  // Implement searchshell!  We're giving you very few hints
  // on how to do it, so you'll need to figure out an appropriate
  // decomposition into functions as well as implementing the
  // functions.  There are several major tasks you need to build:
  //
  //  - crawl from a directory provided by argv[1] to produce and index
  //  - prompt the user for a query and read the query from stdin, in a loop
  //  - split a query into words (check out strtok_r)
  //  - process a query against the index and print out the results
  //
  // When searchshell detects end-of-file on stdin (cntrl-D from the
  // keyboard), searchshell should free all dynamically allocated
  // memory and any other allocated resources and then exit.

  int res;
  ssize_t read;
  size_t len = 0;
  char *line = NULL, *word = NULL, *savedptr;
  char *words[32];
  int wordlen;
  DocTable dt;
  MemIndex idx;
  LinkedList llres;
  LLIter it;
  LLPayload_t payload;
  SearchResultPtr result;

  res = CrawlFileTree(argv[1], &dt, &idx);
  if (res != 1) {
    fprintf(stderr, "Failed to crawl the file tree from %s\n", argv[1]);
    return EXIT_FAILURE;
  }

  while (true) {
    // Only if we set line to NULL and len to 0
    // will the library allocate space for the returned
    // string, which should be freed by the client
    line = NULL; len = 0;
    read = getline(&line, &len, stdin);
    if (read == -1) {
      if (line != NULL) free(line);
      break;
    }

    // Note: strtok return the pointer pointing
    // to the position in the original string
    // this pointer is not malloc()'ed, so should
    // not be free()'d by user.
    word = strtok_r(line, " \n", &savedptr);
    wordlen = 0;
    while (word != NULL) {
      words[wordlen++] = word;
      word = strtok_r(NULL, " \n", &savedptr);
    }

    llres = MIProcessQuery(idx, words, wordlen);

    if (llres != NULL) {
      it = LLMakeIterator(llres, 0);
      do {
        LLIteratorGetPayload(it, &payload);
        result = (SearchResultPtr) payload;
        fprintf(stdout, "Document: %s, rank: %d\n",
                        DTLookupDocID(dt, result->docid),
                        result->rank);
      } while (LLIteratorNext(it));
      LLIteratorFree(it);

      FreeLinkedList(llres, (LLPayloadFreeFnPtr) free);
    }

    free(line);
  }

  FreeDocTable(dt);
  FreeMemIndex(idx);
  return EXIT_SUCCESS;
}

static void Usage(void) {
  fprintf(stderr, "Usage: ./searchshell <docroot>\n");
  fprintf(stderr,
          "where <docroot> is an absolute or relative " \
          "path to a directory to build an index under.\n");
  exit(-1);
}


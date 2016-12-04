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

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>

#include "libhw1/CSE333.h"
#include "memindex.h"
#include "filecrawler.h"

static void Usage(void);

static void NotFound(void);

int main(int argc, char **argv) {
  int result;
  char *forquery = "please enter the query string:\n";
  char *query[1024];
  char *delim = " :-\n\t";
  char *word;
  char *docname;
  char querystring[1024];
  int count = 0;
  LinkedList list;
  LLIter iter;
  SearchResult *sr;

  MemIndex index;
  DocTable table;
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

  // (1) Crawl from a directory provided by arg[1]
  index = AllocateMemIndex();
  table = AllocateDocTable();
  printf("Getting indexes...\n");
  result = CrawlFileTree(argv[1], &table, &index);
  if(result == 0)
    return EXIT_FAILURE;
  
  printf("%s", forquery);
  memset(querystring, 0, sizeof(querystring));
  while(fgets(querystring, 1024, stdin) != NULL) {
    count = 0;
    word = strtok(querystring, delim);
    query[count++] = word;
    while((word = strtok(NULL, delim))) {
      query[count++] = word;
    }
    for(int i = 0; i < count; ++i)
      printf("%s ", query[i]);
    

    // Precess this query
    list = MIProcessQuery(index, query, count);
    if(list == NULL) {
      NotFound();
    } else {
      // ouput the results
      iter = LLMakeIterator(list, 0);
      LLIteratorGetPayload(iter, (LLPayload_t*) &sr);
      docname = DTLookupDocID(table, sr->docid);
      printf("%s", docname);
      printf("   rank: %d\n", sr->rank);

      while(LLIteratorHasNext(iter)) {
        LLIteratorNext(iter);
        LLIteratorGetPayload(iter, (LLPayload_t*) &sr);
        docname = DTLookupDocID(table, sr->docid);
        printf("%s", docname);
        printf("   rank: %d\n", sr->rank);
      } 

      printf("done\n");

      LLIteratorFree(iter);
       
    }   

    printf("%s", forquery);
    memset(querystring, 0, sizeof(querystring));
  }

  return EXIT_SUCCESS;
}

static void NotFound(void) {
  fprintf(stdout, "The query has no results found.\n");
}

static void Usage(void) {
  fprintf(stderr, "Usage: ./searchshell <docroot>\n");
  fprintf(stderr,
          "where <docroot> is an absolute or relative " \
          "path to a directory to build an index under.\n");
  exit(-1);
}


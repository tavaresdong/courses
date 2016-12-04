/*
 * Copyright 2012 Steven Gribble
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
#include <stdint.h>
#include <string.h>

#include "memindex.h"
#include "libhw1/CSE333.h"
#include "libhw1/HashTable.h"

// This helper function is passed to SortLinkedList();
// it is the comparator that compares the rank of two
// search results while sorting the result list.
static int MISearchListComparator(LLPayload_t e1, LLPayload_t e2) {
  SearchResult *sr1 = (SearchResult *) e1;
  SearchResult *sr2 = (SearchResult *) e2;

  if (sr1->rank > sr2->rank)
    return 1;
  if (sr1->rank < sr2->rank)
    return -1;
  return 0;
}

// Used by MIListFree() to free a linked list of positions.
static void MINullFree(LLPayload_t ptr) { }

// Frees a linked list of positions.
static void MIListFree(HTValue_t ptr) {
  LinkedList list = (LinkedList) ptr;
  FreeLinkedList(list, &MINullFree);
}


// Frees a WordDocSet structure.
static void MIFree(HTValue_t ptr) {
  WordDocSetPtr wd = (WordDocSetPtr)ptr;
  free(wd->word);
  FreeHashTable(wd->docIDs, &MIListFree);
  free(wd);
}

MemIndex AllocateMemIndex(void) {
  // Happily, HashTables dynamically resize themselves
  // now, so we can start by allocating a small hashtable.
  HashTable mi = AllocateHashTable(128);
  return mi;
}

void FreeMemIndex(MemIndex index) {
  FreeHashTable(index, &MIFree);
}

HWSize_t MINumWordsInMemIndex(MemIndex index) {
  return NumElementsInHashTable(index);
}

int MIAddPostingList(MemIndex index, char *word, DocID_t docid,
                     LinkedList positions) {
  HTKey_t wordkey = FNVHash64((unsigned char *) word, strlen(word));
  HTKeyValue kv, hitkv;
  WordDocSet *wds;
  HashTable docids;
  int res;

  // STEP 1.
  // Remove this "return 1;". We added this in here
  // so that your filecrawler unit tests would pass
  // even though you hadn't yet finished the
  // memindex.c implementation.


  // First, we have to see if the word we're being handed
  // already exists in the inverted index.
  res = LookupHashTable(index, wordkey, &kv);
  if (res == 0) {
    // STEP 2.
    // No, this is the first time the inverted index has
    // seen this word.  We need to malloc and prepare a
    // new WordDocSet structure.  After malloc'ing it,
    // we need to:
    //   (1) insert the word into the WordDocSet,
    //   (2) allocate a new hashtable for the docID->positions mapping,
    //   (3) insert that hashtable into the WordDocSet, and
    //   (4) insert the the new WordDocSet into the inverted
    //       index (i.e., into the "index" table).
    //printf("case1\n");
    wds = (WordDocSet *) malloc(sizeof(WordDocSet));
    if(wds == NULL) return 0;
    wds->word = word;
    docids = AllocateHashTable(1);
    wds->docIDs = docids;
    kv.key = wordkey;
    kv.value = wds;
    res = InsertHashTable(index, kv, &hitkv);
    if(res == 0)
      return 0;

    //printf("done1\n");
  } else {
    // Yes, this word already exists in the inverted index.
    // So, there's no need to insert it again; we can go
    // ahead and free the word.
    //printf("case2\n");
    free(word);

    // Instead of allocating a new WordDocSet, we'll
    // use the one that's already in the inverted index.
    wds = (WordDocSet *) kv.value;
    //printf("done2\n");
  }

  // Verify that the docID doesn't exist in the WordDocSet.
  res = LookupHashTable(wds->docIDs, docid, &hitkv);
  Verify333(res == 0);

  // STEP 3.
  // Insert a new entry into the wds->docIDs hash table.
  // The entry's key is this docID and the entry's value
  // is the "positions" word positions list we were passed
  // as an argument.

  //printf("Inserting a new entry\n");
  kv.key = docid;
  kv.value = positions; 
  res = InsertHashTable(wds->docIDs, kv, &hitkv);
  Verify333(res == 1);
  //printf("done inserting\n");

  return 1;
}

LinkedList MIProcessQuery(MemIndex index, char *query[], uint8_t qlen) {
  LinkedList retlist;
  HTKeyValue kv;
  WordDocSet *wds;
  int res;
  HTKey_t wordkey;
  HTIter iter;
  SearchResult *sr;
  // If the user provided us with an empty search query, return NULL
  // to indicate failure.
  if (qlen == 0)
    return NULL;

  // Allocate a linked list to store our search results.  A search
  // result is just a list of SearchResult structures.
  retlist = AllocateLinkedList();

  // STEP 4.
  // The most interesting part of Part C starts here...!
  //
  // Look up the first query word (query[0]) in the inverted
  // index.  For each document that matches, allocate a SearchResult
  // structure.  Initialize that SearchResult structure with the
  // docID, and the initial computed rank for the document.  (The
  // initial computed rank is the number of times the word appears
  // in that document.)
  //
  // Then, append the SearchResult structure onto retlist.
  //
  // If there are no matching documents, free retlist and return NULL.
  wordkey = FNVHash64((unsigned char*) query[0], strlen(query[0]));
  
  // Search in index
  res = LookupHashTable(index, wordkey, &kv);
  if(res == 0) {
    // No result, free the list and return NULL
    FreeLinkedList(retlist, (LLPayloadFreeFnPtr) free);
    return NULL;
  }

  wds = kv.value;
  iter = HashTableMakeIterator(wds->docIDs);
  res = 1;
  while(res == 1) {
    res = HTIteratorGet(iter, &kv);
    if(res == 0) break;
    
    // Allocate a SearchResult Structure
    sr = (SearchResult *) malloc(sizeof(SearchResult));
    Verify333(sr != NULL);
    sr->docid = (DocID_t) kv.key;
    sr->rank = NumElementsInLinkedList((LinkedList)kv.value);
    AppendLinkedList(retlist, (LLPayload_t) sr);
	
    res = HTIteratorNext(iter);
  }

  // Free the iterator
  HTIteratorFree(iter);

  // Great; we have our search results for the first query
  // word.  If there is only one query word, we're done!
  // Sort the result list and return it to the caller.
  if (qlen == 1) {
    SortLinkedList(retlist, 0, &MISearchListComparator);
    return retlist;
  }

  // OK, there are additional query words.  Handle them one
  // at a time.
  int i;
  for (i = 1; i < qlen; i++) {
    LLIter llit;
    int j, ne;

    // STEP 5.
    // Look up the next query word (query[i]) in the inverted index.
    // If there are no matches, it means the overall query
    // should return no documents, so free retlist and return NULL.
    wordkey = FNVHash64((unsigned char*) query[i], strlen(query[i]));
    res = LookupHashTable(index, wordkey, &kv);
    if(res == 0) {
      FreeLinkedList(retlist, (LLPayloadFreeFnPtr)free);
      return NULL;
    }
    wds = (WordDocSet *) kv.value;

    // STEP 6.
    // There are matches.  We're going to iterate through
    // the docIDs in our current search result list, testing each
    // to see whether it is also in the set of matches for
    // the query[i].
    //
    // If it is, we leave it in the search
    // result list and we update its rank by adding in the
    // number of matches for the current word.
    //
    // If it isn't, we delete that docID from the search result list.
    llit = LLMakeIterator(retlist, 0);
    ne = NumElementsInLinkedList(retlist);
    for (j = 0; j < ne; j++) {
      LLIteratorGetPayload(llit, (LLPayload_t) &sr);
      wordkey = sr->docid;	// The docid to look for
      res = LookupHashTable(wds->docIDs, wordkey, &kv);
      if(res == 0) {
	// Thid document does'nt contain this word
	// Delete this item
        LLIteratorDelete(llit, free);
      } else {
	// Calculate length
        sr->rank += NumElementsInLinkedList((LinkedList) kv.value);

	// Move the iterator forward
        LLIteratorNext(llit);
      }
    }
    LLIteratorFree(llit);
  }

  // We've finished processing all of the query words.
  // If there are no documents left in our query result list,
  // free retlist and return NULL.
  if (NumElementsInLinkedList(retlist) == 0) {
    FreeLinkedList(retlist, (LLPayloadFreeFnPtr)free);
    return NULL;
  }

  // Sort the result list by rank and return it to the caller.
  SortLinkedList(retlist, 0, &MISearchListComparator);
  return retlist;
}

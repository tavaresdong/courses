/*
 * Copyright 2012 Steven Gribble
 *
 *  This file is part of the UW CSE 333 project sequence (333proj12).
 *
 *  333proj12 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  333proj12 is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with 333proj12.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <stdio.h>
#include <stdlib.h>

#include "CSE333.h"
#include "LinkedList.h"
#include "LinkedList_priv.h"

LinkedList AllocateLinkedList(void) {
  // allocate the linked list record
  LinkedList ll = (LinkedList) malloc(sizeof(LinkedListHead));
  if (ll == NULL) {
    // out of memory
    return (LinkedList) NULL;
  }

  // Step 1.
  // initialize the newly allocated record structure
  ll->num_elements = 0;
  ll->head = ll->tail = NULL;

  // return our newly minted linked list
  return ll;
}

void FreeLinkedList(LinkedList list,
                    LLPayloadFreeFnPtr payload_free_function) {
  // defensive programming: check arguments for sanity.
  Verify333(list != NULL);
  Verify333(payload_free_function != NULL);

  // Step 2.
  // sweep through the list and free all of the nodes' payloads as
  // well as the nodes themselves
  while (list->head != NULL) {
	payload_free_function(list->head->payload);
	LinkedListNodePtr cur = list->head;
	list->head = list->head->next;
	free(cur);
  }

  // free the list record
  free(list);
}

HWSize_t NumElementsInLinkedList(LinkedList list) {
  // defensive programming: check argument for safety.
  Verify333(list != NULL);
  return list->num_elements;
}

bool PushLinkedList(LinkedList list, LLPayload_t payload) {
  // defensive programming: check argument for safety. The user-supplied
  // argument can be anything, of course, so we need to make sure it's
  // reasonable (e.g., not NULL).
  Verify333(list != NULL);

  // allocate space for the new node.
  LinkedListNodePtr ln =
    (LinkedListNodePtr) malloc(sizeof(LinkedListNode));
  if (ln == NULL) {
    // out of memory
    return false;
  }

  // set the payload
  ln->payload = payload;

  if (list->num_elements == 0) {
    // degenerate case; list is currently empty
    Verify333(list->head == NULL);  // debugging aid
    Verify333(list->tail == NULL);  // debugging aid
    ln->next = ln->prev = NULL;
    list->head = list->tail = ln;
    list->num_elements = 1U;
    return true;
  }

  // STEP 3.
  // typical case; list has >=1 elements
  else if(list->num_elements >= 1) {
	Verify333(list->head != NULL);
	Verify333(list->tail != NULL);
	ln->next = list->head;
	ln->prev = NULL;
	list->head->prev = ln;
	list->head = ln;
	list->num_elements += 1U;
	return true;
  }



  // return success
  return true;
}

bool PopLinkedList(LinkedList list, LLPayload_t *payload_ptr) {
  // defensive programming.
  Verify333(payload_ptr != NULL);
  Verify333(list != NULL);

  // Step 4: implement PopLinkedList.  Make sure you test for
  // and empty list and fail.  If the list is non-empty, there
  // are two cases to consider: (a) a list with a single element in it
  // and (b) the general case of a list with >=2 elements in it.
  // Be sure to call free() to deallocate the memory that was
  // previously allocated by PushLinkedList().
  
  if(list->num_elements == 0) {
	return false;
  }

  else if(list->num_elements == 1) {
	Verify333(list->head == list->tail);
	*payload_ptr = list->head->payload;
	free(list->head);
	list->head = NULL;
	list->tail = NULL;
	list->num_elements = 0U;
	return true;
  }

  else if(list->num_elements >= 1) {
	Verify333(list->head != NULL);
	Verify333(list->tail != NULL);
	*payload_ptr = list->head->payload;
	LinkedListNodePtr cur = list->head;
	list->head = list->head->next;
	list->head->prev = NULL;
	free(cur);
	list->num_elements -= 1U;
	return true;
  }

  return true;
}

bool AppendLinkedList(LinkedList list, LLPayload_t payload) {
  // defensive programming: check argument for safety.
  Verify333(list != NULL);

  // Step 5: implement AppendLinkedList.  It's kind of like
  // PushLinkedList, but obviously you need to add to the end
  // instead of the beginning.
  LinkedListNodePtr ln = (LinkedListNodePtr) malloc(sizeof(LinkedListNode));
  if(ln == NULL)
	return false;
  ln->payload = payload;

  if(list->num_elements == 0) {
	Verify333(list->head == NULL);
	Verify333(list->tail == NULL);
	list->head = list->tail = ln;
	ln->prev = NULL;
	ln->next = NULL;
	list->num_elements = 1U;
	return true;
  }
  else if(list->num_elements >= 1) {
	Verify333(list->head != NULL);
	Verify333(list->tail != NULL);
	list->tail->next = ln;
	ln->prev = list->tail;
	ln->next = NULL;
	list->tail = ln;
	list->num_elements += 1U;
	return true;
  }


  return true;
}

bool SliceLinkedList(LinkedList list, LLPayload_t *payload_ptr) {
  // defensive programming.
  Verify333(payload_ptr != NULL);
  Verify333(list != NULL);

  // Step 6: implement SliceLinkedList.
  if(list->num_elements == 0)
	return false;

  else if(list->num_elements == 1) {
	Verify333(list->head == list->tail);
	*payload_ptr = list->head->payload;
	free(list->head);
	list->head = NULL;
	list->tail = NULL;
	list->num_elements = 0U;
	return true;
  }
  else if(list->num_elements >= 1) {
	Verify333(list->head != NULL);
	Verify333(list->tail != NULL);
	*payload_ptr = list->tail->payload;
	LinkedListNodePtr cur = list->tail;
	list->tail = list->tail->prev;
	list->tail->next = NULL;
	free(cur);
	list->num_elements -= 1U;
	return true;
  }



  return true;
}

void SortLinkedList(LinkedList list, unsigned int ascending,
                    LLPayloadComparatorFnPtr comparator_function) {
  Verify333(list != NULL);  // defensive programming
  if (list->num_elements < 2) {
    // no sorting needed
    return;
  }

  // we'll implement bubblesort! nice and easy, and nice and slow :)
  int swapped;
  do {
    LinkedListNodePtr curnode;

    swapped = 0;
    curnode = list->head;
    while (curnode->next != NULL) {
      int compare_result = comparator_function(curnode->payload,
                                               curnode->next->payload);
      if (ascending) {
        compare_result *= -1;
      }
      if (compare_result < 0) {
        // bubble-swap payloads
        LLPayload_t tmp;
        tmp = curnode->payload;
        curnode->payload = curnode->next->payload;
        curnode->next->payload = tmp;
        swapped = 1;
      }
      curnode = curnode->next;
    }
  } while (swapped);
}

LLIter LLMakeIterator(LinkedList list, int pos) {
  // defensive programming
  Verify333(list != NULL);
  Verify333((pos == 0) || (pos == 1));

  // if the list is empty, return failure.
  if (NumElementsInLinkedList(list) == 0U)
    return NULL;

  // OK, let's manufacture an iterator.
  LLIter li = (LLIter) malloc(sizeof(LLIterSt));
  if (li == NULL) {
    // out of memory!
    return NULL;
  }

  // set up the iterator.
  li->list = list;
  if (pos == 0) {
    li->node = list->head;
  } else {
    li->node = list->tail;
  }

  // return the new iterator
  return li;
}

void LLIteratorFree(LLIter iter) {
  // defensive programming
  Verify333(iter != NULL);
  free(iter);
}

bool LLIteratorHasNext(LLIter iter) {
  // defensive programming
  Verify333(iter != NULL);
  Verify333(iter->list != NULL);
  Verify333(iter->node != NULL);

  // Is there another node beyond the iterator?
  if (iter->node->next == NULL)
    return false;  // no

  return true;  // yes
}

bool LLIteratorNext(LLIter iter) {
  // defensive programming
  Verify333(iter != NULL);
  Verify333(iter->list != NULL);
  Verify333(iter->node != NULL);

  // Step 7: if there is another node beyond the iterator, advance to it,
  // and return true.
  if(iter->node->next != NULL) {
	iter->node = iter->node->next;
   	return true;
  }



  // Nope, there isn't another node, so return failure.
  return false;
}

bool LLIteratorHasPrev(LLIter iter) {
  // defensive programming
  Verify333(iter != NULL);
  Verify333(iter->list != NULL);
  Verify333(iter->node != NULL);

  // Is there another node beyond the iterator?
  if (iter->node->prev == NULL)
    return false;  // no

  return true;  // yes
}

bool LLIteratorPrev(LLIter iter) {
  // defensive programming
  Verify333(iter != NULL);
  Verify333(iter->list != NULL);
  Verify333(iter->node != NULL);

  // Step 8:  if there is another node beyond the iterator, advance to it,
  // and return true.
  if(iter->node->prev != NULL) {
	iter->node = iter->node->prev;
	return true;
  }



  // nope, so return failure.
  return false;
}

void LLIteratorGetPayload(LLIter iter, LLPayload_t *payload) {
  // defensive programming
  Verify333(iter != NULL);
  Verify333(iter->list != NULL);
  Verify333(iter->node != NULL);

  // set the return parameter.
  *payload = iter->node->payload;
}

bool LLIteratorDelete(LLIter iter,
                      LLPayloadFreeFnPtr payload_free_function) {
  // defensive programming
  Verify333(iter != NULL);
  Verify333(iter->list != NULL);
  Verify333(iter->node != NULL);

  // Step 9: implement LLIteratorDelete.  This is the most
  // complex function you'll build.  There are several cases
  // to consider:
  //
  // - degenerate case: the list becomes empty after deleting.
  // - degenerate case: iter points at head
  // - degenerate case: iter points at tail
  // - fully general case: iter points in the middle of a list,
  //                       and you have to "splice".
  //
  // Be sure to call the payload_free_function to free the payload
  // the iterator is pointing to, and also free any LinkedList
  // data structure element as appropriate.

  if(iter->list->num_elements == 1) {
	payload_free_function(iter->node->payload);
	free(iter->node);
	iter->node = NULL;	// this must be set to NULL
	iter->list->head = NULL;
	iter->list->tail = NULL;
	iter->list->num_elements = 0U;
	return false;
  }

  else if(iter->node == iter->list->head) {
	LinkedListNodePtr cur = iter->node;
	iter->node = iter->node->next;
 	iter->list->head = iter->node;
	iter->node->prev = NULL;
	payload_free_function(cur->payload);
	free(cur);
	iter->list->num_elements -= 1U;
	return true;
  }
  
  else if(iter->node == iter->list->tail) {
	LinkedListNodePtr cur = iter->node;
	iter->node = iter->node->prev;
	iter->list->tail = iter->node;
	iter->node->next = NULL;
	payload_free_function(cur->payload);
	free(cur);
	iter->list->num_elements -= 1U;
	return true;
  }

  else {
	LinkedListNodePtr cur = iter->node;
	LinkedListNodePtr prev = iter->node->prev;
	LinkedListNodePtr next = iter->node->next;
	prev->next = next;
	next->prev = prev;
	iter->node = next;
	iter->list->num_elements -= 1U;
	payload_free_function(cur->payload);
	free(cur);
	return true;
  }

  return true;
}

bool LLIteratorInsertBefore(LLIter iter, LLPayload_t payload) {
  // defensive programming
  Verify333(iter != NULL);
  Verify333(iter->list != NULL);
  Verify333(iter->node != NULL);

  // If the cursor is pointing at the head, use our
  // PushLinkedList function.
  if (iter->node == iter->list->head) {
    return PushLinkedList(iter->list, payload);
  }

  // General case: we have to do some splicing.
  LinkedListNodePtr newnode =
    (LinkedListNodePtr) malloc(sizeof(LinkedListNode));
  if (newnode == NULL)
    return false;  // out of memory

  newnode->payload = payload;
  newnode->next = iter->node;
  newnode->prev = iter->node->prev;
  newnode->prev->next = newnode;
  newnode->next->prev = newnode;
  iter->list->num_elements += 1;
  return true;
}

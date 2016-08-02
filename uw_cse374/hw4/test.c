#include "lib.h"
#include "trie.h"
#include <assert.h>
#include <string.h>
#include <stdio.h>

void test_create_trie_node()
{
    struct trie_node* inter_node = create_trie_node(false, NULL);
    struct trie_node* end_node = create_trie_node(true, "hello");

    assert(inter_node->isEnd == false);
    assert(inter_node->word == false);
    assert(end_node->isEnd == true);
    assert(strcmp(end_node->word, "hello") == 0);
}


void test_search_node()
{
    struct trie_node* trie = create_empty_trie();
    insert_word(trie, "22737", "acres");
    insert_word(trie, "2273", "acre");
    insert_word(trie, "729", "pax");
    insert_word(trie, "22737", "bards");

    struct trie_node* s = search_trie(trie, "22737");
    assert(s != NULL);
    assert(s->isEnd == true);
    assert(strcmp(s->word, "acres") == 0);

    s = search_trie(trie, "22737#");
    assert(s != NULL);
    assert(s->isEnd == true);
    assert(strcmp(s->word, "bards") == 0);

    s = search_trie(trie, "22737##");
    assert(s == NULL);

    s = search_trie(trie, "729");
    assert(s != NULL);
    assert(s->isEnd == true);
    assert(strcmp(s->word, "pax") == 0);

    s = search_trie(trie, "227");
    assert(s != NULL);
    assert(s->isEnd == false);

    s = search_trie(trie, "56");
    assert(s == NULL);
}

int main(int argc, char **argv)
{
    test_create_trie_node();
    test_search_node();   
}

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

void test_insert_node()
{
    struct trie_node* trie = create_empty_trie();
    insert_word(trie, "22737", "acres");
    //insert_word(trie, "2273", "acre");
    insert_word(trie, "729", "pax");
    insert_word(trie, "22737", "bards");
    print_trie(trie);
}

int main(int argc, char **argv)
{
    test_create_trie_node();
    test_insert_node();   
}

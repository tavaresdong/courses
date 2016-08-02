#include "trie.h"
#include "lib.h"
#include <string.h>

// This method creates a trie node from heap and preset
// its corresponding word (if it is an end word)
// If it is not an end node, we could ignore the word parameter, just set it to NULL
struct trie_node* create_trie_node(bool isEnd,
                            const char* word)
{
    struct trie_node* node = (struct trie_node*) guarded_malloc(sizeof(struct trie_node));
    
    node->isEnd = false;
    node->word = NULL;
    if (isEnd)
    {
        node->isEnd = true;
        node->word = (char*) guarded_malloc(strlen(word) + 1); 
        strncpy(node->word, word, strlen(word) + 1);
    }
    for (int i = 0; i < 9; ++i)
    {
        node->child[i] = NULL;
    }
    return node;
}

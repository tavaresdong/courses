#ifndef TRIE_H_
#define TRIE_H_
#include <stdbool.h>

struct trie_node 
{
    bool isEnd;           // This node is end node or not
    char *word;           // If this node is end node, the corresponding word
    struct trie_node* child[9];  // The children 2:0 3:1 ... #:8
};

// Create a trie_node
extern struct trie_node* create_trie_node(bool isEnd, const char* word);

extern struct trie_node* create_empty_trie();

// Insert a word (its seq of numbers provided as nums)
// To the tree root
extern void insert_word(struct trie_node* root, 
                        const char* nums,
                        const char* word);

// Print the trie structure
extern void print_trie(struct trie_node* root);

extern struct trie_node* search_trie(struct trie_node* trie, const char* nums);

#endif

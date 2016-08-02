#include "trie.h"
#include "lib.h"
#include <string.h>
#include <stdio.h>

// This method creates a trie node from heap and preset
// its corresponding word (if it is an end word)
// If it is not an end node, we could ignore the word parameter, just set it to NULL
struct trie_node* create_trie_node(bool isEnd,
                            const char* word)
{
    struct trie_node* node = (struct trie_node*) 
                             guarded_malloc(sizeof(struct trie_node));
    
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

struct trie_node* create_empty_trie()
{
    return create_trie_node(false, NULL);   
}

void insert_word(struct trie_node* root,
                 const char* nums,
                 const char* word)
{
    if (root == NULL)
    {
        fprintf(stderr, "Error, the tree is NULL\n");
        return;
    }

    struct trie_node* cur = root;
    size_t i = 0;
    for (; i < strlen(nums); i++)
    {
        int num = nums[i] - '2';
        if (num < 0 || num > 7)
        {
            fprintf(stderr, "Invalid number :%s\n", nums);
            exit(EXIT_FAILURE);
        }
        if (cur->child[num] == NULL)
        {
            cur->child[num] = create_trie_node(false, NULL);
        }
        cur = cur->child[num];
    }

    // If this is the first time we found the sequence
    if (cur->isEnd == false)
    {
        cur->isEnd = true;
        cur->word = (char*) guarded_malloc(strlen(word) + 1);
        strncpy(cur->word, word, strlen(word) + 1);
    }
    else
    {
        while (cur->child[8] != NULL)
        {
            cur = cur->child[8];
        }
        cur->child[8] = create_trie_node(true, word);
    }
}

void print_trie_helper(int cnt, struct trie_node* root)
{
    if (root == NULL)
    {
        fprintf(stderr, "Trie is NULL\n");
        exit(EXIT_FAILURE);
    }

    for (int i = 0; i < cnt; i++)
        fprintf(stdout, "  ");

    if (root->isEnd)
    {
        fprintf(stdout, "node word: %s\n", root->word);
    }
    else
    {
        fprintf(stdout, "node, not end\n");
    }
    struct trie_node* same = root->child[8];
    while (same != NULL)
    {
        for (int i = 0; i < cnt; i++)
            fprintf(stdout, "  ");
        fprintf(stdout, "node sameword: %s\n", same->word);
        same = same->child[8];
    }
    for (int i = 0; i < 8; i++)
    {
        if (root->child[i] != NULL)
        {
            print_trie_helper(cnt + 1, root->child[i]);
        }
    }
}

void print_trie(struct trie_node* root)
{
    print_trie_helper(0, root);
}

struct trie_node* search_trie(struct trie_node* trie, 
                        const char* nums)
{
    struct trie_node* cur = trie;
    int i = 0;
    for (; cur != NULL && i < strlen(nums); ++i)
    {
        int ind = (nums[i] == '#')? 8 : nums[i] - '2';
        cur = cur->child[ind];
    }
    return cur;
}


void delete_trie(struct trie_node* trie)
{
    if (trie == NULL)
    {
        return;
    }

    if (trie->word != NULL)
    {
        guarded_free(trie->word);
    }

    for (int i = 0; i < 9; i++)
    {
        if (trie->child[i] != NULL)
        {
            delete_trie(trie->child[i]);
        }       
    }
    guarded_free(trie);
}

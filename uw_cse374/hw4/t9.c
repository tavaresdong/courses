#include "lib.h"
#include "trie.h"
#include "stdio.h"
#include <string.h>
#include <stdlib.h>

#define MAX_LINELEN 500

char convert(char c)
{
    int dist = c - 'a';
    if (dist <= 2)
        return '2';
    else if (dist <= 5)
        return '3';
    else if (dist <= 8)
        return '4';
    else if (dist <= 11)
        return '5';
    else if (dist <= 14)
        return '6';
    else if (dist <= 18)
        return '7';
    else if (dist <= 21)
        return '8';
    else
        return '9';
}


bool read_data(const char* filename, struct trie_node* trie)
{
    char buffer[MAX_LINELEN];
    FILE* file = fopen(filename, "r");
    if (file == NULL)
    {
        fprintf(stderr, "Cannot open file %s\n", filename);
        return false;
    }
    
    while (fgets(buffer, MAX_LINELEN, file) != NULL)
    {
        char *pos = strchr(buffer, '\n');
        if (pos != NULL)
        {
            *pos = '\0';
        }

        // Translate the word to num representation
        char* numbuf = (char*) guarded_malloc(strlen(buffer) + 1);
        int i = 0;
        for (; buffer[i] != '\0'; i++)
        {
            numbuf[i] = convert(buffer[i]);
        }
        numbuf[i] = '\0';
        //fprintf(stdout, "The word to insert is %s, number is %s\n", buffer, numbuf);
        insert_word(trie, numbuf , buffer);
        guarded_free(numbuf);
    }
    if (!feof(file))
    {
        fprintf(stderr, "Error while reading dictionary\n");
        return false;
    }
    fclose(file);
    return true;
}

int main(int argc, char **argv)
{
    if (argc != 2)
    {
        fprintf(stderr, "Usage: t9 FILE\n");
        exit(EXIT_FAILURE);
    }

    // Create an empty trie to store info from file
    struct trie_node* trie = create_empty_trie();
    if (read_data(argv[1], trie))
    {
        fprintf(stdout, "Enter \"exit\" to quit\n");
        struct trie_node* cur = NULL;
        char buf[MAX_LINELEN];
        while (true)
        {
            fprintf(stdout, "Enter key sequence, or '#' for next word\n");
            fprintf(stdout, ">");
            fscanf(stdin, "%s", buf);
            if (strncmp(buf, "exit", 4) == 0)
                break;

            if (strncmp(buf, "#", 1) == 0)
            {
                if (cur == NULL)
                {
                    fprintf(stdout, "\tThere are no more T9onyms\n");
                }
                else
                {
                    cur = search_trie(cur, "#");
                    if (cur == NULL)
                    {
                        fprintf(stdout, "\tThere are no more T9onyms\n");
                    }
                    else
                    {
                        fprintf(stdout, "\t'%s'\n", cur->word);
                    }
                }
            }
            else
            {
                fprintf(stdout, "to search is %s\n", buf);
                cur = search_trie(trie, buf);
                if (cur != NULL && cur->isEnd)
                {
                    fprintf(stdout, "\t'%s'\n", cur->word);
                }
                else
                {
                    fprintf(stdout, "\tThere are no more T9onyms\n");
                }
            }
        }
    }
    delete_trie(trie);
    exit(EXIT_SUCCESS);
}

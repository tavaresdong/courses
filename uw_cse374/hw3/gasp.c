#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <ctype.h>


#define MAX_LINE_LENGTH 500
#define MAX_PATTERN_LENGTH 100

void normalize(char* str)
{
    size_t len = strlen(str);
    for (size_t i = 0; i < len; i++)
    {
        str[i] = tolower(str[i]);
    }
}

// Find if option i is in this string
bool should_ignore_case(char *arg, bool orig)
{
    if (orig == true) 
    {
        return true;
    }
    char* pos = strchr(arg, 'i');
    if (pos != NULL)
    {
        return true;
    }
    return false;
}

// Find if option n is in this string
bool should_contain_number_lines(char *arg, bool orig)
{
    if (orig == true)
        return true;
    char* pos = strchr(arg, 'n');
    if (pos != NULL)
        return true;
    return false;
}

// This line for test of string

// This line for test of StRing

void do_pattern_matching(bool ignore_case,
                         bool contain_number_lines,
                         char* filename, FILE* file, char* pattern)
{
    int lineno = 1;
    char buffer[MAX_LINE_LENGTH];
    char orig[MAX_LINE_LENGTH];
    while (fgets(buffer, MAX_LINE_LENGTH, file) != NULL)
    {
        if (ignore_case)
        {
            strncpy(orig, buffer, MAX_LINE_LENGTH);
            normalize(buffer);
        }
        char* match = strstr(buffer, pattern);
        if (match != NULL)
        {
            if (contain_number_lines)
            {
                fprintf(stdout, "%s, %d: %s\n", filename, lineno, orig);
            }
            else
            {
                fprintf(stdout, "%s: %s\n", filename, orig);
            }
        }
        lineno += 1;
    }
    if (!feof(file))
    {
        fprintf(stderr, "Encountered error while reading %s\n", filename);
    }
}

int main(int argc, char **argv)
{
    if (argc < 3)
    {
        fprintf(stderr, "Usage: %s [options] STRING FILE...\n", argv[0]);
        exit(-1);
    }

    bool ignore_case = false;
    bool contain_number_lines = false;
    int i = 1;
    for (; i < argc - 2; i++)
    {
        if (argv[i][0] != '-')
            break;
        ignore_case = should_ignore_case(argv[i], ignore_case);
        contain_number_lines = should_contain_number_lines(argv[i], 
                               contain_number_lines);
    }

    char* pattern = argv[i];
    if (strlen(pattern) > MAX_PATTERN_LENGTH)
    {
        fprintf(stderr, "Pattern is longer than %d length\n",
                MAX_PATTERN_LENGTH);
        exit(-1);
    }

    if (ignore_case)
    {
        normalize(pattern);
    }

    for (i += 1; i < argc; i++)
    {
        FILE* file = fopen(argv[i], "r");
        if (file == NULL) 
        {
            fprintf(stderr, "Error opening file: %s, error: %d\n",
                    argv[i], errno);
            continue;
        }
        do_pattern_matching(ignore_case, contain_number_lines,
                            argv[i], file, pattern);
        fclose(file);
    }

    exit(0);
}

#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <dirent.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <vector>
#include <string>
#include <cstring>
#include <memory>
#include "parse.h"
#include "command.h"


int read_command(char* buf, int size);
void handle_cd(char* path);


int main()
{
    char buf[256];
    char data[256];
    int status;
    
    while (read_command(buf, sizeof(buf)) != -1)
    {
        // If the command is exit, exit the shell
        if (strncmp(buf, "exit", 4) == 0) 
        {
            break;
        }
        else if (buf[0] == 'c' && buf[1] == 'd')
        {
            // Handle the cd builtin
            buf[strlen(buf) - 1] = 0;
            handle_cd(&buf[3]);
        }
        else if (strncmp(buf, "pwd", 3) == 0)
        {   
            // Handle the pwd builtin
            getcwd(data, sizeof(data));
            fprintf(stdout, "Current working directory is %s\n", data);
        }
        else
        {
            if (fork() == 0)
            {
                parse_command(buf)->run_command();
            }
            wait(&status);
        }
    }

    exit(EXIT_SUCCESS);
}

// Output prompt and read command in a line
int read_command(char* buf,  int size)
{
    fprintf(stdout, "yuchen shell$ ");
    memset(buf, 0, size);
    
    // The shell reads input from stdin
    fgets(buf, size, stdin);
    if (buf[0] == 0) 
    {
        return -1;
    }
    return 0;
}


// Function to handle cd to some other directory
void handle_cd(char *path)
{
    char* path_to = path;

    if (path[0] == 0 || strncmp(path, "~", 1) == 0)
    {
        // No argument, cd to home directory
        path_to = getenv("HOME");
    }
    else if (path[0] == '.' && path[1] == 0)
    {
        // do nothing
    }
    else if (path[0] == '.' && path[1] == '.' && path[2] == 0)
    {
        // TODO get parent directory
    }


    if (chdir(path_to) != 0)
    {
        fprintf(stderr, "Failed to cd to %s\n", path_to);
    }
}








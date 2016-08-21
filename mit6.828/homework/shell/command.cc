#include "command.h"
#include "dirent.h"
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <cstring>
#include <cstdlib>
#include <unistd.h>

using std::vector;
using std::string;
using std::shared_ptr;


void Coloncommand::run_command() const
{
    int status;
    if (fork() == 0)
    {
        first->run_command();
    }
    wait(&status);
    second->run_command();
    exit(EXIT_SUCCESS);
}



void Pipecommand::run_command() const
{
    int p[2];   // Pipe fds
    int status = pipe(p);
    if (status != 0)
    {
        fprintf(stderr, "Failed creating pipe\n");
        exit(EXIT_FAILURE);
    }

    // First command
    if (fork() == 0)
    {
        if (dup2(p[1], STDOUT_FILENO) == -1)
        {
            fprintf(stderr, "Failed dup2 stdout\n");
            exit(EXIT_FAILURE);
        }
        close(p[1]);
        first->run_command();
    }

    // Second Command
    if(fork() == 0)
    {
        close(p[1]);
        if (dup2(p[0], STDIN_FILENO) == -1)
        {
            fprintf(stderr, "Failed dup2 stdin\n");
            exit(EXIT_FAILURE);
        }
        close(p[0]);
        second->run_command();
    }
    // Shell wait for the two commands
    close(p[1]);
    close(p[0]);
    wait(&status);
    wait(&status);
    exit(EXIT_SUCCESS);
}

string Execommand::find_command(string command)
{
    char *paths = getenv("PATH");
    char *path_dir = strtok(paths, ":");
    while (path_dir != NULL)
    {
        DIR* dir = opendir(path_dir);
        if (dir != NULL)
        {
            struct dirent* entry = NULL;
            while ((entry = readdir(dir)) != NULL)
            {
                if (strcmp(entry->d_name, command.c_str()) == 0)
                {
                    /*
                    char *path_found = malloc(strlen(path_dir) + strlen(command) + 2);
                    path_found = strcat(path_found, path_dir);
                    path_found = strcat(path_found, "/");
                    path_found = strcat(path_found, command);
                    */
                    string path_found(path_dir);
                    path_found += "/";
                    path_found += command;
                    return path_found;
                }
            }
        }
        else
        {
            fprintf(stderr, "cannot open dir : %s\n", path_dir);
        }
        path_dir = strtok(NULL, ":");
    }
    return string();
}


void Execommand::run_command() const
{
    //fprintf(stdout, "Running command\n");
    //fprintf(stdout, "Running:  args size %d\n", (int)args.size());
    if (args.size() == 0)
    {
        exit(EXIT_FAILURE);
    }
    string cmdpath = find_command(args[0]);
    if (!cmdpath.empty())
    {
        char** argv = new char*[args.size() + 1];
        size_t i = 0;
        //fprintf(stdout, "command path is %s\n", cmdpath.c_str());
        for (; i < args.size(); i++)
        {
            argv[i] = new char[args[i].size() + 1];
            strcpy(argv[i], args[i].c_str());
            //fprintf(stdout, "arg %d : %s\n", i, argv[i]);
        }
        argv[i] = 0;
        execv(cmdpath.c_str(), argv);
    }
    fprintf(stderr, "Failed to find the command\n");
    exit(EXIT_SUCCESS);
}



void Redircommand::run_command() const
{
    int openfd;
    if (type == '>')
    {
        openfd = open(file, mode, S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);
    }
    else
    {
        openfd = open(file, mode);
    }
    if (openfd == -1)
    {
        fprintf(stderr, "Failed to open file %s\n", file);
        exit(EXIT_FAILURE);
    }
    if (dup2(openfd, fd) == -1)
    {
        fprintf(stderr, "Failed dupping\n");
    }
    close(openfd);
    cmd->run_command();
}

void Redircommand::set_args(const vector<string>& paras)
{
    cmd->set_args(paras);
}

Redircommand::Redircommand(shared_ptr<Command> c, char *f, int t) : type(t), cmd(c), file(f)
{
    mode = (type == '<') ? O_RDONLY : O_WRONLY|O_CREAT|O_TRUNC;
    fd = (type == '<') ? 0 : 1;
}

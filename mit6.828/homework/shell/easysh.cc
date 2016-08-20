#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <dirent.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <vector>
#include <string>
#include <memory>

using std::vector;
using std::string;
using std::shared_ptr;


class Command
{
public:
    virtual void run_command() const = 0;
};

class Coloncommand : public Command
{
public:
    void run_command() const
    {
        int status;
        if (fork() == 0)
        {
            first->run_command();
        }
        wait(&status);
        second->run_command();
    }

    Coloncommand(shared_ptr<Command> f, shared_ptr<Command> s) : first(f), second(s) { }
private:
    shared_ptr<Command> first;
    shared_ptr<Command> second;
};


class Pipecommand : public Command
{
public:
    void run_command() const
    {
        // TODO
    }
    Pipecommand(shared_ptr<Command> f, shared_ptr<Command> s) : first(f), second(s) { }
private:
    shared_ptr<Command> first;
    shared_ptr<Command> second;     
};

// Execution command, find the path of command and execute
class Execommand : public Command
{
public:
    void run_command() const
    {
        //fprintf(stdout, "Running command\n");
        fprintf(stdout, "Running:  args size %d\n", (int)args.size());
        if (args.size() == 0)
        {
            exit(EXIT_FAILURE);
        }
        string cmdpath = find_command(args[0]);
        if (!cmdpath.empty())
        {
            char** argv = new char*[args.size() + 1];
            int i = 0;
            fprintf(stdout, "command path is %s\n", cmdpath.c_str());
            for (; i < args.size(); i++)
            {
                argv[i] = new char[args[i].size() + 1];
                strcpy(argv[i], args[i].c_str());
                fprintf(stdout, "arg %d : %s\n", i, argv[i]);
            }
            argv[i] = 0;
            execv(cmdpath.c_str(), argv);
        }
        fprintf(stderr, "Failed to find the command\n");
    }

    static string find_command(string command)
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
    
    Execommand(const vector<string>& paras) : args(paras) {}
private:
    vector<string> args;
};


int read_command(char* buf, int size);
void handle_cd(char* path);
shared_ptr<Command> parse_command(char *line);
shared_ptr<Command> parse_colon(char **cur, char *end);
shared_ptr<Command> parse_exec(char **cur, char *end);
shared_ptr<Command> parse_pipe(char **cur, char *end);



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
                shared_ptr<Command> cmd = parse_command(buf);
                cmd->run_command();
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



/* Parsing related code */

string whitespace = " \r\n\t\v";
string symbols = "<>|;";

int get_token(char **ps, char* es, char **q, char **eq)
{
    char *s;
    int ret;
    
    s = *ps;
    while (s < es && whitespace.find(*s) != string::npos)
    {
        s++;
    }
    if (q)
        *q = s;
    ret = *s;
    switch (*s) 
    {
    case 0:
        break;
    case '<':
    case '>':
    case '|':
    case ';':
        s++;
        break;
    default:
        ret = 'a';
        while (s < es && whitespace.find(*s) == string::npos
                      && symbols.find(*s) == string::npos)
            s++;
        break;
    }
    if (eq)
        *eq = s;
    while (s < es && whitespace.find(*s) != string::npos)
        s++;
    *ps = s;
    return ret;
}


int peek(char **ps, char *es, const char *tokens)
{
    char *s = *ps;
    while (s < es && whitespace.find(*s) != string::npos)
        s++;
    *ps = s;
    return *s && strchr(tokens, *s);
}

char *copy_str(char *s, char* es)
{
    int num = es - s;
    char *str = (char*) malloc(num + 1);
    if (str == NULL)
    {
        fprintf(stderr, "Malloc failed\n");
        exit(EXIT_FAILURE);
    }
    strncpy(str, s, num);
    str[num] = 0;
    return str;
}

// Parse the command to it's related type
shared_ptr<Command> parse_command(char *buf)
{
    char *end = buf + strlen(buf);
    char *cur = buf;
    shared_ptr<Command> cmd = parse_colon(&cur, end);

    // Neglect trailing whitespaces
    peek(&cur, end, "");
    if (cur != end)
    {
        fprintf(stderr, "parse not complete\n");
        exit(EXIT_FAILURE);
    }
    return cmd;
}


shared_ptr<Command> parse_colon(char **cur, char *end)
{
    // TODO, add pipe support
    shared_ptr<Command> cmd = parse_pipe(cur, end);
    if (peek(cur, end, ";"))
    {
        get_token(cur, end, 0, 0);
        cmd = std::make_shared<Coloncommand>(cmd, parse_colon(cur, end));
    }
    return cmd;
}


shared_ptr<Command> parse_pipe(char **cur, char *end)
{
    shared_ptr<Command> cmd = parse_exec(cur, end);
    if (peek(cur, end, "|")
    {
        get_token(cur, end, 0, 0);
        cmd = std::make_shared<Pipecommand>(cmd, parse_pipe(cur, end));
    }
    return cmd;
}


shared_ptr<Command> parse_exec(char **cur, char *end)
{
    char *token_beg, *token_end;
    int status, argc = 0;
    vector<string> args;
    
    // TODO: add redir support
    while (!peek(cur, end, ";|"))
    {
        if ((status = get_token(cur, end, &token_beg, &token_end)) == 0)
            break;
        if (status != 'a')
        {
            fprintf(stderr, "syntax error\n");
            exit(EXIT_FAILURE);
        }
        string arg(copy_str(token_beg, token_end));
        args.push_back(arg);
    }   
    return std::make_shared<Execommand>(args);
}




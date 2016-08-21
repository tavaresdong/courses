/* Parsing related code */

#include <cstring>
#include "command.h"

using std::string;
using std::shared_ptr;
using std::vector;
using std::make_shared;

static string whitespace = " \r\n\t\v";
static string symbols = "<>|;";

static int get_token(char **ps, char* es, char **q, char **eq)
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


static int peek(char **ps, char *es, const char *tokens)
{
    char *s = *ps;
    while (s < es && whitespace.find(*s) != string::npos)
        s++;
    *ps = s;
    return *s && strchr(tokens, *s);
}

static char *copy_str(char *s, char* es)
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

shared_ptr<Command> parse_redir(shared_ptr<Command> cmd, char **cur, char *end)
{
    int token;
    char *token_beg, *token_end;
    
    while(peek(cur, end, "<>"))
    {
        token = get_token(cur, end, 0, 0);
        if (get_token(cur, end, &token_beg, &token_end) != 'a')
        {
            fprintf(stderr, "Missing file to redir to\n");
            exit(EXIT_FAILURE);
        }
        if (token == '<')
        {
            cmd = std::make_shared<Redircommand>(cmd, copy_str(token_beg, token_end), '<');
        }
        else
        {
            cmd = std::make_shared<Redircommand>(cmd, copy_str(token_beg, token_end), '>');
        }
            
    }
    return cmd;
}


shared_ptr<Command> parse_exec(char **cur, char *end)
{
    char *token_beg, *token_end;
    int status;
    vector<string> args;
    
    // TODO: add redir support
    shared_ptr<Command> cmd = std::make_shared<Execommand>();
    shared_ptr<Command> redir = parse_redir(cmd, cur, end);
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
        redir = parse_redir(redir, cur, end);
    }   
    redir->set_args(args);
    return redir;
}

shared_ptr<Command> parse_pipe(char **cur, char *end)
{
    shared_ptr<Command> cmd = parse_exec(cur, end);
    if (peek(cur, end, "|"))
    {
        get_token(cur, end, 0, 0);
        cmd = std::make_shared<Pipecommand>(cmd, parse_pipe(cur, end));
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












#ifndef COMMAND_H_
#define COMMAND_H_

#include <memory>
#include <vector>
#include <string>


class Command
{
public:
    virtual void run_command() const = 0;
    virtual void set_args(const std::vector<std::string>& as) = 0;
};


class Coloncommand : public Command
{
public:
    void run_command() const;

    Coloncommand(std::shared_ptr<Command> f, std::shared_ptr<Command> s) : first(f), second(s) { }
    void set_args(const std::vector<std::string>& as) {}
private:
    std::shared_ptr<Command> first;
    std::shared_ptr<Command> second;
};


class Pipecommand : public Command
{
public:
    void run_command() const;
    Pipecommand(std::shared_ptr<Command> f, std::shared_ptr<Command> s) : first(f), second(s) { }
    void set_args(const std::vector<std::string>& as) {}
private:
    std::shared_ptr<Command> first;
    std::shared_ptr<Command> second;     
};

// Execution command, find the path of command and execute
class Execommand : public Command
{
public:
    void run_command() const;
    void set_args(const std::vector<std::string>& paras)
    {
        args = paras;
    }

    static std::string find_command(std::string command);
    
    Execommand(const std::vector<std::string>& paras) : args(paras) {}
    Execommand() { }
private:
    std::vector<std::string> args;

};


class Redircommand : public Command
{
public:
    void run_command() const;
    void set_args(const std::vector<std::string>& paras);
    Redircommand(std::shared_ptr<Command> c, char* f, int t);
private:
    int type;
    std::shared_ptr<Command> cmd;
    char* file;
    int  mode;
    int fd;
};
#endif

#ifndef PARSE_H_
#define PARSE_H_
#include "command.h"

extern std::shared_ptr<Command> parse_command(char *line);

#endif

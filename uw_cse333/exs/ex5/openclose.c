#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

int main(void) {
	int fd = open("foo.txt", O_RDONLY);
	if(fd == 1)
		printf("error");

	write(STDOUT_FILENO, "hello world\n", 13);
	close(fd);
}

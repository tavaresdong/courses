#include <sys/types.h>
#include <dirent.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>


static void usage() {
	fprintf(stderr, "use like this: ex7 directory\n");
	exit(EXIT_FAILURE);
}

static void read_and_print(const char* dirname, char filename[]) {
	// we need the full name
	size_t fullpathlen = strlen(dirname) + strlen(filename) + 2;
	char buf[1024];

	char *fullname = (char*) malloc(fullpathlen);
	if(fullname == NULL) {
		fprintf(stderr, "Error allocating memory\n");
		exit(EXIT_FAILURE);
	}
	snprintf(fullname, fullpathlen, "%s/%s", dirname, filename);
	int fd = open(fullname, O_RDONLY);
	if(fd == -1) {
		perror("file open failed.\n");
		abort();
	} else {
		fprintf(stdout, "%s is opened successfully.\n", fullname);
		while (1) {
			ssize_t rd = read(fd, buf, 1024);
			if (rd == 0) break;
			else if (rd == -1 && errno == EINTR) {
				fprintf(stderr, "Some Error happened.\n");
				exit(EXIT_FAILURE);		
			} else {
				if(fwrite(buf, rd, 1, stdout) != 1) {
					fprintf(stderr, "fwrite error\n");	
					exit(EXIT_FAILURE);
				}
			}
			
		}
	}

	close(fd);

}

static void find_txt_in_dir(const char* dirname, DIR *d) {
	struct dirent *dir_entry = NULL;
	while( dir_entry = readdir(d) ) {
		if(dir_entry->d_type == DT_REG) {
			// a regular file, get its name
			unsigned int len = strlen(dir_entry->d_name);
			// attention, need to test whether the length is > 4
			if (len >= 4 && strncmp( &dir_entry->d_name[len - 4], ".txt", 4) == 0 ) {
				// we found one .txt file
				// printf("%s\n", dir_entry->d_name);
				// read and print the file
				read_and_print(dirname, dir_entry->d_name);
			}	
		}
	}
}

int main(int argc, char **argv) {
	DIR *dir = NULL;

	if (argc != 2) {
		usage();
		return 0;
	}

	char *dirname = argv[1];
	dir = opendir(dirname);
	if(dir == NULL) {
		perror("Cannot open directory.\n");
		return 0;
	}
	
	find_txt_in_dir(argv[1], dir);
	
}

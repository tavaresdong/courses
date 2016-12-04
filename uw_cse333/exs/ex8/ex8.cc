#include <iostream>
#include <cstdlib>
#include <cassert>

using namespace std;

void usage() {
	cerr << "The number must be positive!" << endl;
	exit(EXIT_FAILURE);
}

void factorize(int tofact);

int main(void) {
	cout << "Which positive integer would you like me to factorize?";

	int tofact = 0;
	cin >> tofact;

	if (tofact <= 0) usage();

	factorize(tofact);

}


void factorize(int tofact) {
	
	assert(tofact > 0);
	int half = tofact / 2;
	for (int num = 1; num <= tofact / 2; num++) {
		if (tofact % num == 0)
			cout << num << " ";
	}
	cout << tofact << endl;
};

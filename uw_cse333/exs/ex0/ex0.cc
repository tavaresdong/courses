#include <iostream>
#include <cstdlib>

using namespace std;


/**
 * this function calculates PI with num rounds of iteration
 */
double calcPiWithPrec(int num) {
	double start = 3.0;
	for(int rd = 1; rd <= num; ++rd) {
		double toadd = 4.0 / ((2 * rd) * (2 * rd + 1) * (2 * rd + 2));	
		if(rd % 2 == 0) toadd *= -1;
		start += toadd;

	}
	return start;

}

int main(int argc, char **argv) {

	cout << argc << endl;
	if(argc != 2)
		cout << "usage : ex0 20" << endl;

	char* numStr = argv[1];
	int num = atoi(numStr);
	cout << num << endl;
	double result = calcPiWithPrec(num);
	cout << result << endl;
}


#include <iostream>
using namespace std;

void doublePassTest(double passed) {
	passed = 3.0;

};


void manipulateArray(char arr[]) {
	arr[0] = 'b';

};

int main() {
	double d = 2.0, orig = d;
 	doublePassTest(d);
	if(d == orig) cout << " double is passed by value" << endl;
	else cout << " double is passed by reference" << endl;

	char arr[3] = {'a', 'b', 'c'};
	char arr2[3] = {'a', 'b', 'c'};

	manipulateArray(arr);
	if(arr[0] != arr2[0]) cout << "array is passed by reference" << endl;
	else cout << "array is passed by value" << endl;
	
	return 0;
}

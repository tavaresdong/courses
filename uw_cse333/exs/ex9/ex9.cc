#include "IntPair.h"
#include <iostream>


void Test(IntPair ip) {
	int v1 = 0, v2 = 0;
	ip.GetVar1(&v1);
	ip.GetVar2(&v2);
	ip.SetVar1(v1 + 1);
	ip.SetVar2(v2 + 2);

}


int main(void) {
	int v1 = 0, v2 = 0;
	IntPair ip(2, 3);
	Test(ip);
	ip.GetVar1(&v1);
	ip.GetVar2(&v2);
	if(v1 == 2 && v2 == 3)
		std::cout << "Is pass by value." << std::endl;
	else if(v1 == 3 && v2 == 4)
		std::cout << "Is pass by reference." << std::endl;

}

#ifndef INT_PAIR_H
#define INT_PAIR_H

class IntPair {
public:
	IntPair(int v1, int v2) : var1(v1), var2(v2) {}
	void GetVar1(int *pv1);
	void GetVar2(int *pv2);

	void SetVar1(int v1);
	
	void SetVar2(int v2);

private:
	int var1, var2;

};

#endif

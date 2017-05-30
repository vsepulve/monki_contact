#if !defined(_DATO_H)
#define _DATO_H

#include <iostream>
#include <fstream>
#include <stdio.h>

using namespace std;

class Dato{
public:
	int id;
	Dato();
	virtual ~Dato();
	virtual Dato *clonar();
	virtual char *texto_print();
};

ostream& operator<<(ostream& os, const Dato& dato);

#endif


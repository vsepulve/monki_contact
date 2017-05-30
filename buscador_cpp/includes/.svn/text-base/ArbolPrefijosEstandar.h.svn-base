#ifndef ARBOL_PREFIJOS_ESTANDAR_H
#define ARBOL_PREFIJOS_ESTANDAR_H

#include <iostream>
#include <string.h>
#include <list>
#include <set>

#include <NodoEstandar.h>
 
using namespace std;

class ArbolPrefijosEstandar{
private:
	NodoEstandar *raiz;
	unsigned int max_palabra;
	char *buffer;
	static const bool debug=false;
	
public:
	ArbolPrefijosEstandar();
	ArbolPrefijosEstandar(list<string*> *lista);
	ArbolPrefijosEstandar(list<char*> *lista);
	~ArbolPrefijosEstandar();
	
	void print();
	void consultar(const char *palabra, list<string> *res);
	void consultar(const char *palabra, list<string> *res, unsigned int k);
//	void consultar(string, list<string*>*);
};
 
#endif // ARBOL_PREFIJO_H

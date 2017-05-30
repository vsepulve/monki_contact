#ifndef NODO_ESTANDAR_H
#define NODO_ESTANDAR_H

#include <iostream>
#include <string.h>
#include <list>
#include <set>

using namespace std;

class NodoEstandar{
private:
//	bool pal;
	char letra;
	unsigned char n_hijos;
	NodoEstandar *hijos;
	
	static const bool debug=false;
	
public:
	NodoEstandar();
	NodoEstandar(char _letra);
	~NodoEstandar();
	NodoEstandar* getHijo(unsigned char pos);
	
	char getLetra();
	unsigned char numHijos();
	bool esPalabra();
	
	void setNumHijos(unsigned char _n_hijos);
	void setPalabra(bool _pal);
	void setLetra(char _letra);
	void setHijos(set<string> &palabras_hijos);
	
	//entrega la distancia minima al siguiente nodo que sea una palabra o una hoja
	int disMinSigPal();
	
//	virtual char getType();
	void print(char *, int);
	void printEnList(char *buff, int i, list<string> *res, int max_level);
	
	NodoEstandar* getNodoPal(const char *,int);
	
	void consultar(char *buff, unsigned int nivel, set< pair<int, string> > *res, unsigned int k);
	
};
 
#endif // NODO_H



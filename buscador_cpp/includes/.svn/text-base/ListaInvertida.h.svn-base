#if !defined(_LISTA_INVERTIDA_H)
#define _LISTA_INVERTIDA_H

#include <stdio.h>
#include <string.h>
#include <iostream>
#include <list>
#include <list>

using namespace std;

class ListaInvertida{

private:
	int n_docs;
	int *docs;
	float *vals;
	int cur_index;
	
public:

	ListaInvertida();
	ListaInvertida(list< pair<int, float> > *lista);
	ListaInvertida(list<list< pair<int, float> >* > *listas);
	ListaInvertida(list<ListaInvertida*> *listas);
	~ListaInvertida();
	
	//deja la lista en el primer documento
	//notar que una lista puede no tener next, y aun estar en una posicion valida (la ultima)
	void reset();
	
	bool hasNext();
	
	void next();
	
	int size();
	
	int getDocId();
	
	float getValor();
	
};

#endif


#if !defined(_INDICE_PIVOTES_SIMPLE_H)
#define _INDICE_PIVOTES_SIMPLE_H

#include <stdio.h>
#include <stdlib.h>
#include <iostream>

#include <list>
#include <set>

#include "Indice.h"
#include "Dato.h"
#include "Distancia.h"

using namespace std;

class IndicePivotesSimple : public Indice{

private:
	
	Distancia* dist;
	int n_datos;
	list<Dato*> *datos;
	
	int n_pivotes;
	list<Dato*> *pivotes;
	
	float **matriz;
	float *matriz_q;
	
	int evitadas;
	int realizadas;
	
public:

	IndicePivotesSimple();
	IndicePivotesSimple(list<Dato*> *_datos, Distancia *_dist, int _n_pivotes);
	~IndicePivotesSimple();
	
	int size();
	int rango(Dato *q, float r, list<pair<Dato*, float> > *res);
	int rango_minimo(Dato *q, list<pair<Dato*, float> > *res);
	
};


#endif


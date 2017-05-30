#if !defined(_INDICE_H)
#define _INDICE_H

#include "Distancia.h"
#include "Dato.h"
#include <list>

class Indice{
	
private:
	
public:
	Indice();
	Indice(list<Dato*> *lista, Distancia *_dist);
	//borrar los datos pero no la distancia
	virtual ~Indice();
	
	virtual int size();
	virtual int rango(Dato *q, double r, list<pair<Dato*, double> > *res);
	virtual int rango_minimo(Dato *q, list<pair<Dato*, double> > *res);
	
};

#endif

#if !defined(_INDICE_LINEAL_H)
#define _INDICE_LINEAL_H

#include "Indice.h"
#include <list>
#include <set>

class IndiceLineal : public  Indice {
	
private:
	
	Distancia* dist;
	int n_datos;
	list<Dato*> *datos;
	
public:
	IndiceLineal();
	IndiceLineal(list<Dato*> *_datos, Distancia *_dist);
	~IndiceLineal();
	
	int size();
	int rango(Dato *q, double r, list< pair<Dato*, double> > *res);
	int rango_minimo(Dato *q, list< pair<Dato*, double> > *res);
	
};

#endif





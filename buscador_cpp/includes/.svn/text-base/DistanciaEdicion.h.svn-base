#if !defined(_DISTANCIA_EDICION_H)
#define _DISTANCIA_EDICION_H

#include "Distancia.h"
#include "DatoTexto.h"

class DistanciaEdicion : public Distancia{
private:
	inline int min(int i1, int i2, int i3);
public:
	int max_palabra;
	//la matriz es de (max+1)x(max+1) !
	int **m;
	
	DistanciaEdicion();
	DistanciaEdicion(int _max_palabra);
	~DistanciaEdicion();
	
	double d(Dato &d1, Dato &d2);
};

#endif


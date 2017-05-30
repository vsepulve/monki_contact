#if !defined(_DISTANCIA_EDICION_PESOS_H)
#define _DISTANCIA_EDICION_PESOS_H

#include "Distancia.h"
#include "DatoTexto.h"

class DistanciaEdicionPesos : public Distancia{
private:
	inline int min(int i1, int i2, int i3);
public:
	int max_palabra;
	//la matriz es de (max+1)x(max+1) !
	int **m;
	
	DistanciaEdicionPesos();
	DistanciaEdicionPesos(int _max_palabra);
	~DistanciaEdicionPesos();
	
	double d(Dato &d1, Dato &d2);
};

#endif


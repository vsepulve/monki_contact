#if !defined(_SERVIDOR_CONSULTAS_H)
#define _SERVIDOR_CONSULTAS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>

#include <list>
#include <map>
#include <set>

#include "RankeadorBasico.h"
#include "IndiceInvertido.h"

using namespace std;

class ServidorConsultas{

private:
	RankeadorBasico *rankeador;
	
	//este comparador es equivalente al del Rankeador, pero no usa apariciones
	//se usa para ordenar los resultados en la busqueda inter-usuarios
	class Comparador : public std::binary_function<const pair<int, float>&, const pair<int, float>&, bool> {
	public:
		Comparador(){
		}
		inline bool operator()(const pair<int, float> &a, const pair<int, float> &b){
			if (a.second < b.second){
				return false;
			}
			else{
				return true;
			}
		}
	};
	
	Comparador comp;
	
	int last_update_user;
	bool last_update;
	
public:
	
	ServidorConsultas();
	ServidorConsultas(const char *archivo_ii, bool binario);
	ServidorConsultas(const char *archivo_ii, const char *archivo_grupos, bool binario);
	~ServidorConsultas();
	
	//recibe el c-string con la consulta, y escribe en el c-string de salida (supone que tiene memoria suficiente)
	int consultar(char *consulta, char *salida);
	
	bool lastUpdate(){return last_update;}
	int lastUpdatedUser(){return last_update_user;}
	
};


#endif


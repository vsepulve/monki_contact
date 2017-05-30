#if !defined(_RANKEADOR_BASICO_H)
#define _RANKEADOR_BASICO_H

#include <stdio.h>
#include <string.h>
#include <iostream>
#include <list>

#include "IndiceInvertidoAtributos.h"
#include "ListaInvertida.h"

using namespace std;

class RankeadorBasico{

private:
	IndiceInvertidoAtributos *indice;
	list<ListaInvertida*> *listas;
	list<float> *pesos;
	
	class ComparadorVal : public std::binary_function<const pair<int, float>&, const pair<int, float>&, bool> {
	public:
		map<int, int> *apariciones;
		ComparadorVal(){
		}
		inline bool operator()(const pair<int, float> &a, const pair<int, float> &b){
			//primero ordenar por el numero de terminos de consulta en cada doc
			if((*apariciones)[a.first] < (*apariciones)[b.first]){
				return false;
			}
			else if((*apariciones)[a.first] > (*apariciones)[b.first]){
				return true;
			}
			else{
				//si el numero de terminos es igual, usar el puntaje total
				if (a.second < b.second){
					return false;
				}
				else{
					return true;
				}
			}//else... apariciones iguales
		}//fin metodo comparacion
	};
	
	ComparadorVal comp;
	
public:

	RankeadorBasico();
	RankeadorBasico(IndiceInvertidoAtributos *_indice);
	~RankeadorBasico();
	
//	list< pair<int, float> > *consultar(int user_id, char **terms, char **valores, int n_terms);
	list< pair<int, float> > *consultar(int user_id, list< pair<string, float> > *lista_terms, list<string> *atributos);
	list< pair<int, float> > *consultarAnd(int user_id, list< pair<string, float> > *lista_terms, list<string> *atributos);
	IndiceInvertidoAtributos *getIndice();
	
};

#endif


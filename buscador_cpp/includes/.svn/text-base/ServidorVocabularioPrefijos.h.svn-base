#if !defined(_SERVIDOR_VOCABULARIO_PREFIJOS_H)
#define _SERVIDOR_VOCABULARIO_PREFIJOS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>

#include <list>
#include <set>
#include <map>

#include "IndiceInvertido.h"
#include "IndiceInvertidoAtributos.h"

#include "ArbolPrefijosEstandar.h"

using namespace std;

class ServidorVocabularioPrefijos{
private:
	
	//puntaje de terminos originales
	static const int puntaje_normal=3;
	static const int puntaje_sugerencia=1;
//	static const int max_sugerencias=3;
	
//	map<int, ArbolPrefijosSimple*> *vocabularios;
	map<int, ArbolPrefijosEstandar*> *vocabularios;
	
//	static const int max_terms=20;
//	//arreglos de punteros a char (no copian la memoria !)
//	const char **terms;
//	const char **sugerencias;
//	float *valores;
	
	void cargarVocabularios(IndiceInvertidoAtributos *indice);
	void cargarVocabularios(IndiceInvertidoAtributos *indice, int user_id);
	
	int last_update_user;
	bool last_update;
	
public:
	
	ServidorVocabularioPrefijos();
	
	ServidorVocabularioPrefijos(const char *archivo_ii);
	ServidorVocabularioPrefijos(const char *archivo_ii, bool binario);
	
	~ServidorVocabularioPrefijos();
	
	//recibe el c-string con la consulta, y escribe en el c-string de salida (supone que tiene memoria suficiente)
	int consultar(char *consulta, char *salida);
	
	void actualizarUsuario(const char *archivo_ii, bool binario, int user_id);
	
	bool lastUpdate(){return last_update;}
	int lastUpdatedUser(){return last_update_user;}
	
	
};

#endif


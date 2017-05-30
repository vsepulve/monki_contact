#if !defined(_INDICE_INVERTIDO_ATRIBUTOS_H)
#define _INDICE_INVERTIDO_ATRIBUTOS_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <fstream>
#include <list>
#include <map>

//#include "IndiceInvertido.h"
#include "ListaInvertida.h"

using namespace std;

class IndiceInvertidoAtributos{

protected:
	map< int, map<string, map<string, ListaInvertida*>* >* > *mapa_indices;
	
private:
	float combinarValores(list<float> &valores);
	//buffer de lectura
	char *linea;
	int max_linea;
	
public:

	IndiceInvertidoAtributos();
	
	virtual ~IndiceInvertidoAtributos();
	
	map< int, map<string, map<string, ListaInvertida*>* >* > *getMapa() const;
	
	virtual bool cargar(const char *archivo_indice);
	
	virtual bool cargar(const char *archivo_indice, const char *archivo_grupos);
	
	virtual bool actualizarUsuario(const char *archivo_indice, const char *archivo_grupos, int user_id);
	
	virtual map<int, int> *cargarGrupos(const char *archivo_indice);
	
	virtual bool cargarUsuario(fstream *lector, int user_id, map<int, int> *mapa_grupos);
	
//	virtual void guardarBinario(const char *archivo);
//	
//	virtual void guardarBinario(const char *archivo, int user_id);
	
	bool contieneUsuario(int user_id) const;
	
	ListaInvertida *getLista(int user_id, string *atributo, string *s);
	
	list<int> *getUsuarios() const;
	
	list<string> *getAtributosUsuario(int user_id) const;
	
};

#endif


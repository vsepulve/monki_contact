#if !defined(_INDICE_INVERTIDO_H)
#define _INDICE_INVERTIDO_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <fstream>
#include <list>
#include <map>

#include "IndiceInvertido.h"
#include "ListaInvertida.h"

using namespace std;

class IndiceInvertido{

protected:
	map< int, map<string, ListaInvertida*>* > *mapa_indices;

private:
	float combinar_valores(list<float> &valores);
	
public:

	IndiceInvertido();
	
	virtual ~IndiceInvertido();
	
	map< int, map<string, ListaInvertida*>* > *getMapa() const;
	
	virtual bool cargarBinario(const char *archivo);
	
	virtual bool cargar(const char *archivo_indice);
	
	virtual bool cargarBinario(const char *archivo, const char *archivo_grupos);
	
	virtual bool cargar(const char *archivo_indice, const char *archivo_grupos);
	
	virtual void guardarBinario(const char *archivo);
	
	virtual void guardarBinario(const char *archivo, int user_id);
	
	bool contieneUsuario(int user_id) const;
	
	ListaInvertida *getLista(int user_id, string *s);
	
	list<int> *getUsuarios() const;
	
};

#endif


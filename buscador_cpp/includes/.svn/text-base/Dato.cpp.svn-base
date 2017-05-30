
#include "Dato.h"

Dato::Dato(){
	static int new_id=0;
	id=new_id;
	new_id++;
//	cout<<"Dato["<<id<<"]\n";
}

Dato::~Dato(){
//	cout<<"Dato::~Dato["<<id<<"]\n";
}

Dato *Dato::clonar(){
	Dato *dato=new Dato();
	dato->id=id;
	return dato;
}

char *Dato::texto_print(){
	char *linea=new char[1024];
	sprintf(linea, "Dato[%d]", id);
	return linea;
}

ostream& operator<<(ostream& os, const Dato& dato){
	//agregar print
	//texto_print es polimorfico y trae el texto correcto
	//debe referenciarse con ((Dato*)&dato)->...
	char *linea=((Dato*)&dato)->texto_print();
	os<<linea;
	delete [] linea;
	//Retornar el stream
	return os;
}



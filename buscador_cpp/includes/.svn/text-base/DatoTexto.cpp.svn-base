
#include "DatoTexto.h"

DatoTexto::DatoTexto(){
	//cout<<"DatoTexto::DatoTexto\n";
	n=0;
	s=NULL;
}

DatoTexto::DatoTexto(const char *_s){
	//cout<<"DatoTexto::DatoTexto\n";
	n=strlen(_s);
	s=new char[n+1];
	strcpy(s, _s);
}

DatoTexto::~DatoTexto(){
//	cout<<"DatoTexto::~DatoTexto["<<id<<"]\n";
	if(s!=NULL){
		delete [] s;
		s=NULL;
	}
}

Dato *DatoTexto::clonar(){
	Dato *dato=new DatoTexto(s);
	dato->id=id;
	return dato;
}

char *DatoTexto::getTexto() const{
	return s;
}

int DatoTexto::largo() const{
	return n;
}

char *DatoTexto::texto_print(){
	char *linea=new char[1024];
	sprintf(linea, "DatoTexto[%d] (%s)", id, s);
	return linea;
}

ostream& operator<<(ostream& os, const DatoTexto& dato){
	//agregar print
	//texto_print es polimorfico y trae el texto correcto
	//debe referenciarse con ((Dato*)&dato)->...
	char *linea=((Dato*)&dato)->texto_print();
	os<<linea;
	delete [] linea;
	//Retornar el stream
	return os;
}


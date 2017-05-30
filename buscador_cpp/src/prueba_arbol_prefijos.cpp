
#include <stdlib.h>
#include <iostream>

#include <vector>
#include <list>

#include "ArbolPrefijosEstandar.h"

using namespace std;

int main(int argc, char* argv[]){
	
	list<string*> *vocabulario=new list<string*>();
	
	vocabulario->push_back(new string("flan"));
	vocabulario->push_back(new string("fr"));
	vocabulario->push_back(new string("fran"));
	vocabulario->push_back(new string("francisca"));
	vocabulario->push_back(new string("francisco"));
	vocabulario->push_back(new string("fraz"));
	vocabulario->push_back(new string("frei"));
	vocabulario->push_back(new string("frito"));
	vocabulario->push_back(new string("fru"));
	vocabulario->push_back(new string("frutariano"));
	vocabulario->push_back(new string("frutas"));
//	vocabulario->push_back(new string("fz"));
	
//	vocabulario->sort();
	
	ArbolPrefijosEstandar *arbol=new ArbolPrefijosEstandar(vocabulario);
	
	list<string> res;
	
	
	cout<<"\n";
	cout<<"----- Construccion Lista ----\n";
	cout<<"\n";
	
	cout<<"Buscando: \"f\"\n";
	arbol->consultar("f", &res);
	for(list<string>::iterator it=res.begin(); it!=res.end(); it++)
		cout<<"res: "<<(*it)<<"\n";
	res.clear();

	cout<<"Buscando: \"fr\"\n";
	arbol->consultar("fr", &res);
	for(list<string>::iterator it=res.begin(); it!=res.end(); it++)
		cout<<"res: "<<(*it)<<"\n";
	res.clear();

	cout<<"Buscando: \"fra\"\n";
	arbol->consultar("fra", &res);
	for(list<string>::iterator it=res.begin(); it!=res.end(); it++)
		cout<<"res: "<<(*it)<<"\n";
	res.clear();

	cout<<"Buscando: \"fran\"\n";
	arbol->consultar("fran", &res);
	for(list<string>::iterator it=res.begin(); it!=res.end(); it++)
		cout<<"res: "<<(*it)<<"\n";
	res.clear();

	cout<<"Buscando: \"franc\"\n";
	arbol->consultar("franc", &res);
	for(list<string>::iterator it=res.begin(); it!=res.end(); it++)
		cout<<"res: "<<(*it)<<"\n";
	res.clear();

	cout<<"Buscando: \"fru\"\n";
	arbol->consultar("fru", &res);
	for(list<string>::iterator it=res.begin(); it!=res.end(); it++)
		cout<<"res: "<<(*it)<<"\n";
	res.clear();
	
	
	cout<<"Buscando: \"fr\", 6\n";
	arbol->consultar("fr", &res, 6);
	for(list<string>::iterator it=res.begin(); it!=res.end(); it++)
		cout<<"set: "<<(*it)<<"\n";
	res.clear();
	
	
//	cout<<"Buscando hijos de nodo \"f\"\n";
//	NodoPrefijosSimple *nodo=arbol->buscar_nodo("f");
//	if(nodo!=NULL){
//		nodo->consultar_hijos(NULL, &res);
//	}
//	else{
//		cout<<"Nodo nulo\n";
//	}
	
	
}




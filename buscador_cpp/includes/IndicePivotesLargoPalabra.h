#if !defined(_INDICE_PIVOTES_LARGO_PALABRA_H)
#define _INDICE_PIVOTES_LARGO_PALABRA_H

#include <stdio.h>
#include <iostream>

#include <list>
#include <set>

#include "Dato.h"
#include "Distancia.h"

using namespace std;

class IndicePivotesLargoPalabra : public Indice{
private:
	
	Distancia* dist;
	int n_datos;
//	list<Dato*> *datos;
	Dato **datos;
	
	int n_pivotes;
	list<Dato*> *pivotes;
	int max_largo;
	list<int> **largos;
	int max_distancia;
	
	float **matriz;
	float *matriz_q;
	
	int evitadas;
	int realizadas;
	
public:
	void setMaxDist(int max_dist){
		max_distancia=max_dist;
	}
	
	IndicePivotesLargoPalabra(){
		dist=NULL;
//		datos=new list<Dato*>();
		datos=NULL;
		pivotes=new list<Dato*>();
		matriz=NULL;
		matriz_q=NULL;
		n_datos=0;
		n_pivotes=0;
	}
	
	IndicePivotesLargoPalabra(list<Dato*> *_datos, Distancia *_dist, int _n_pivotes){
//		cout<<"IndicePivotesLargoPalabra - inicio ("<<(_datos->size())<<" datos, "<<_n_pivotes<<" pivotes)\n";
		dist=_dist;
//		datos=new list<Dato*>();
		list<Dato*> *lista_datos=new list<Dato*>();
		pivotes=new list<Dato*>();
		n_datos=_datos->size();
		n_pivotes=_n_pivotes;
		n_datos-=n_pivotes;
		
//		cout<<"IndicePivotesLargoPalabra - preparando "<<n_datos<<" datos, "<<n_pivotes<<" pivotes\n";
		set<int> *agregados=new set<int>();
		int candidato;
		for(int i=0; i<n_pivotes; i++){
			candidato = random()%(n_datos+n_pivotes);
			while(agregados->find(candidato) != agregados->end()){
				candidato = random()%(n_datos+n_pivotes);
			}
			agregados->insert(candidato);
		}
//		cout<<"IndicePivotesLargoPalabra - "<<(agregados->size())<<" candidatos\n";
		
		list<Dato*>::iterator it;
		Dato *dato, *pivote;
		
		candidato=0;
		for(it=_datos->begin(); it!=_datos->end(); it++){
			dato=(*it);
			if(agregados->find(candidato) != agregados->end()){
				pivotes->push_back(dato->clonar());
//				cout<<"IndicePivotesLargoPalabra - nuevo pivote: "<<*(pivotes->back())<<"\n";
			}
			else{
				lista_datos->push_back(dato->clonar());
			}
			candidato++;
		}
//		cout<<"IndicePivotesLargoPalabra - "<<(lista_datos->size())<<" datos, "<<(pivotes->size())<<" pivotes listos\n";
		
		agregados->clear();
		delete agregados;
		
		matriz=new float*[n_datos];
		for(int i=0; i<n_datos; i++){
			matriz[i]=new float[n_pivotes];
		}
		matriz_q=new float[n_pivotes];
		
		list<Dato*>::iterator it_pivote;
		int i_dato, i_pivote;
		
		datos=new Dato*[lista_datos->size()];
		
		max_largo=0;
		i_dato=0;
		for(it=lista_datos->begin(); it!=lista_datos->end(); it++){
			dato=(*it);
			if( ((DatoTexto*)dato)->largo() > max_largo ){
				max_largo=((DatoTexto*)dato)->largo();
			}
			datos[i_dato]=dato;
			i_pivote=0;
			for(it_pivote=pivotes->begin(); it_pivote!=pivotes->end(); it_pivote++){
				pivote=*it_pivote;
				matriz[i_dato][i_pivote]=dist->d(*dato, *pivote);
				i_pivote++;
			}
			i_dato++;
		}
		lista_datos->clear();
		delete lista_datos;
//		cout<<"IndicePivotesLargoPalabra - matrices listas\n";
		
		//buscar la palabra mas larga
		largos=new list<int>*[max_largo+1];
		for(int i=0; i<=max_largo; i++){
			largos[i]=new list<int>();
		}
		i_dato=0;
		for(int i=0; i<n_datos; i++){
			dato=datos[i];
			largos[((DatoTexto*)dato)->largo()]->push_back(i_dato);
			i_dato++;
		}
		//la maxima distancia que se buscara (desde largo_q hasta largo_q+max_distancia)
		//este valor debe cambiarse luego de la construccion;
		max_distancia=1;
		
//		cout<<"IndicePivotesLargoPalabra - fin\n";
		
	}
	
	~IndicePivotesLargoPalabra(){
//		cout<<"IndicePivotesLargoPalabra::~IndicePivotesLargoPalabra - inicio\n";
		if(datos!=NULL){
			for(int i=0; i<n_datos; i++){
				delete datos[i];
			}
			delete [] datos;
		}
		if(matriz!=NULL){
			for(int i=0; i<n_datos; i++){
				delete [] matriz[i];
			}
			delete [] matriz;
		}
		if(matriz_q!=NULL){
			delete [] matriz_q;
		}
		if(largos!=NULL){
			for(int i=0; i<=max_largo; i++){
				largos[i]->clear();
				delete largos[i];
			}
			delete [] largos;
		}
//		cout<<"IndicePivotesLargoPalabra::~IndicePivotesLargoPalabra - fin\n";
	}
	
	int size(){
		return n_datos + n_pivotes;
	}
	
	int rango(Dato *q, float r, list<pair<Dato*, float> > *res){
//		cout<<"IndicePivotesLargoPalabra::rango - inicio ("<<(*q)<<", "<<r<<")\n";
		int agregados=0;
		evitadas=0;
		realizadas=0;
		
		Dato *dato, *pivote;
		float d;
		bool descartado;
		
		list<Dato*>::iterator it, it_pivote;
		
//		cout<<"IndicePivotesLargoPalabra::rango - preparando matriz de consulta\n";
		it_pivote=pivotes->begin();
		for(int i=0; i<n_pivotes; i++){
			pivote=(*it_pivote);
			matriz_q[i]=dist->d(*q, *pivote);
			realizadas++;
			if(matriz_q[i] <= r){
				res->push_back(pair<Dato*, float>(pivote, matriz_q[i]));
				agregados++;
			}
			it_pivote++;
		}
		
//		cout<<"IndicePivotesLargoPalabra::rango - matriz lista ("<<agregados<<" agregados)\n";
		
		int largo_q=((DatoTexto*)q)->largo();
		list<int>::iterator it_largos;
		int i_dato;
		for(int largo=largo_q; largo<=largo_q+max_distancia && largo<=max_largo; largo++){
			for(it_largos=largos[largo]->begin(); it_largos!=largos[largo]->end(); it_largos++){
				i_dato=*it_largos;
				dato=datos[i_dato];
				it_pivote=pivotes->begin();
				descartado=false;
				for(int j=0; j<n_pivotes; j++){
					d=matriz_q[j]-matriz[i_dato][j];
					if(d<0)
						d=-d;
					if(d>r){
						evitadas++;
						descartado=true;
						break;
					}
					it_pivote++;
				}
				if(!descartado){
					//si no lo puedo descartar, calculo su distancia
					d=dist->d(*q, *dato);
					realizadas++;
					if(d<=r){
						res->push_back(pair<Dato*, float>(dato, d));
						agregados++;
					}
				}
				
			}
			
			
		}
		
//		cout<<"IndicePivotesLargoPalabra::rango - fin\n";
		
		return agregados;
	}
	
	
};


#endif



#include "IndiceLineal.h"

IndiceLineal::IndiceLineal(){
	dist=NULL;
	n_datos=0;
	datos=new list<Dato*>();
}

IndiceLineal::IndiceLineal(list<Dato*> *_datos, Distancia *_dist){
//	cout<<"IndiceLineal - inicio\n";
	dist=_dist;
	n_datos=_datos->size();
	datos=new list<Dato*>();
	for(list<Dato*>::iterator it=datos->begin(); it!=_datos->end(); it++){
		datos->push_back((*it)->clonar());
	}
//	cout<<"IndiceLineal - fin\n";
}

IndiceLineal::~IndiceLineal(){
//	cout<<"IndiceLineal::~IndiceLineal - inicio\n";
	if(datos!=NULL){
		for(list<Dato*>::iterator it=datos->begin(); it!=datos->end(); it++){
			delete (*it);
		}
		datos->clear();
		delete datos;
		datos=NULL;
	}
//	cout<<"IndiceLineal::~IndiceLineal - fin\n";
}

int IndiceLineal::size(){
	return n_datos;
}

int IndiceLineal::rango(Dato *q, double r, list< pair<Dato*, double> > *res){
//	cout<<"IndiceLineal::rango - inicio ("<<*q<<", "<<r<<")\n";
	int agregados=0;
	
	list<Dato*>::iterator it;
	Dato *dato;
	double d;
	for(it=datos->begin(); it!=datos->end(); it++){
		dato=*it;
		d=dist->d(*q, *dato);
		if(d<=r){
			cout<<"IndiceLineal::rango - "<<*dato<<" agregado por "<<d<<"\n";
			res->push_back(pair<Dato*, double>(dato, d));
			agregados++;
		}
	}
	
//	cout<<"IndiceLineal::rango - fin ("<<agregados<<")\n";
	return agregados;
}

int IndiceLineal::rango_minimo(Dato *q, list< pair<Dato*, double> > *res){
//	cout<<"IndiceLineal::rango - inicio ("<<*q<<", "<<r<<")\n";
	double r=DBL_MAX;
	int agregados=0;
	
	set< pair<double, Dato*> > *candidatos=new set< pair<double, Dato*> >();
	
	list<Dato*>::iterator it;
	Dato *dato;
	double d;
	for(it=datos->begin(); it!=datos->end(); it++){
		dato=*it;
		d=dist->d(*q, *dato);
		if(d<=r){
			cout<<"IndiceLineal::rango - "<<*dato<<" agregado por "<<d<<"\n";
//			res->push_back(pair<Dato*, double>(dato, d));
			candidatos->insert(pair<double, Dato*>(d, dato));
//			agregados++;
			r=d;
		}
	}
	
	set< pair<double, Dato*> >::iterator it_res;
	
	it_res=candidatos->begin();
	d=it_res->first;
	dato=it_res->second;
	
	res->push_back(pair<Dato*, double>(dato, d));
	agregados++;
	
	it_res++;
	
	for(; it_res!=candidatos->end() && it_res->first==d; it_res++){
		d=it_res->first;
		dato=it_res->second;
		
		res->push_back(pair<Dato*, double>(dato, d));
		agregados++;
		
	}
	
	candidatos->clear();
	delete candidatos;
	
//	cout<<"IndiceLineal::rango - fin ("<<agregados<<")\n";
	return agregados;
}





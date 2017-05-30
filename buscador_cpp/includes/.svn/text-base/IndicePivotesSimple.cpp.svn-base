
#include "IndicePivotesSimple.h"

IndicePivotesSimple::IndicePivotesSimple(){
	dist=NULL;
	datos=new list<Dato*>();
	pivotes=new list<Dato*>();
	matriz=NULL;
	matriz_q=NULL;
	n_datos=0;
	n_pivotes=0;
}

IndicePivotesSimple::IndicePivotesSimple(list<Dato*> *_datos, Distancia *_dist, int _n_pivotes){
//	cout<<"IndicePivotesSimple - inicio ("<<(_datos->size())<<" datos, "<<_n_pivotes<<" pivotes)\n";
	dist=_dist;
	datos=new list<Dato*>();
	pivotes=new list<Dato*>();
	n_datos=_datos->size();
	n_pivotes=_n_pivotes;
	n_datos-=n_pivotes;
	
//	cout<<"IndicePivotesSimple - preparando "<<n_datos<<" datos, "<<n_pivotes<<" pivotes\n";
	set<int> *agregados=new set<int>();
	int candidato;
	for(int i=0; i<n_pivotes; i++){
		candidato = rand()%(n_datos+n_pivotes);
		while(agregados->find(candidato) != agregados->end()){
			candidato = rand()%(n_datos+n_pivotes);
		}
		agregados->insert(candidato);
	}
//	cout<<"IndicePivotesSimple - "<<(agregados->size())<<" candidatos\n";
	
	list<Dato*>::iterator it;
	Dato *dato, *pivote;
	
	candidato=0;
	for(it=_datos->begin(); it!=_datos->end(); it++){
		dato=(*it);
		if(agregados->find(candidato) != agregados->end()){
			pivotes->push_back(dato->clonar());
//			cout<<"IndicePivotesSimple - nuevo pivote: "<<*(pivotes->back())<<"\n";
		}
		else{
			datos->push_back(dato->clonar());
		}
		candidato++;
	}
//	cout<<"IndicePivotesSimple - "<<(datos->size())<<" datos, "<<(pivotes->size())<<" pivotes listos\n";
	
	agregados->clear();
	delete agregados;
	
	matriz=new float*[n_datos];
	for(int i=0; i<n_datos; i++){
		matriz[i]=new float[n_pivotes];
	}
	matriz_q=new float[n_pivotes];
	
	list<Dato*>::iterator it_pivote;
	int i_dato, i_pivote;
	
	i_dato=0;
	for(it=datos->begin(); it!=datos->end(); it++){
		dato=(*it);
		i_pivote=0;
		for(it_pivote=pivotes->begin(); it_pivote!=pivotes->end(); it_pivote++){
			pivote=*it_pivote;
			matriz[i_dato][i_pivote]=dist->d(*dato, *pivote);
			i_pivote++;
		}
		i_dato++;
	}
//	cout<<"IndicePivotesSimple - matrices listas\n";
	
//	cout<<"IndicePivotesSimple - fin\n";
	
}

IndicePivotesSimple::~IndicePivotesSimple(){
//	cout<<"IndicePivotesSimple::~IndicePivotesSimple - inicio\n";
	if(datos!=NULL){
		list<Dato*>::iterator it;
		for(it=datos->begin(); it!=datos->end(); it++){
			delete (*it);
		}
		datos->clear();
		delete datos;
		datos=NULL;
	}
	if(pivotes!=NULL){
		list<Dato*>::iterator it;
		for(it=pivotes->begin(); it!=pivotes->end(); it++){
			delete (*it);
		}
		pivotes->clear();
		delete pivotes;
		pivotes=NULL;
	}
//	cout<<"IndicePivotesSimple::~IndicePivotesSimple - fin\n";
}

int IndicePivotesSimple::size(){
	return n_datos + n_pivotes;
}

int IndicePivotesSimple::rango(Dato *q, float r, list<pair<Dato*, float> > *res){
//	cout<<"IndicePivotesSimple::rango - inicio ("<<(*q)<<", "<<r<<")\n";
	int agregados=0;
	evitadas=0;
	realizadas=0;
	
	Dato *dato, *pivote;
	float d;
	bool descartado;
	
	list<Dato*>::iterator it, it_pivote;
	
//	cout<<"IndicePivotesSimple::rango - preparando matriz de consulta\n";
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
	
//	cout<<"IndicePivotesSimple::rango - matriz lista ("<<agregados<<" agregados)\n";
	
	it=datos->begin();
	for(int i=0; i<n_datos; i++){
		dato=(*it);
		it_pivote=pivotes->begin();
		descartado=false;
		for(int j=0; j<n_pivotes; j++){
			d=matriz_q[j]-matriz[i][j];
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
		it++;
	}
	
//	cout<<"IndicePivotesSimple::rango - fin\n";
	
	return agregados;
}

int IndicePivotesSimple::rango_minimo(Dato *q, list<pair<Dato*, float> > *res){
//	cout<<"IndicePivotesSimple::rango_minimo - inicio ("<<(*q)<<")\n";
	float r=FLT_MAX;
	int agregados=0;
	evitadas=0;
	realizadas=0;
	
	Dato *dato, *pivote;
	float d;
	bool descartado;
	
	set< pair<float, Dato*> > *candidatos=new set< pair<float, Dato*> >();
	
	list<Dato*>::iterator it, it_pivote;
	
//	cout<<"IndicePivotesSimple::rango_minimo - preparando matriz de consulta\n";
	it_pivote=pivotes->begin();
	for(int i=0; i<n_pivotes; i++){
		pivote=(*it_pivote);
		matriz_q[i]=dist->d(*q, *pivote);
		realizadas++;
		if(matriz_q[i] <= r){
//			res->push_back(pair<Dato*, float>(pivote, matriz_q[i]));
			candidatos->insert(pair<float, Dato*>(matriz_q[i], pivote));
//			agregados++;
			r=matriz_q[i];
		}
		it_pivote++;
	}
	
//	cout<<"IndicePivotesSimple::rango_minimo - matriz lista ("<<(candidatos->size())<<" candidatos, radio "<<r<<")\n";
	
	it=datos->begin();
	for(int i=0; i<n_datos; i++){
		dato=(*it);
		it_pivote=pivotes->begin();
		descartado=false;
		for(int j=0; j<n_pivotes; j++){
			d=matriz_q[j]-matriz[i][j];
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
//				res->push_back(pair<Dato*, float>(dato, d));
				candidatos->insert(pair<float, Dato*>(d, dato));
//				agregados++;
				r=d;
			}
		}
		it++;
	}
//	cout<<"IndicePivotesSimple::rango_minimo - candidatos listos ("<<(candidatos->size())<<")\n";
	
	set< pair<float, Dato*> >::iterator it_res;
	
	it_res=candidatos->begin();
	d=it_res->first;
	dato=it_res->second;
	
//	cout<<"IndicePivotesSimple::rango_minimo - agregando ("<<(*dato)<<", "<<d<<")\n";
	res->push_back(pair<Dato*, float>(dato, d));
	agregados++;
	
	it_res++;
	
	for(; it_res!=candidatos->end() && it_res->first==d; it_res++){
		d=it_res->first;
		dato=it_res->second;
		
//		cout<<"IndicePivotesSimple::rango_minimo - agregando ("<<(*dato)<<", "<<d<<")\n";
		res->push_back(pair<Dato*, float>(dato, d));
		agregados++;
		
	}
	
	candidatos->clear();
	delete candidatos;
	
//	cout<<"IndicePivotesSimple::rango_minimo - fin ("<<agregados<<")\n";
	
	return agregados;
}


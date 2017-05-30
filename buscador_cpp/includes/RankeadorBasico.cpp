
#include "RankeadorBasico.h"

RankeadorBasico::RankeadorBasico(){
//	cout<<"RankeadorBasico - inicio\n";
	
	indice=NULL;
	listas=new list<ListaInvertida*>();
	pesos=new list<float>();
	comp=ComparadorVal();

//	cout<<"RankeadorBasico - fin\n";
}

RankeadorBasico::RankeadorBasico(IndiceInvertidoAtributos *_indice){
//	cout<<"RankeadorBasico - inicio\n";
	
	indice=_indice;
	listas=new list<ListaInvertida*>();
	pesos=new list<float>();
	comp=ComparadorVal();

//	cout<<"RankeadorBasico - fin\n";
}

RankeadorBasico::~RankeadorBasico(){
	cout<<"RankeadorBasico::~RankeadorBasico - inicio\n";
	if(indice!=NULL){
		delete indice;
		indice=NULL;
	}
	if(listas!=NULL){
		//las listas son solo referencias al indice
		listas->clear();
		delete listas;
		listas=NULL;
	}
	if(pesos!=NULL){
		pesos->clear();
		delete pesos;
		pesos=NULL;
	}
}

IndiceInvertidoAtributos *RankeadorBasico::getIndice(){
	return indice;
}

list< pair<int, float> > *RankeadorBasico::consultarAnd(int user_id, list< pair<string, float> > *lista_terms, list<string> *atributos){
	//cout<<"RankeadorBasico::consultarAnd - inicio (user_id: "<<user_id<<", "<<lista_terms->size()<<" terms)\n";
	
//	list<string>::iterator it_terms;
//	list<float>::iterator it_valores;
	list< pair<string, float> >::iterator it_terms;
	
//	cout<<"RankeadorBasico::consultarAnd - inicio (user_id: "<<user_id<<", [";
//	for(it_terms=lista_terms->begin(); it_terms!=lista_terms->end(); it_terms++){
//		cout<<(it_terms->first)<<" ";
//	}
//	cout<<"])\n";
	
	//El ranking combina el puntaje total de un documento
	//y el numero de terminos de consulta en ese documento
	//mapa de valores (suma de puntajes) por cada doc_id
	map<int, float> resultados;
	map<int, float>::iterator it_res;
	//mapa del numero de terminos de consulta que aparecen en cada doc
	map<int, int> terminos;
	map<int, int>::iterator it_terminos;
	
	list<ListaInvertida*>::iterator it;
	list<float>::iterator it_pesos;
	ListaInvertida *lista;
	
	int doc_id;
	float valor, valor_old;
	
	//lista de listas para el merge de listas (por atributos)
	//list<list< pair<int, float> >* > listas_temp;
	list<ListaInvertida*> listas_temp;
	list<string>::iterator it_atributos;
	
	for(it_terms=lista_terms->begin(); it_terms!=lista_terms->end(); it_terms++){
		
		bool all=false;
		for(it_atributos=atributos->begin(); it_atributos!=atributos->end(); it_atributos++){
			if(it_atributos->compare("all")==0){
				all=true;
				break;
			}
		}
		
		if(all){
			list<string> *atributos_todos=indice->getAtributosUsuario(user_id);
			for(it_atributos=atributos_todos->begin(); it_atributos!=atributos_todos->end(); it_atributos++){
				lista=indice->getLista(user_id, &(*it_atributos), &(it_terms->first));
				if(lista==NULL){
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<it_terms->first<<" no encontrada\n";
				}
				else{
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<it_terms->first<<" con "<<(lista->size())<<" candidatos\n";
					listas_temp.push_back(lista);
				}
			}//for... cada atributo
			atributos_todos->clear();
			delete atributos_todos;
		}
		else{
			for(it_atributos=atributos->begin(); it_atributos!=atributos->end(); it_atributos++){
				lista=indice->getLista(user_id, &(*it_atributos), &(it_terms->first));
				if(lista==NULL){
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<it_terms->first<<" no encontrada\n";
				}
				else{
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<it_terms->first<<" con "<<(lista->size())<<" candidatos\n";
					listas_temp.push_back(lista);
				}
			}//for... cada atributo
		}
		
		pesos->push_back(it_terms->second);
		if(listas_temp.size()>0){
			listas->push_back(new ListaInvertida(&listas_temp));
		}
		listas_temp.clear();
		
	}//for... cada termino
	
	it_pesos=pesos->begin();
	for(it=listas->begin(); it!=listas->end(); it++){
		lista=*it;
		
		if(lista->size() > 0){
		
			lista->reset();
			//cout<<"RankeadorBasico::consultarAnd - Agregando lista con "<<(lista->size())<<" candidatos\n";
		
			//tomar el doc inicial
			doc_id=lista->getDocId();
			valor=(*it_pesos)*(lista->getValor());
			//cout<<"RankeadorBasico::consultarAnd - Agregando ("<<doc_id<<", "<<valor<<")\n";
			if(resultados.find(doc_id)!=resultados.end()){
				valor_old=resultados[doc_id];
				resultados[doc_id]=valor + valor_old;
				terminos[doc_id]++;
			}
			else{
				resultados[doc_id]=valor;
				terminos[doc_id]=1;
			}
		
			//tomar el resto de los docs
			while(lista->hasNext()){
				lista->next();
				doc_id=lista->getDocId();
				valor=(*it_pesos)*(lista->getValor());
				//cout<<"RankeadorBasico::consultarAnd - Agregando ("<<doc_id<<", "<<valor<<")\n";
				if(resultados.find(doc_id)!=resultados.end()){
					valor_old=resultados[doc_id];
					resultados[doc_id]=valor + valor_old;
					terminos[doc_id]++;
				}
				else{
					resultados[doc_id]=valor;
					terminos[doc_id]=1;
				}
			}
		}
		
		it_pesos++;
	}
	
	list< pair<int, float> > *list_res=new list< pair<int, float> >();
	for(it_res=resultados.begin(); it_res!=resultados.end(); it_res++){
		//solo si el doc tiene todos los terminos
		//esta es la PEOR FORMA de hacer un and !!! (luego debe ser cambiada por un algoritmo de verdad)
		if((unsigned int)(terminos[it_res->first])==lista_terms->size()){
			list_res->push_back(pair<int, float>(it_res->first, it_res->second));
		}
	}
	
	//el comparador considera las apariciones y los puntajes totales
	comp.apariciones=&terminos;
	list_res->sort(comp);
	
//	list< pair<int, float> >::iterator it_list_res;
//	for(it_list_res=list_res->begin(); it_list_res!=list_res->end(); it_list_res++){
//		cout<<"doc "<<(it_list_res->first)<<" con "<<(it_list_res->second)<<" ("<<terminos[(it_list_res->first)]<<" apariciones)\n";
//	}
	
	//cout<<"RankeadorBasico.consultarAnd - fin ("<<list_res->size()<<" resultados)\n";
	
	resultados.clear();
	terminos.clear();
	
	pesos->clear();
	
	for(it=listas->begin(); it!=listas->end(); it++){
		delete *it;
	}
	listas->clear();
	
	return list_res;
}

list< pair<int, float> > *RankeadorBasico::consultar(int user_id, list< pair<string, float> > *lista_terms, list<string> *atributos){
//	cout<<"RankeadorBasico::consultar - inicio (user_id: "<<user_id<<", "<<lista_terms->size()<<" terms)\n";
	
//	list<string>::iterator it_terms;
//	list<float>::iterator it_valores;
	list< pair<string, float> >::iterator it_terms;
	
	//El ranking combina el puntaje total de un documento
	//y el numero de terminos de consulta en ese documento
	//mapa de valores (suma de puntajes) por cada doc_id
	map<int, float> resultados;
	map<int, float>::iterator it_res;
	//mapa del numero de terminos de consulta que aparecen en cada doc
	map<int, int> terminos;
	map<int, int>::iterator it_terminos;
	
	list<ListaInvertida*>::iterator it;
	list<float>::iterator it_pesos;
	ListaInvertida *lista;
	
	int doc_id;
	float valor, valor_old;
	
	//lista de listas para el merge de listas (por atributos)
//	list<list< pair<int, float> >* > listas_temp;
	list<ListaInvertida*> listas_temp;
	list<string>::iterator it_atributos;
	
	for(it_terms=lista_terms->begin(); it_terms!=lista_terms->end(); it_terms++){
		
		bool all=false;
		for(it_atributos=atributos->begin(); it_atributos!=atributos->end(); it_atributos++){
			if(it_atributos->compare("all")==0){
				all=true;
				break;
			}
		}
		
		if(all){
			list<string> *atributos_todos=indice->getAtributosUsuario(user_id);
			for(it_atributos=atributos_todos->begin(); it_atributos!=atributos_todos->end(); it_atributos++){
				lista=indice->getLista(user_id, &(*it_atributos), &(it_terms->first));
				if(lista==NULL){
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<it_terms->first<<" no encontrada\n";
				}
				else{
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<it_terms->first<<" con "<<(lista->size())<<" candidatos\n";
					listas_temp.push_back(lista);
				}
			}//for... cada atributo
			atributos_todos->clear();
			delete atributos_todos;
		}
		else{
			for(it_atributos=atributos->begin(); it_atributos!=atributos->end(); it_atributos++){
				lista=indice->getLista(user_id, &(*it_atributos), &(it_terms->first));
				if(lista==NULL){
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<(*it_atributos)<<" - "<<it_terms->first<<" no encontrada\n";
				}
				else{
					//cout<<"RankeadorBasico::consultarAnd - Lista "<<(*it_atributos)<<" - "<<it_terms->first<<" con "<<(lista->size())<<" candidatos\n";
					listas_temp.push_back(lista);
				}
			}//for... cada atributo
		}
		
		pesos->push_back(it_terms->second);
		if(listas_temp.size()>0){
			listas->push_back(new ListaInvertida(&listas_temp));
		}
		listas_temp.clear();
		
	}//for... cada termino
	
	it_pesos=pesos->begin();
	for(it=listas->begin(); it!=listas->end(); it++){
		lista=*it;
		lista->reset();
//		cout<<"Agregando lista con "<<(lista->size())<<" candidatos\n";
		
		//tomar el doc inicial
		doc_id=lista->getDocId();
		valor=(*it_pesos)*(lista->getValor());
//		cout<<"agregando ("<<doc_id<<", "<<valor<<")\n";
		if(resultados.find(doc_id)!=resultados.end()){
			valor_old=resultados[doc_id];
			resultados[doc_id]=valor + valor_old;
			terminos[doc_id]++;
		}
		else{
			resultados[doc_id]=valor;
			terminos[doc_id]=1;
		}
		
		//tomar el resto de los docs
		while(lista->hasNext()){
			lista->next();
			doc_id=lista->getDocId();
			valor=(*it_pesos)*(lista->getValor());
//			cout<<"agregando ("<<doc_id<<", "<<valor<<")\n";
			if(resultados.find(doc_id)!=resultados.end()){
				valor_old=resultados[doc_id];
				resultados[doc_id]=valor + valor_old;
				terminos[doc_id]++;
			}
			else{
				resultados[doc_id]=valor;
				terminos[doc_id]=1;
			}
		}
		
		it_pesos++;
	}
	
	list< pair<int, float> > *list_res=new list< pair<int, float> >();
	for(it_res=resultados.begin(); it_res!=resultados.end(); it_res++){
		//list_res->push_back(pair<int, float>(it_res->first, it_res->second));
		//Por ahora, modificare las escalas de puntaje para asegurar (en la practica) que se ordenen
		//por numero de terminos, incluso entre multiples sets de resultados
		list_res->push_back(pair<int, float>(it_res->first, it_res->second + 1000*terminos[it_res->first]));
	}
	
	//el comparador considera las apariciones y los puntajes totales
	comp.apariciones=&terminos;
	list_res->sort(comp);
	
//	list< pair<int, float> >::iterator it_list_res;
//	for(it_list_res=list_res->begin(); it_list_res!=list_res->end(); it_list_res++){
//		cout<<"doc "<<(it_list_res->first)<<" con "<<(it_list_res->second)<<" ("<<terminos[(it_list_res->first)]<<" apariciones)\n";
//	}
	
//	cout<<"RankeadorBasico.consultar - fin ("<<list_res->size()<<" resultados)\n";
	
	resultados.clear();
	terminos.clear();
	
	pesos->clear();
	
	for(it=listas->begin(); it!=listas->end(); it++){
		delete *it;
	}
	listas->clear();
	
	return list_res;
	
}

//list< pair<int, float> > *RankeadorBasico::consultar(int user_id, char **terms, char **valores, int n_terms){
////	cout<<"RankeadorBasico::consultar - inicio (user_id: "<<user_id<<", "<<terms<<", "<<valores<<", "<<n_terms<<" terms)\n";
//	
//	//El ranking combina el puntaje total de un documento
//	//y el numero de terminos de consulta en ese documento
//	//mapa de valores (suma de puntajes) por cada doc_id
//	map<int, float> resultados;
//	map<int, float>::iterator it_res;
//	//mapa del numero de terminos de consulta que aparecen en cada doc
//	map<int, int> terminos;
//	map<int, int>::iterator it_terminos;
//	
//	list<ListaInvertida*>::iterator it;
//	list<float>::iterator it_pesos;
//	ListaInvertida *lista;
//	
//	int doc_id;
//	float valor, valor_old;
//	string *s;
//	
//	for(int i=0; i<n_terms; i++){
//		s=new string(terms[i]);
//		lista=indice->getLista(user_id, s);
//		if(lista==NULL){
////			cout<<"Lista "<<*s<<" no encontrada\n";
//		}
//		else{
////			cout<<"Lista "<<*s<<" con "<<(lista->size())<<" candidatos\n";
//			listas->push_back(lista);
//			pesos->push_back(atof(valores[i]));
//		}
//		s->clear();
//		delete s;
//	}
//	
//	it_pesos=pesos->begin();
//	for(it=listas->begin(); it!=listas->end(); it++){
//		lista=*it;
//		lista->reset();
////		cout<<"Agregando lista con "<<(lista->size())<<" candidatos\n";
//		
//		//tomar el doc inicial
//		doc_id=lista->getDocId();
//		valor=(*it_pesos)*(lista->getValor());
////		cout<<"agregando ("<<doc_id<<", "<<valor<<")\n";
//		if(resultados.find(doc_id)!=resultados.end()){
//			valor_old=resultados[doc_id];
//			resultados[doc_id]=valor + valor_old;
//			terminos[doc_id]++;
//		}
//		else{
//			resultados[doc_id]=valor;
//			terminos[doc_id]=1;
//		}
//		
//		//tomar el resto de los docs
//		while(lista->hasNext()){
//			lista->next();
//			doc_id=lista->getDocId();
//			valor=(*it_pesos)*(lista->getValor());
////			cout<<"agregando ("<<doc_id<<", "<<valor<<")\n";
//			if(resultados.find(doc_id)!=resultados.end()){
//				valor_old=resultados[doc_id];
//				resultados[doc_id]=valor + valor_old;
//				terminos[doc_id]++;
//			}
//			else{
//				resultados[doc_id]=valor;
//				terminos[doc_id]=1;
//			}
//		}
//		
//		it_pesos++;
//	}
//	
//	list< pair<int, float> > *list_res=new list< pair<int, float> >();
//	for(it_res=resultados.begin(); it_res!=resultados.end(); it_res++){
//		list_res->push_back(pair<int, float>(it_res->first, it_res->second));
//	}
//	
//	//el comparador considera las apariciones y los puntajes totales
//	comp.apariciones=&terminos;
//	list_res->sort(comp);
//	
////	list< pair<int, float> >::iterator it_list_res;
////	for(it_list_res=list_res->begin(); it_list_res!=list_res->end(); it_list_res++){
////		cout<<"doc "<<(it_list_res->first)<<" con "<<(it_list_res->second)<<" ("<<terminos[(it_list_res->first)]<<" apariciones)\n";
////	}
//	
////	cout<<"RankeadorBasico.consultar - fin ("<<list_res->size()<<" resultados)\n";
//	
//	resultados.clear();
//	terminos.clear();
//	
//	pesos->clear();
//	listas->clear();
//	
//	return list_res;
//	
//}


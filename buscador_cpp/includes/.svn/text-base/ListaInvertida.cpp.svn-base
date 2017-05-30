
#include "ListaInvertida.h"

ListaInvertida::ListaInvertida(){
	n_docs=0;
	docs=NULL;
	vals=NULL;
	cur_index=0;
}

ListaInvertida::ListaInvertida(list< pair<int, float> > *lista){
	n_docs=lista->size();
	docs=new int[n_docs];
	vals=new float[n_docs];
	list< pair<int, float> >::iterator it;
	
	cur_index=0;
	for(it=lista->begin(); it!=lista->end(); it++){
		docs[cur_index]=it->first;
		vals[cur_index]=it->second;
		cur_index++;
	}
	
	cur_index=0;
}

//constructor que hace merge de las listas (guarda el mayor valor por doc)
ListaInvertida::ListaInvertida(list<ListaInvertida*> *listas){
	//cout<<"ListaInvertida - inicio merge ("<<listas->size()<<" listas)\n";
	
	list<ListaInvertida*>::iterator it_listas;
	int total_elementos=0;
	for(it_listas=listas->begin(); it_listas!=listas->end(); it_listas++){
		total_elementos+=(*it_listas)->size();
	}
	if(total_elementos==0){
//		cout<<"ListaInvertida - listas vacias\n";
		n_docs=0;
		docs=NULL;
		vals=NULL;
		cur_index=0;
	}
	else{
//		cout<<"ListaInvertida - merge de "<<total_elementos<<" en total\n";
	
		list< pair<int, float> > lista_temp;
	
		ListaInvertida *lista;
		list< pair<int, float> >::iterator it;
	
		for(it_listas=listas->begin(); it_listas!=listas->end(); it_listas++){
			lista=*it_listas;
			
//			cout<<"ListaInvertida - lista con "<<lista->size()<<" elementos\n";
			
			lista->reset();
//			cout<<"ListaInvertida - ("<<lista->getDocId()<<", "<<lista->getValor()<<")\n";
			lista_temp.push_back(pair<int, float>(lista->getDocId(), lista->getValor()));
			while(lista->hasNext()){
				lista->next();
//				cout<<"ListaInvertida - ("<<lista->getDocId()<<", "<<lista->getValor()<<")\n";
				lista_temp.push_back(pair<int, float>(lista->getDocId(), lista->getValor()));
			}
		
		}
		//ordeno por el orden natural (el primer elemento del par, doc_id)
		lista_temp.sort();
	
		list< pair<int, float> > lista_final;
		int doc_id;
		float valor;
	
		it=lista_temp.begin();
		doc_id=it->first;
		valor=it->second;
		it++;
		for( ; it!=lista_temp.end(); it++){
			if(it->first!=doc_id){
				lista_final.push_back(pair<int, float>(doc_id, valor));
				doc_id=it->first;
				valor=it->second;
			}
			if(it->second > valor){
				valor=it->second;
			}
		}
		lista_final.push_back(pair<int, float>(doc_id, valor));
	
		n_docs=lista_final.size();
		docs=new int[n_docs];
		vals=new float[n_docs];
	
		cur_index=0;
		for(it=lista_final.begin(); it!=lista_final.end(); it++){
			docs[cur_index]=it->first;
			vals[cur_index]=it->second;
			cur_index++;
		}
	
		cur_index=0;
	}
	//cout<<"ListaInvertida - fin\n";
}

//constructor que hace merge de las listas (guarda el mayor valor por doc)
ListaInvertida::ListaInvertida(list<list< pair<int, float> >* > *listas){
	
	list< pair<int, float> > lista_temp;
	
	list<list< pair<int, float> >* >::iterator it_listas;
	list< pair<int, float> > *lista;
	list< pair<int, float> >::iterator it;
	
	for(it_listas=listas->begin(); it_listas!=listas->end(); it_listas++){
		lista=*it_listas;
		for(it=lista->begin(); it!=lista->end(); it++){
			lista_temp.push_back(*it);
		}
	}
	//ordeno por el orden natural (el primer elemento del par, doc_id)
	lista_temp.sort();
	
	list< pair<int, float> > lista_final;
	int doc_id;
	float valor;
	
	it=lista_temp.begin();
	doc_id=it->first;
	valor=it->second;
	for( ; it!=lista_temp.end(); it++){
		if(it->first!=doc_id){
			lista_final.push_back(pair<int, float>(doc_id, valor));
			doc_id=it->first;
			valor=it->second;
		}
		if(it->second > valor){
			valor=it->second;
		}
	}
	
	n_docs=lista_final.size();
	docs=new int[n_docs];
	vals=new float[n_docs];
	
	cur_index=0;
	for(it=lista_final.begin(); it!=lista_final.end(); it++){
		docs[cur_index]=it->first;
		vals[cur_index]=it->second;
		cur_index++;
	}
	
	cur_index=0;
}

ListaInvertida::~ListaInvertida(){
	if(docs!=NULL){
		delete [] docs;
		docs=NULL;
	}
	if(vals!=NULL){
		delete [] vals;
		vals=NULL;
	}
}

void ListaInvertida::reset(){
	cur_index=0;
}

int ListaInvertida::size(){
	return n_docs;
}

bool ListaInvertida::hasNext(){
	return cur_index<(n_docs-1);
}

void ListaInvertida::next(){
	if(hasNext()){
		cur_index++;
	}
}

int ListaInvertida::getDocId(){
	return docs[cur_index];
}

float ListaInvertida::getValor(){
	return vals[cur_index];
}


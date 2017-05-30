
#include "IndiceInvertido.h"

IndiceInvertido::IndiceInvertido(){
//	cout<<"IndiceInvertido - inicio\n";
	mapa_indices=new map< int, map<string, ListaInvertida*> *>();
//	cout<<"IndiceInvertido - fin\n";
}

IndiceInvertido::~IndiceInvertido(){
	//cout<<"IndiceInvertido::~IndiceInvertido - inicio\n";
	if(mapa_indices!=NULL){
		map< int, map<string, ListaInvertida*>* >::iterator it;
		map<string, ListaInvertida*> *indice;
		map<string, ListaInvertida*>::iterator it2;
		
		for(it=mapa_indices->begin(); it!=mapa_indices->end(); it++){
			indice=it->second;
			
			for(it2=indice->begin(); it2!=indice->end(); it2++){
				delete it2->second;
			}
			indice->clear();
			delete indice;
		}
		
		mapa_indices->clear();
		delete mapa_indices;
		mapa_indices=NULL;
	}
	//cout<<"IndiceInvertido::~IndiceInvertido - fin\n";
}

map< int, map<string, ListaInvertida*>* > *IndiceInvertido::getMapa() const{
	return mapa_indices;
}

bool IndiceInvertido::contieneUsuario(int user_id) const{
	return mapa_indices->find(user_id) != mapa_indices->end();
}

list<int> *IndiceInvertido::getUsuarios() const{
	//return mapa_indices->find(user_id) != mapa_indices->end();
	list<int> *lista_usuarios=new list<int>();
	map< int, map<string, ListaInvertida*>* >::iterator it;
	for(it=mapa_indices->begin(); it!=mapa_indices->end(); it++){
		lista_usuarios->push_back(it->first);
	}
	return lista_usuarios;
}

bool IndiceInvertido::cargar(const char *archivo_indice){

//	cout<<"IndiceInvertido::cargar - inicio (desde "<<archivo_indice<<")\n";
	
	map<string, ListaInvertida*> *mapa_terms;
	
	int user_id, doc_id;
	int n_users, n_terms, n_docs;
	float valor;
	ListaInvertida *lista;
	list< pair<int, float> > *lista_original=new list< pair<int, float> >();
	
	fstream *lector=new fstream(archivo_indice, fstream::in);
	int max_linea=1024*1024;
	char *linea=new char[max_linea];
	char *tok;
	
	lector->getline(linea, max_linea);
//	cout<<"IndiceInvertido::cargar - n_users (desde "<<linea<<")\n";
	n_users=atoi(linea);
	
	for(int i=0; i<n_users; i++){
		
		//user id
		lector->getline(linea, max_linea);
//		cout<<"IndiceInvertido::cargar - user_id (desde "<<linea<<")\n";
		user_id=atoi(linea);
		
		//numero terms
		lector->getline(linea, max_linea);
//		cout<<"IndiceInvertido::cargar - n_terms (desde "<<linea<<")\n";
		n_terms=atoi(linea);
		
		mapa_terms=new map<string, ListaInvertida*>();
		
		for(int j=0; j<n_terms; j++){
			lector->getline(linea, max_linea);
			
			tok=strtok(linea, " \n\t");
			string palabra(tok);
			
			tok=strtok(NULL, " \n\t");
			n_docs=atoi(tok);
			
			for(int k=0; k<n_docs; k++){
				
				tok=strtok(NULL, " \n\t");
				doc_id=atoi(tok);
				
				tok=strtok(NULL, " \n\t");
				valor=atof(tok);
				
				lista_original->push_back(pair<int, float>(doc_id, valor));
				
			}
			
			lista=new ListaInvertida(lista_original);
			(*mapa_terms)[palabra]=lista;
			
			lista_original->clear();
			
		}//for... cada termino
		
		(*mapa_indices)[user_id]=mapa_terms;
		
	}//for... cada usuario
	
	lector->close();
	delete lector;
	
//	cout<<"lectura de users terminada\n";
	delete lista_original;
	
	delete [] linea;
	
//	cout<<"IndiceInvertido::cargar - fin\n";
	return true;
	
}

bool IndiceInvertido::cargarBinario(const char *archivo_indice){
	
//	cout<<"IndiceInvertido::cargar - inicio (desde "<<archivo_indice<<")\n";
	
	map<string, ListaInvertida*> *mapa_terms;
	
	int user_id, doc_id;
	int n_users, n_terms, n_docs;
	float valor;
	ListaInvertida *lista;
	list< pair<int, float> > *lista_original=new list< pair<int, float> >();
	
	fstream *lector=new fstream(archivo_indice, fstream::in | fstream::binary);
	int max_linea=1024;
	char *linea=new char[max_linea];
	int largo_palabra;
	
	lector->read((char*)(&n_users), sizeof(int));
	for(int i=0; i<n_users; i++){
		
		//user id
		lector->read((char*)(&user_id), sizeof(int));
		
		//numero terms
		lector->read((char*)(&n_terms), sizeof(int));
		
		mapa_terms=new map<string, ListaInvertida*>();
		
		for(int j=0; j<n_terms; j++){
			
			lector->read((char*)(&largo_palabra), sizeof(int));
			lector->read(linea, largo_palabra);
			linea[largo_palabra]=0;
			string palabra(linea);
			
			lector->read((char*)(&n_docs), sizeof(int));
			
			for(int k=0; k<n_docs; k++){
				
				lector->read((char*)(&doc_id), sizeof(int));
				lector->read((char*)(&valor), sizeof(float));
				
				lista_original->push_back(pair<int, float>(doc_id, valor));
				
			}
			
			lista=new ListaInvertida(lista_original);
			(*mapa_terms)[palabra]=lista;
			
			lista_original->clear();
			
		}//for... cada termino
		
		(*mapa_indices)[user_id]=mapa_terms;
		
	}//for... cada usuario
	
	lector->close();
	delete lector;
	
//	cout<<"lectura de users terminada\n";
	delete lista_original;
	
	delete [] linea;
	
//	cout<<"IndiceInvertido::cargar - fin\n";
	return true;

}

bool IndiceInvertido::cargarBinario(const char *archivo_indice, const char *archivo_grupos){
	
//	cout<<"IndiceInvertido::cargar - inicio (desde "<<archivo_indice<<" y "<<archivo_grupos<<")\n";
	
	//Lectura de Grupos
	
	map<int, int> mapa_grupos;
	
	fstream *lector=NULL;
	
	lector=new fstream(archivo_grupos, fstream::in | fstream::binary);
	
	int n_grupos=0;
	int doc_id, group_id;
	
	if(lector->good()){
		lector->read((char*)(&n_grupos), sizeof(int));
	}
	
	cout<<"n_grupos: "<<n_grupos<<"\n";
	
	for(int i=0; i<n_grupos; i++){

		lector->read((char*)(&doc_id), sizeof(int));
		lector->read((char*)(&group_id), sizeof(int));
		
		//cout<<"grupo["<<doc_id<<"]: "<<group_id<<"\n";
		
		mapa_grupos[doc_id]=group_id;
		
	}
	
	lector->close();
	delete lector;
	
	//Fin Lectura de Grupos
	
	
	//Lectura de Indice
	
	map<string, ListaInvertida*> *mapa_terms;
	
	int user_id;
	int n_users, n_terms, n_docs;
	float valor;
	ListaInvertida *lista;
	list< pair<int, float> > *lista_original=new list< pair<int, float> >();
	list< pair<int, float> >::iterator it_lista;
	
	list< pair<int, float> > *lista_final=new list< pair<int, float> >();
	list<float> valores;
	int ultimo_id;
	
	lector=new fstream(archivo_indice, fstream::in | fstream::binary);
	int max_linea=1024;
	char *linea=new char[max_linea];
	int largo_palabra;
	
	lector->read((char*)(&n_users), sizeof(int));
	
	for(int i=0; i<n_users; i++){
		
		//user id
		lector->read((char*)(&user_id), sizeof(int));
		
		//numero terms
		lector->read((char*)(&n_terms), sizeof(int));
		
		mapa_terms=new map<string, ListaInvertida*>();
		
		for(int j=0; j<n_terms; j++){
			
			lector->read((char*)(&largo_palabra), sizeof(int));
			lector->read(linea, largo_palabra);
			linea[largo_palabra]=0;
			string palabra(linea);
			
			lector->read((char*)(&n_docs), sizeof(int));
			
			for(int k=0; k<n_docs; k++){
				
				lector->read((char*)(&doc_id), sizeof(int));
				lector->read((char*)(&valor), sizeof(float));
				
//				lista_original->push_back(pair<int, float>(doc_id, valor));
				if(mapa_grupos.find(doc_id)!=mapa_grupos.end()){
					lista_original->push_back(pair<int, float>(mapa_grupos[doc_id], valor));
				}
				
			}
			
			//procesar lista con grupos
			lista_original->sort();
			
			it_lista=lista_original->begin();
			ultimo_id=it_lista->first;
			valor=it_lista->second;
			valores.push_back(valor);
			it_lista++;
			for(; it_lista!=lista_original->end(); it_lista++){
				doc_id=it_lista->first;
				valor=it_lista->second;
				if(doc_id==ultimo_id){
					valores.push_back(valor);
				}
				else{
					//guardar
					lista_final->push_back(pair<int, float>(ultimo_id, combinar_valores(valores)));
					valores.clear();
					ultimo_id=doc_id;
					valores.push_back(valor);
				}
			}
			//guardar
			lista_final->push_back(pair<int, float>(ultimo_id, combinar_valores(valores)));
			valores.clear();
			
			lista=new ListaInvertida(lista_final);
			(*mapa_terms)[palabra]=lista;
			
			lista_original->clear();
			lista_final->clear();
			
		}//for... cada termino
		
		(*mapa_indices)[user_id]=mapa_terms;
		
	}//for... cada usuario
	
	lector->close();
	delete lector;
	
//	cout<<"lectura de users terminada\n";
	delete lista_original;
	delete lista_final;
	mapa_grupos.clear();
	
	delete [] linea;
	
//	cout<<"IndiceInvertido::cargar - fin\n";
	return true;
}

bool IndiceInvertido::cargar(const char *archivo_indice, const char *archivo_grupos){
	
	cout<<"IndiceInvertido::cargar - inicio (desde "<<archivo_indice<<" y "<<archivo_grupos<<")\n";
	
	map<string, ListaInvertida*> *mapa_terms;
	
	int user_id, doc_id;
	int n_users, n_terms, n_docs;
	float valor;
	ListaInvertida *lista;
	list< pair<int, float> > *lista_original=new list< pair<int, float> >();
	list< pair<int, float> >::iterator it_lista;
	
	list< pair<int, float> > *lista_final=new list< pair<int, float> >();
	list<float> valores;
	int ultimo_id;
	int group_id;
	
	fstream *lector=NULL;
	int max_linea=1024*1024;
	char *linea=new char[max_linea];
	char *tok;
	
	//cout<<"IndiceInvertido::cargar - inicio carga grupos\n";
	
	map<int, int> mapa_grupos;
	
	lector=new fstream(archivo_grupos, fstream::in);
	
	lector->getline(linea, max_linea);
	//cout<<"IndiceInvertido::cargar - n_pares (desde "<<linea<<")\n";
	int n_pares=atoi(linea);
	
	for(int i=0; i<n_pares; i++){
		
		lector->getline(linea, max_linea);
		
		tok=strtok(linea, " \n\t");
		doc_id=atoi(tok);
		
		tok=strtok(NULL, " \n\t");
		group_id=atoi(tok);
		
		mapa_grupos[doc_id]=group_id;
		
	}
	
	lector->close();
	delete lector;
	
	//cout<<"IndiceInvertido::cargar - total contactos agrupados: "<<(mapa_grupos.size())<<"\n";
	
	//cout<<"IndiceInvertido::cargar - inicio carga indice\n";
	
	lector=new fstream(archivo_indice, fstream::in);
	
	lector->getline(linea, max_linea);
	//cout<<"IndiceInvertido::cargar - n_users (desde "<<linea<<")\n";
	n_users=atoi(linea);
	
	for(int i=0; i<n_users; i++){
		
		//user id
		lector->getline(linea, max_linea);
		//cout<<"IndiceInvertido::cargar - user_id (desde "<<linea<<")\n";
		user_id=atoi(linea);
		
		//numero terms
		lector->getline(linea, max_linea);
		//cout<<"IndiceInvertido::cargar - n_terms (desde "<<linea<<")\n";
		n_terms=atoi(linea);
		
		mapa_terms=new map<string, ListaInvertida*>();
		
		for(int j=0; j<n_terms; j++){
			lector->getline(linea, max_linea);
			
			tok=strtok(linea, " \n\t");
			string palabra(tok);
			
			tok=strtok(NULL, " \n\t");
			n_docs=atoi(tok);
			
			for(int k=0; k<n_docs; k++){
				
				tok=strtok(NULL, " \n\t");
				doc_id=atoi(tok);
				
				tok=strtok(NULL, " \n\t");
				valor=atof(tok);
				
//				lista_original->push_back(pair<int, float>(doc_id, valor));
				if(mapa_grupos.find(doc_id)!=mapa_grupos.end()){
					lista_original->push_back(pair<int, float>(mapa_grupos[doc_id], valor));
				}
				
			}
			
			//procesar lista con grupos
			lista_original->sort();
			
			it_lista=lista_original->begin();
			ultimo_id=it_lista->first;
			valor=it_lista->second;
			valores.push_back(valor);
			it_lista++;
			for(; it_lista!=lista_original->end(); it_lista++){
				doc_id=it_lista->first;
				valor=it_lista->second;
				if(doc_id==ultimo_id){
					valores.push_back(valor);
				}
				else{
					//guardar
					lista_final->push_back(pair<int, float>(ultimo_id, combinar_valores(valores)));
					valores.clear();
					ultimo_id=doc_id;
					valores.push_back(valor);
				}
			}
			//guardar
			lista_final->push_back(pair<int, float>(ultimo_id, combinar_valores(valores)));
			valores.clear();
			
			lista=new ListaInvertida(lista_final);
			(*mapa_terms)[palabra]=lista;
			
			lista_original->clear();
			lista_final->clear();
			
		}//for... cada termino
		
		//cout<<"IndiceInvertido::cargar - Agregando indice de user "<<user_id<<" con "<<(mapa_terms->size())<<" terminos\n";
		
		(*mapa_indices)[user_id]=mapa_terms;
		
	}//for... cada usuario
	
	lector->close();
	delete lector;
	
	//cout<<"IndiceInvertido::cargar - lectura de users terminada\n";
	delete lista_original;
	delete lista_final;
	mapa_grupos.clear();
	
	delete [] linea;
	
	cout<<"IndiceInvertido::cargar - fin\n";
	return true;
}

float IndiceInvertido::combinar_valores(list<float> &valores){
	if(valores.size()==0){
		return 0.0f;
	}
	float r=0.0f;
	for(list<float>::iterator it=valores.begin(); it!=valores.end(); it++){
		r+=*it;
	}
	r/=valores.size();
	return r;
}

ListaInvertida *IndiceInvertido::getLista(int user_id, string *s){
	
	//cout<<"IndiceInvertido::getLista - inicio ("<<user_id<<", "<<(*s)<<")\n";
	
	//map< int, map<string, ListaInvertida*>* > *mapa_indices;
	map< int, map<string, ListaInvertida*>* >::iterator it_mapa;
	map<string, ListaInvertida*> *indice;
	map<string, ListaInvertida*>::iterator it_indice;
	
	it_mapa=mapa_indices->find(user_id);
	if(it_mapa!=mapa_indices->end()){
		indice=it_mapa->second;
		it_indice=indice->find(*s);
		if(it_indice!=indice->end()){
			return it_indice->second;
		}
		else{
			return NULL;
		}
	}
	else{
		return NULL;
	}
	
	//cout<<"IndiceInvertido::getLista - inicio\n";
	
}


void IndiceInvertido::guardarBinario(const char *archivo){
	
	//map< int, map<string, ListaInvertida*>* > *mapa_indices;
	map< int, map<string, ListaInvertida*>* >::iterator it_mapa;
	map<string, ListaInvertida*> *mapa_user;
	map<string, ListaInvertida*>::iterator it_mapa_user;
	ListaInvertida *lista;
	
	int n_users, user_id, n_terms, n_docs, doc_id, largo_term;
	const char *term;
	float valor;
	
	fstream *escritor=new fstream(archivo, fstream::trunc | fstream::out | fstream::binary);
	
	//n_users
	n_users=mapa_indices->size();
	escritor->write((char*)(&n_users), sizeof(int));
	
	for(it_mapa=mapa_indices->begin(); it_mapa!=mapa_indices->end(); it_mapa++){
		user_id=it_mapa->first;
		mapa_user=it_mapa->second;
		
		//user_id
		escritor->write((char*)(&user_id), sizeof(int));
		
		//n_terms
		n_terms=mapa_user->size();
		escritor->write((char*)(&n_terms), sizeof(int));
		
		for(it_mapa_user=mapa_user->begin(); it_mapa_user!=mapa_user->end(); it_mapa_user++){
			//term n_docs doc valor doc valor...
			
			term=(it_mapa_user->first).c_str();
			largo_term=strlen(term);
			
			//notar que se guarda la palbra SIN EL 0 FINAL
			//el lector debe asegurar palabra_leida[largo_palabra]=0
			escritor->write((char*)(&largo_term), sizeof(int));
			escritor->write(term, largo_term);
			
			lista=it_mapa_user->second;
			n_docs=lista->size();
			escritor->write((char*)(&n_docs), sizeof(int));
			
			lista->reset();
			//el primer doc
			doc_id=lista->getDocId();
			valor=lista->getValor();
			escritor->write((char*)(&doc_id), sizeof(int));
			escritor->write((char*)(&valor), sizeof(float));
			//los demas docs
			while(lista->hasNext()){
				lista->next();
				doc_id=lista->getDocId();
				valor=lista->getValor();
				escritor->write((char*)(&doc_id), sizeof(int));
				escritor->write((char*)(&valor), sizeof(float));
			}//while... docs en lista
			
		}//for... cada term de lista de usuario
		
	}//for... cada usuario
	
	escritor->close();
	delete escritor;
	
}

//solo guarda el indice del usuario buscado
void IndiceInvertido::guardarBinario(const char *archivo, int user_buscado){
	
	//map< int, map<string, ListaInvertida*>* > *mapa_indices;
	map< int, map<string, ListaInvertida*>* >::iterator it_mapa;
	map<string, ListaInvertida*> *mapa_user;
	map<string, ListaInvertida*>::iterator it_mapa_user;
	ListaInvertida *lista;
	
	int n_users, user_id, n_terms, n_docs, doc_id, largo_term;
	const char *term;
	float valor;
	
	fstream *escritor=new fstream(archivo, fstream::trunc | fstream::out | fstream::binary);
	
	//n_users
	n_users=1;
	escritor->write((char*)(&n_users), sizeof(int));
	
	//user_id
	user_id=user_buscado;
	escritor->write((char*)(&user_id), sizeof(int));
	
	it_mapa=mapa_indices->find(user_buscado);
	if(it_mapa==mapa_indices->end()){
		//usuario no encontrado
		
		//n_terms
		n_terms=0;
		escritor->write((char*)(&n_terms), sizeof(int));
		
	}
	else{
		//usuario correcto
		mapa_user=it_mapa->second;
		
		//n_terms
		n_terms=mapa_user->size();
		escritor->write((char*)(&n_terms), sizeof(int));
		
		for(it_mapa_user=mapa_user->begin(); it_mapa_user!=mapa_user->end(); it_mapa_user++){
			//term n_docs doc valor doc valor...
			
			term=(it_mapa_user->first).c_str();
			largo_term=strlen(term);
			
			//notar que se guarda la palbra SIN EL 0 FINAL
			//el lector debe asegurar palabra_leida[largo_palabra]=0
			escritor->write((char*)(&largo_term), sizeof(int));
			escritor->write(term, largo_term);
			
			lista=it_mapa_user->second;
			n_docs=lista->size();
			escritor->write((char*)(&n_docs), sizeof(int));
			
			lista->reset();
			//el primer doc
			doc_id=lista->getDocId();
			valor=lista->getValor();
			escritor->write((char*)(&doc_id), sizeof(int));
			escritor->write((char*)(&valor), sizeof(float));
			//los demas docs
			while(lista->hasNext()){
				lista->next();
				doc_id=lista->getDocId();
				valor=lista->getValor();
				escritor->write((char*)(&doc_id), sizeof(int));
				escritor->write((char*)(&valor), sizeof(float));
			}//while... docs en lista
			
		}//for... cada term de lista de usuario
		
	}//else... usuario correcto
	
	escritor->close();
	delete escritor;
	
}



















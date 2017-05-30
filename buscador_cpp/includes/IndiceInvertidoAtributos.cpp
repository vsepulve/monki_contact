
#include "IndiceInvertidoAtributos.h"

IndiceInvertidoAtributos::IndiceInvertidoAtributos(){
	cout<<"IndiceInvertidoAtributos - inicio\n";
	mapa_indices=new map<int, map<string, map<string, ListaInvertida*>* >* >();
	max_linea=10*1024*1024;
	linea=new char[max_linea];
	cout<<"IndiceInvertidoAtributos - fin\n";
}

IndiceInvertidoAtributos::~IndiceInvertidoAtributos(){
	cout<<"IndiceInvertidoAtributos::~IndiceInvertidoAtributos - inicio\n";
	if(mapa_indices!=NULL){
		map< int, map<string, map<string, ListaInvertida*>* >* >::iterator it;
		map<string, map<string, ListaInvertida*>* > *mapa_atributos;
		map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
		map<string, ListaInvertida*> *indice;
		map<string, ListaInvertida*>::iterator it2;
		
		for(it=mapa_indices->begin(); it!=mapa_indices->end(); it++){
			mapa_atributos=it->second;
			
			for(it_atributos=mapa_atributos->begin(); it_atributos!=mapa_atributos->end(); it_atributos++){
				indice=it_atributos->second;
				
				for(it2=indice->begin(); it2!=indice->end(); it2++){
					delete it2->second;
				}
				
				indice->clear();
				delete indice;
			}
			mapa_atributos->clear();
			delete mapa_atributos;
		}
		
		mapa_indices->clear();
		delete mapa_indices;
		mapa_indices=NULL;
	}
	if(linea!=NULL){
		delete [] linea;
	}
	cout<<"IndiceInvertidoAtributos::~IndiceInvertidoAtributos - fin\n";
}

map< int, map<string, map<string, ListaInvertida*>* >* > *IndiceInvertidoAtributos::getMapa() const{
	return mapa_indices;
}

bool IndiceInvertidoAtributos::contieneUsuario(int user_id) const{
	return mapa_indices->find(user_id) != mapa_indices->end();
}

list<int> *IndiceInvertidoAtributos::getUsuarios() const{
	list<int> *lista_usuarios=new list<int>();
	map<int, map<string, map<string, ListaInvertida*>* >* >::iterator it;
	for(it=mapa_indices->begin(); it!=mapa_indices->end(); it++){
		lista_usuarios->push_back(it->first);
	}
	return lista_usuarios;
}

list<string> *IndiceInvertidoAtributos::getAtributosUsuario(int user_id) const{
	list<string> *atributos=new list<string>();
	map<int, map<string, map<string, ListaInvertida*>* >* >::iterator it;
	map<string, map<string, ListaInvertida*>* > *mapa_atributos;
	map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
	it=mapa_indices->find(user_id);
	if(it!=mapa_indices->end()){
		mapa_atributos=it->second;
		for(it_atributos=mapa_atributos->begin(); it_atributos!=mapa_atributos->end(); it_atributos++){
			atributos->push_back(it_atributos->first);
		}
	}
	return atributos;
}

bool IndiceInvertidoAtributos::cargar(const char *archivo_indice){
	
	cout<<"IndiceInvertidoAtributos::cargar - inicio (desde "<<archivo_indice<<")\n";
	
	int n_users, user_id;
	
	fstream *lector=NULL;
	bool errores=false;
	
	map<int, int> *mapa_grupos=NULL;
	
	lector=new fstream(archivo_indice, fstream::in);
	
	lector->getline(linea, max_linea);
	if(linea==NULL || strlen(linea)<1){
		cout<<"IndiceInvertidoAtributos::cargar - Error al leer n_users\n";
		errores=true;
		n_users=0;
	}
	else{
		//cout<<"IndiceInvertidoAtributos::cargar - n_users (desde "<<linea<<")\n";
		n_users=atoi(linea);
	}
	
	for(int i=0; i<n_users; i++){
		
		//user id
		lector->getline(linea, max_linea);
		if(linea==NULL || strlen(linea)<1){
			cout<<"IndiceInvertidoAtributos::cargar - Error al leer n_users\n";
			errores=true;
			break;
		}
		else{
			//cout<<"IndiceInvertidoAtributos::cargar - user_id (desde "<<linea<<")\n";
			user_id=atoi(linea);
		}
		
		if(! cargarUsuario(lector, user_id, mapa_grupos)){
			errores=true;
			break;
		}
		
	}//for... cada usuario
		
	lector->close();
	delete lector;
	
//	//Sin grupos
//	mapa_grupos->clear();
//	delete mapa_grupos;
	
	cout<<"IndiceInvertidoAtributos::cargar - fin (errores: "<<errores<<", mapa_indices->size: "<<(mapa_indices->size())<<")\n";
	return errores==false;
	
}

bool IndiceInvertidoAtributos::cargar(const char *archivo_indice, const char *archivo_grupos){
	
	cout<<"IndiceInvertidoAtributos::cargar - inicio (desde "<<archivo_indice<<" y "<<archivo_grupos<<")\n";
	
	int n_users, user_id;
	
	fstream *lector=NULL;
	bool errores=false;
	
	map<int, int> *mapa_grupos=cargarGrupos(archivo_grupos);
	
	lector=new fstream(archivo_indice, fstream::in);
	
	lector->getline(linea, max_linea);
	if(linea==NULL || strlen(linea)<1){
		cout<<"IndiceInvertidoAtributos::cargar - Error al leer n_users\n";
		errores=true;
		n_users=0;
	}
	else{
		//cout<<"IndiceInvertidoAtributos::cargar - n_users (desde "<<linea<<")\n";
		n_users=atoi(linea);
	}
	if(n_users==0){
		cout<<"IndiceInvertidoAtributos::cargar - Error (0 usuarios)\n";
		errores=true;
	}
	
	for(int i=0; i<n_users; i++){
		
		//user id
		lector->getline(linea, max_linea);
		if(linea==NULL || strlen(linea)<1){
			cout<<"IndiceInvertidoAtributos::cargar - Error al leer n_users\n";
			errores=true;
			break;
		}
		else{
			//cout<<"IndiceInvertidoAtributos::cargar - user_id (desde "<<linea<<")\n";
			user_id=atoi(linea);
		}
		
		if(! cargarUsuario(lector, user_id, mapa_grupos)){
			errores=true;
			break;
		}
		
	}//for... cada usuario
	
	lector->close();
	delete lector;
	
	mapa_grupos->clear();
	delete mapa_grupos;
	
	cout<<"IndiceInvertidoAtributos::cargar - fin (errores: "<<errores<<", mapa_indices->size: "<<(mapa_indices->size())<<")\n";
	return errores==false;
}

map<int, int> *IndiceInvertidoAtributos::cargarGrupos(const char *archivo_grupos){
	
	int n_pares, doc_id, group_id;
	
	fstream *lector=NULL;
	char *tok;
	bool errores=false;
	
	cout<<"IndiceInvertidoAtributos::cargarGrupos - Inicio carga grupos\n";
	
	map<int, int> *mapa_grupos=new map<int, int>();
	
	lector=new fstream(archivo_grupos, fstream::in);
	
	lector->getline(linea, max_linea);
	if(linea==NULL || strlen(linea)<1){
		cout<<"IndiceInvertidoAtributos::cargarGrupos - Error al leer n_pares\n";
		errores=true;
		n_pares=0;
	}
	else{
		//cout<<"IndiceInvertidoAtributos::cargarGrupos - n_pares (desde "<<linea<<")\n";
		n_pares=atoi(linea);
	}
	
	for(int i=0; i<n_pares; i++){
		
		lector->getline(linea, max_linea);
		if(linea==NULL || strlen(linea)<1){
			errores=true;
			break;
		}
		
		tok=strtok(linea, " \n\t");
		if(tok==NULL || strlen(tok)<1){
			errores=true;
			break;
		}
		doc_id=atoi(tok);
		
		tok=strtok(NULL, " \n\t");
		if(tok==NULL || strlen(tok)<1){
			errores=true;
			break;
		}
		group_id=atoi(tok);
		
		(*mapa_grupos)[doc_id]=group_id;
		
	}
	
	lector->close();
	delete lector;
	
	if(errores){
		cout<<"IndiceInvertidoAtributos::cargarGrupos - Fin (errores)\n";
		mapa_grupos->clear();
		delete mapa_grupos;
		return NULL;
	}
	
	cout<<"IndiceInvertidoAtributos::cargarGrupos - Fin\n";
	return mapa_grupos;
}

//carga leyendo desde el atributo
//recibe el mapa de grupos (si es NULL no lo usa y carga directamente)
//retorna el esatdo de la carga (true => todo ok)
bool IndiceInvertidoAtributos::cargarUsuario(fstream *lector, int user_id, map<int, int> *mapa_grupos){

	cout<<"IndiceInvertidoAtributos::cargarUsuario - Inicio (user "<<user_id<<")\n";
	
	map<string, map<string, ListaInvertida*>* > *mapa_atributos;
	map<string, ListaInvertida*> *mapa_terms;
	
	int doc_id;
	int n_terms, n_docs, n_atributos;
	float valor;
	ListaInvertida *lista;
	list< pair<int, float> > *lista_original=new list< pair<int, float> >();
	list< pair<int, float> >::iterator it_lista;
	
	list< pair<int, float> > *lista_final=new list< pair<int, float> >();
	list<float> valores;
	int ultimo_id;
	
	char *tok;
	bool errores=false;
	
	//atributos
	lector->getline(linea, max_linea);
	if(linea==NULL || strlen(linea)<1){
		cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer n_atributos\n";
		n_atributos=0;
		errores=true;
	}
	else{
		//cout<<"IndiceInvertidoAtributos::actualizarUsuario - n_atributos (desde "<<linea<<")\n";
		n_atributos=atoi(linea);
	}
	
	mapa_atributos=new map<string, map<string, ListaInvertida*>* >();
	
	for(int i_at=0; i_at<n_atributos; i_at++){
		
		lector->getline(linea, max_linea);
		if(linea==NULL || strlen(linea)<1){
			cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer atributo\n";
			errores=true;
			break;
		}
		//cout<<"IndiceInvertidoAtributos::actualizarUsuario - atributo (desde "<<linea<<")\n";
		string atributo(linea);
		
		//numero terms
		lector->getline(linea, max_linea);
		if(linea==NULL || strlen(linea)<1){
			cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer n_terms\n";
			errores=true;
			break;
		}
		//cout<<"IndiceInvertidoAtributos::actualizarUsuario - n_terms (desde "<<linea<<")\n";
		n_terms=atoi(linea);
		
		mapa_terms=new map<string, ListaInvertida*>();
	
		for(int j=0; j<n_terms; j++){
			lector->getline(linea, max_linea);
			if(linea==NULL || strlen(linea)<1){
				cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer lista\n";
				errores=true;
				break;
			}
		
			tok=strtok(linea, " \n\t");
			if(tok==NULL || strlen(tok)<1){
				cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer termino\n";
				errores=true;
				break;
			}
			string palabra(tok);
		
			tok=strtok(NULL, " \n\t");
			if(tok==NULL || strlen(tok)<1){
				cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer n_docs\n";
				errores=true;
				break;
			}
			n_docs=atoi(tok);
		
			for(int k=0; k<n_docs; k++){
				
				tok=strtok(NULL, " \n\t");
				if(tok==NULL || strlen(tok)<1){
					cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer doc_id\n";
					errores=true;
					break;
				}
				doc_id=atoi(tok);
				
				tok=strtok(NULL, " \n\t");
				if(tok==NULL || strlen(tok)<1){
					cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer valor\n";
					errores=true;
					break;
				}
				valor=atof(tok);
				
				if(mapa_grupos==NULL){
					//sin grupos
					lista_original->push_back(pair<int, float>(doc_id, valor));
				}
				else if(mapa_grupos->find(doc_id)!=mapa_grupos->end()){
					//con grupos
					lista_original->push_back(pair<int, float>((*mapa_grupos)[doc_id], valor));
				}
				else{
					//cout<<"IndiceInvertidoAtributos::actualizarUsuario - doc "<<doc_id<<" no agrupado\n";
				}
			}
			
			if(mapa_grupos == NULL){
				//sin grupos (usar lista_original directamente)
				
				lista=new ListaInvertida(lista_original);
				(*mapa_terms)[palabra]=lista;
				
				lista_original->clear();
				
			}
			else if(lista_original->size() == 0){
				//si no quedo nada agrupado, no agrego la lista al indice
				//podria realizarse algun control o warning
				
			}
			else{
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
						lista_final->push_back(pair<int, float>(ultimo_id, combinarValores(valores)));
						valores.clear();
						ultimo_id=doc_id;
						valores.push_back(valor);
					}
				}
				//guardar
				
				lista_final->push_back(pair<int, float>(ultimo_id, combinarValores(valores)));
				valores.clear();
				
				lista=new ListaInvertida(lista_final);
				(*mapa_terms)[palabra]=lista;
				
				lista_original->clear();
				lista_final->clear();
			
			}//else... todo ok, procesar lista con grupos
			
		}//for... cada termino
	
		//cout<<"IndiceInvertidoAtributos::actualizarUsuario - Agregando indice de user "<<user_id<<" con "<<(mapa_terms->size())<<" terminos\n";
		(*mapa_atributos)[atributo]=mapa_terms;
	
	}//for... cada atributo
	
	if(!errores){
		if(mapa_indices->find(user_id) != mapa_indices->end()){
			//borrar datos del usuario para reemplazar sus indices
		
			map<string, map<string, ListaInvertida*>* > *mapa_atributos_temp;
			map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
			map<string, ListaInvertida*> *indice;
			map<string, ListaInvertida*>::iterator it2;
		
			mapa_atributos_temp=(*mapa_indices)[user_id];
		
			for(it_atributos=mapa_atributos_temp->begin(); it_atributos!=mapa_atributos_temp->end(); it_atributos++){
				indice=it_atributos->second;
			
				for(it2=indice->begin(); it2!=indice->end(); it2++){
					delete it2->second;
				}
			
				indice->clear();
				delete indice;
			}//for cada atributo
			mapa_atributos_temp->clear();
			delete mapa_atributos_temp;
		
		}//if... borrar datos del usuario
		
		(*mapa_indices)[user_id]=mapa_atributos;
		
	}//sin errores
	
	//cout<<"IndiceInvertidoAtributos::actualizarUsuario - lectura de users terminada\n";
	delete lista_original;
	delete lista_final;
	
	cout<<"IndiceInvertidoAtributos::cargarUsuario - Fin (errores: "<<errores<<")\n";
	
	return errores==false;
}

bool IndiceInvertidoAtributos::actualizarUsuario(const char *archivo_indice, const char *archivo_grupos, int user_id){
	
	cout<<"IndiceInvertidoAtributos::actualizarUsuario - Inicio (desde "<<archivo_indice<<" y "<<archivo_grupos<<")\n";
	
	int n_users, user_id_leido;
	
	fstream *lector=NULL;
	
	bool errores=false;
	
	map<int, int> *mapa_grupos=cargarGrupos(archivo_grupos);
	
	if(mapa_grupos==NULL){
		cout<<"IndiceInvertidoAtributos::actualizarUsuario - Fin (errores)\n";
		return false;
	}
	
	lector=new fstream(archivo_indice, fstream::in);
	
	lector->getline(linea, max_linea);
	if(linea==NULL || strlen(linea)<1){
		cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer n_users\n";
		n_users=0;
		errores=true;
	}
	else{
		//cout<<"IndiceInvertidoAtributos::actualizarUsuario - n_users (desde "<<linea<<")\n";
		n_users=atoi(linea);
	}
	
	//user id
	lector->getline(linea, max_linea);
	if(linea==NULL || strlen(linea)<1){
		cout<<"IndiceInvertidoAtributos::actualizarUsuario - Error al leer user_id\n";
		user_id_leido=0;
		errores=true;
	}
	else{
		//cout<<"IndiceInvertidoAtributos::actualizarUsuario - user_id (desde "<<linea<<")\n";
		user_id_leido=atoi(linea);
	}
	
	if(errores || n_users!=1 || user_id_leido!=user_id){
		//error
		
		mapa_grupos->clear();
		delete mapa_grupos;
		
		lector->close();
		delete lector;
		
		cout<<"IndiceInvertidoAtributos::actualizarUsuario - Fin (errores)\n";
		return false;
	}
	
	//cargando datos usuario user_id
	bool resultado=cargarUsuario(lector, user_id, mapa_grupos);
	
	mapa_grupos->clear();
	delete mapa_grupos;
	
	lector->close();
	delete lector;
	
	cout<<"IndiceInvertidoAtributos::actualizarUsuario - fin\n";
	return resultado;
}

float IndiceInvertidoAtributos::combinarValores(list<float> &valores){
	if(valores.size()==0){
		return 0.0f;
	}
	float r=0.0f;
	for(list<float>::iterator it=valores.begin(); it!=valores.end(); it++){
		r+=*it;
	}
	r/=valores.size();
	//ajuste por el numero de contactos (heuristica de 4 contactos => 100%)
	r=r*(valores.size())/4;
	return r;
}

ListaInvertida *IndiceInvertidoAtributos::getLista(int user_id, string *atributo, string *s){
	
	//cout<<"IndiceInvertidoAtributos::getLista - inicio ("<<user_id<<", "<<(*atributo)<<", "<<(*s)<<")\n";
	
	//map< int, map<string, map<string, ListaInvertida*>* >* > *mapa_indices
	map<int, map<string, map<string, ListaInvertida*>* >* >::iterator it_indices;
	map<string, map<string, ListaInvertida*>* > *mapa_atributos;
	map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
	map<string, ListaInvertida*> *indice;
	map<string, ListaInvertida*>::iterator it_indice;
	
	it_indices=mapa_indices->find(user_id);
	if(it_indices!=mapa_indices->end()){
		
		mapa_atributos=it_indices->second;
		it_atributos=mapa_atributos->find(*atributo);
		if(it_atributos!=mapa_atributos->end()){
		
			indice=it_atributos->second;
			it_indice=indice->find(*s);
			if(it_indice!=indice->end()){
				
//				cout<<"IndiceInvertidoAtributos::getLista - revisando lista...\n";
				ListaInvertida *lista=it_indice->second;
				if(lista->size()==0){
//					cout<<"lista vacia\n";
				}
				else{
					lista->reset();
//					cout<<"("<<lista->getDocId()<<", "<<lista->getValor()<<")\n";
					while(lista->hasNext()){
						lista->next();
//						cout<<"("<<lista->getDocId()<<", "<<lista->getValor()<<")\n";
					}
				}
				
			
				return it_indice->second;
			}
			else{
//				cout<<"IndiceInvertidoAtributos::getLista - termino no encontrado\n";
				return NULL;
			}
			
		}
		else{
//			cout<<"IndiceInvertidoAtributos::getLista - atributo no encontrado\n";
			return NULL;
		}
	}
	else{
//		cout<<"IndiceInvertidoAtributos::getLista - usuario no encontrado\n";
		return NULL;
	}
	
	//cout<<"IndiceInvertidoAtributos::getLista - fin\n";
	
}

//void IndiceInvertidoAtributos::guardarBinario(const char *archivo){
//	
//	map< int, map<string, map<string, ListaInvertida*>* >* >::iterator it_mapa;
//	map<string, map<string, ListaInvertida*>* > *mapa_atributos;
//	map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
//	map<string, ListaInvertida*> *indice;
//	map<string, ListaInvertida*>::iterator it_indice;
//	ListaInvertida *lista;
//	
//	int n_users, user_id, n_terms, n_docs, doc_id, largo_term, n_atributos;
//	const char *term;
//	float valor;
//	
//	fstream *escritor=new fstream(archivo, fstream::trunc | fstream::out | fstream::binary);
//	
//	//n_users
//	n_users=mapa_indices->size();
//	escritor->write((char*)(&n_users), sizeof(int));
//	
//	for(it_mapa=mapa_indices->begin(); it_mapa!=mapa_indices->end(); it_mapa++){
//		user_id=it_mapa->first;
//		mapa_atributos=it_mapa->second;
//		
//		//user_id
//		escritor->write((char*)(&user_id), sizeof(int));
//		
//		//n_atributos
//		n_atributos=mapa_atributos->size();
//		escritor->write((char*)(&n_atributos), sizeof(int));
//		
//		for(it_atributos=mapa_atributos->begin(); it_atributos!=mapa_atributos->end(); it_atributos++){
//			
//			//atributo
//			term=(it_atributos->first).c_str();
//			largo_term=strlen(term);
//			
//			escritor->write((char*)(&largo_term), sizeof(int));
//			escritor->write(term, largo_term);
//			
//			indice=it_atributos->second;
//			
//			//n_terms
//			n_terms=indice->size();
//			escritor->write((char*)(&n_terms), sizeof(int));
//		
//			for(it_indice=indice->begin(); it_indice!=indice->end(); it_indice++){
//				//term n_docs doc valor doc valor...
//				
//				term=(it_indice->first).c_str();
//				largo_term=strlen(term);
//			
//				//notar que se guarda la palbra SIN EL 0 FINAL
//				//el lector debe asegurar palabra_leida[largo_palabra]=0
//				escritor->write((char*)(&largo_term), sizeof(int));
//				escritor->write(term, largo_term);
//			
//				lista=it_indice->second;
//				n_docs=lista->size();
//				escritor->write((char*)(&n_docs), sizeof(int));
//			
//				lista->reset();
//				//el primer doc
//				doc_id=lista->getDocId();
//				valor=lista->getValor();
//				escritor->write((char*)(&doc_id), sizeof(int));
//				escritor->write((char*)(&valor), sizeof(float));
//				//los demas docs
//				while(lista->hasNext()){
//					lista->next();
//					doc_id=lista->getDocId();
//					valor=lista->getValor();
//					escritor->write((char*)(&doc_id), sizeof(int));
//					escritor->write((char*)(&valor), sizeof(float));
//				}//while... docs en lista
//			
//			}//for... cada term de lista de usuario
//		
//		}//for... cada atributo
//		
//	}//for... cada usuario
//	
//	escritor->close();
//	delete escritor;
//	
//}

////solo guarda el indice del usuario buscado
//void IndiceInvertidoAtributos::guardarBinario(const char *archivo, int user_buscado){
//	
//	//map< int, map<string, ListaInvertida*>* > *mapa_indices;
//	map< int, map<string, map<string, ListaInvertida*>* >* >::iterator it_mapa;
//	map<string, map<string, ListaInvertida*>* > *mapa_atributos;
//	map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
//	map<string, ListaInvertida*> *indice;
//	map<string, ListaInvertida*>::iterator it_indice;
//	ListaInvertida *lista;
//	
//	int n_users, user_id, n_terms, n_docs, doc_id, largo_term, n_atributos;
//	const char *term;
//	float valor;
//	
//	fstream *escritor=new fstream(archivo, fstream::trunc | fstream::out | fstream::binary);
//	
//	//n_users
//	n_users=1;
//	//cout<<n_users<<"\n";
//	escritor->write((char*)(&n_users), sizeof(int));
//	
//	//user_id
//	user_id=user_buscado;
//	//cout<<user_id<<"\n";
//	escritor->write((char*)(&user_id), sizeof(int));
//	
//	it_mapa=mapa_indices->find(user_buscado);
//	if(it_mapa==mapa_indices->end()){
//		//usuario no encontrado
//		
//		//n_atributos
//		n_atributos=0;
//		escritor->write((char*)(&n_atributos), sizeof(int));
//		
//		//n_terms
//		n_terms=0;
//		escritor->write((char*)(&n_terms), sizeof(int));
//		
//	}
//	else{
//		//usuario correcto
//		mapa_atributos=it_mapa->second;
//		
//		//n_atributos
//		n_atributos=mapa_atributos->size();
//		//cout<<n_atributos<<"\n";
//		escritor->write((char*)(&n_atributos), sizeof(int));
//		
//		for(it_atributos=mapa_atributos->begin(); it_atributos!=mapa_atributos->end(); it_atributos++){
//			//atributo
//			term=(it_atributos->first).c_str();
//			largo_term=strlen(term);
//			
//			//notar que se guarda la palbra SIN EL 0 FINAL
//			//el lector debe asegurar palabra_leida[largo_palabra]=0
//			escritor->write((char*)(&largo_term), sizeof(int));
//			escritor->write(term, largo_term);
//			//cout<<term<<"\n";
//			
//			indice=it_atributos->second;
//			
//			//n_terms
//			n_terms=indice->size();
//			escritor->write((char*)(&n_terms), sizeof(int));
//			//cout<<n_terms<<"\n";
//		
//			for(it_indice=indice->begin(); it_indice!=indice->end(); it_indice++){
//				//term n_docs doc valor doc valor...
//			
//				term=(it_indice->first).c_str();
//				largo_term=strlen(term);
//			
//				//notar que se guarda la palbra SIN EL 0 FINAL
//				//el lector debe asegurar palabra_leida[largo_palabra]=0
//				escritor->write((char*)(&largo_term), sizeof(int));
//				escritor->write(term, largo_term);
//				//cout<<term<<" ";
//			
//				lista=it_indice->second;
//				n_docs=lista->size();
//				escritor->write((char*)(&n_docs), sizeof(int));
//				//cout<<n_docs<<" ";
//			
//				lista->reset();
//				//el primer doc
//				doc_id=lista->getDocId();
//				valor=lista->getValor();
//				escritor->write((char*)(&doc_id), sizeof(int));
//				escritor->write((char*)(&valor), sizeof(float));
//				//cout<<doc_id<<" "<<valor<<" ";
//				//los demas docs
//				while(lista->hasNext()){
//					lista->next();
//					doc_id=lista->getDocId();
//					valor=lista->getValor();
//					escritor->write((char*)(&doc_id), sizeof(int));
//					escritor->write((char*)(&valor), sizeof(float));
//					//cout<<doc_id<<" "<<valor<<" ";
//				}//while... docs en lista
//				//cout<<"\n";
//			
//			}//for... cada term del indice
//			
//		}//for... cada atributo
//		
//	}//else... usuario correcto
//	
//	escritor->close();
//	delete escritor;
//	
//}



















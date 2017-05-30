
#include "ServidorVocabularioPrefijos.h"

ServidorVocabularioPrefijos::ServidorVocabularioPrefijos(){
	
	vocabularios=new map<int, ArbolPrefijosEstandar*>();
}

ServidorVocabularioPrefijos::ServidorVocabularioPrefijos(const char *archivo_ii){
cout<<"ServidorVocabularioPrefijos - inicio (archivo: "<<archivo_ii<<", texto)\n";
	
	vocabularios=new map<int, ArbolPrefijosEstandar*>();
	IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
	if(indice->cargar(archivo_ii)){
		cargarVocabularios(indice);
	}
	else{
		cout<<"ServidorVocabulario - Error al cargar indice\n";
	}
	delete indice;
	
cout<<"ServidorVocabulario - fin\n";
}

ServidorVocabularioPrefijos::ServidorVocabularioPrefijos(const char *archivo_ii, bool binario){
cout<<"ServidorVocabularioPrefijos - inicio (archivo: "<<archivo_ii<<", binario: "<<binario<<")\n";
	
	vocabularios=new map<int, ArbolPrefijosEstandar*>();
	IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
	if(binario){
		//indice->cargarBinario(archivo_ii);
		cout<<"ServidorVocabularioPrefijos::consultar - carga binaria deshabilitada\n";
	}
	else{
		if(indice->cargar(archivo_ii)){
			cargarVocabularios(indice);
		}
		else{
			cout<<"ServidorVocabulario - Error al cargar indice\n";
		}
	}
	delete indice;
	
cout<<"ServidorVocabulario - fin\n";
}

void ServidorVocabularioPrefijos::actualizarUsuario(const char *archivo_ii, bool binario, int user_id){
cout<<"ServidorVocabularioPrefijos::actualizarUsuario - inicio (archivo: "<<archivo_ii<<", binario: "<<binario<<", user_id: "<<user_id<<")\n";
	
	IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
	if(binario){
		//indice->cargarBinario(archivo_ii);
		cout<<"ServidorVocabularioPrefijos::consultar - carga binaria deshabilitada\n";
	}
	else{
		if(indice->cargar(archivo_ii)){
			cout<<"ServidorVocabularioPrefijos::consultar - indice cargado, buscando voc de usuario\n";
			//vocabularios=new map<int, ArbolPrefijosEstandar*>();
			map<int, ArbolPrefijosEstandar*>::iterator it_vocs;
			it_vocs=vocabularios->find(user_id);
			if(it_vocs!=vocabularios->end()){
				cout<<"ServidorVocabularioPrefijos::consultar - voc de usuario encontrado, borrando\n";
				delete it_vocs->second;
				//vocabularios->erase(it_vocs);
				//Puede ser mas eficiente conservar el registro para no desordenar la estructura interna
				//Despues de todo esta (asumo) asegurado que se reusara la posicion de user_id
				(*vocabularios)[user_id]=NULL;
			}
			cout<<"ServidorVocabularioPrefijos::consultar - cargando nuevo voc de usuario\n";
			cargarVocabularios(indice, user_id);
		}
		else{
			cout<<"ServidorVocabularioPrefijos::consultar - Error al cargar indice\n";
		}
	}
	
	delete indice;
	
cout<<"ServidorVocabulario::actualizarUsuario - fin\n";
}

void ServidorVocabularioPrefijos::cargarVocabularios(IndiceInvertidoAtributos *indice){
cout<<"ServidorVocabularioPrefijos::cargarVocabularios - inicio\n";
	
	map< int, map<string, map<string, ListaInvertida*>* >* > *indices=indice->getMapa();
	
	map< int, map<string, map<string, ListaInvertida*>* >* >::iterator it_indices;
	map<string, map<string, ListaInvertida*>* > *mapa_atributos;
	map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
	map<string, ListaInvertida*> *mapa;
	map<string, ListaInvertida*>::iterator it_mapa;
	
	list<string*> *vocabulario=new list<string*>();
	list<string*>::iterator it_voc;
	
	int user_id;
	
	for(it_indices=indices->begin(); it_indices!=indices->end(); it_indices++){
		user_id=it_indices->first;
		mapa_atributos=it_indices->second;
		
		for(it_atributos=mapa_atributos->begin(); it_atributos!=mapa_atributos->end(); it_atributos++){
			//atributo=it_atributos->first;
			mapa=it_atributos->second;
			
			for(it_mapa=mapa->begin(); it_mapa!=mapa->end(); it_mapa++){
				//prueba de cargar solo palabras con mas de 1 doc
				if(it_mapa->second->size()>2 && it_mapa->second->size()<128){
					vocabulario->push_back( (string*) (&(it_mapa->first)) );
				}
			}//for... cada termino
		}//for... cada atributo
		
//		(*vocabularios)[user_id]=new ArbolPrefijosSimple(vocabulario);
		(*vocabularios)[user_id]=new ArbolPrefijosEstandar(vocabulario);
		
		//aqui no es necesario eliminar la memoria pues es de los strings del indice
//		for(it_voc=vocabulario->begin(); it_voc!=vocabulario->end(); it_voc++){
//			delete [] (*it_voc);
//		}
		
		vocabulario->clear();
		
	}
	
	delete vocabulario;
	
cout<<"ServidorVocabularioPrefijos::cargarVocabularios - fin\n";
	
}

void ServidorVocabularioPrefijos::cargarVocabularios(IndiceInvertidoAtributos *indice, int user_id){
cout<<"ServidorVocabularioPrefijos::cargarVocabularios - inicio\n";
	
	map< int, map<string, map<string, ListaInvertida*>* >* > *indices=indice->getMapa();
	
	map< int, map<string, map<string, ListaInvertida*>* >* >::iterator it_indices;
	map<string, map<string, ListaInvertida*>* > *mapa_atributos;
	map<string, map<string, ListaInvertida*>* >::iterator it_atributos;
	map<string, ListaInvertida*> *mapa;
	map<string, ListaInvertida*>::iterator it_mapa;
	
	list<string*> *vocabulario=new list<string*>();
	list<string*>::iterator it_voc;
	
	it_indices=indices->find(user_id);
	if(it_indices!=indices->end()){
		//user_id=it_indices->first;
		mapa_atributos=it_indices->second;
		
		for(it_atributos=mapa_atributos->begin(); it_atributos!=mapa_atributos->end(); it_atributos++){
			//atributo=it_atributos->first;
			mapa=it_atributos->second;
			
			for(it_mapa=mapa->begin(); it_mapa!=mapa->end(); it_mapa++){
				//prueba de cargar solo palabras con mas de 1 doc
				if(it_mapa->second->size()>2 && it_mapa->second->size()<128){
					vocabulario->push_back( (string*) (&(it_mapa->first)) );
				}
			}//for... cada termino
		}//for... cada atributo
		
		(*vocabularios)[user_id]=new ArbolPrefijosEstandar(vocabulario);
		
		vocabulario->clear();
		
	}
	
	delete vocabulario;
	
cout<<"ServidorVocabularioPrefijos::cargarVocabularios - fin\n";
	
}

//void ServidorVocabularioPrefijos::cargarVocabularios(IndiceInvertido *indice){
//cout<<"ServidorVocabularioPrefijos::cargarVocabularios - inicio\n";
//	
//	map< int, map<string, ListaInvertida*>* > *indices=indice->getMapa();
//	
//	map< int, map<string, ListaInvertida*>* >::iterator it_indices;
//	map<string, ListaInvertida*> *mapa;
//	map<string, ListaInvertida*>::iterator it_mapa;
//	
//	list<string*> *vocabulario=new list<string*>();
//	list<string*>::iterator it_voc;
//	
//	int user_id;
//	
//	for(it_indices=indices->begin(); it_indices!=indices->end(); it_indices++){
//		user_id=it_indices->first;
//		mapa=it_indices->second;
//		
//		for(it_mapa=mapa->begin(); it_mapa!=mapa->end(); it_mapa++){
//			//prueba de cargar solo palabras con mas de 1 doc
//			if(it_mapa->second->size()>2 && it_mapa->second->size()<128){
//				vocabulario->push_back( (string*) (&(it_mapa->first)) );
//			//}
//		}
//		
////		(*vocabularios)[user_id]=new ArbolPrefijosSimple(vocabulario);
//		(*vocabularios)[user_id]=new ArbolPrefijosEstandar(vocabulario);
//		
//		//aqui no es necesario eliminar la memoria pues es de los strings del indice
////		for(it_voc=vocabulario->begin(); it_voc!=vocabulario->end(); it_voc++){
////			delete [] (*it_voc);
////		}
//		
//		vocabulario->clear();
//		
//	}
//	
//	delete vocabulario;
//	
//cout<<"ServidorVocabularioPrefijos::cargarVocabularios - fin\n";
//	
//}

ServidorVocabularioPrefijos::~ServidorVocabularioPrefijos(){
//cout<<"ServidorVocabularioPrefijos::~ServidorVocabulario - inicio\n";
	
	if(vocabularios!=NULL){
		//map<int, ArbolPrefijosSimple*>::iterator it;
		map<int, ArbolPrefijosEstandar*>::iterator it;
		for(it=vocabularios->begin(); it!=vocabularios->end(); it++){
			delete it->second;
		}
		vocabularios->clear();
		delete vocabularios;
		vocabularios=NULL;
	}
	
//cout<<"ServidorVocabularioPrefijos::~ServidorVocabulario - fin\n";
}

//recibe el c-string con la consulta, y escribe en el c-string de salida (supone que tiene memoria suficiente)
int ServidorVocabularioPrefijos::consultar(char *consulta, char *salida){
	//cout<<"ServidorVocabularioPrefijos::consultar - inicio ("<<consulta<<")\n";
	
	char *tok;
	int k=0;
	//mapa user_id => lista (terms)
	map<int, list<string> > mapa_atributos;
	map<int, list<string> >::iterator it_atributos;
	map<int, list<string> > mapa_consulta;
	map<int, list<string> >::iterator it_consulta;
	list<string>::iterator it_terms;
	int user_id=0;
	int n_atributos=0;
	bool actualizar_usuario = false;
	
	last_update_user = -1;
	last_update = false;
	
	//Lectura de Comando
	
	tok = strtok(consulta, " ");
	if(tok==NULL || strlen(tok)<1){
		cout<<"ServidorVocabularioPrefijos::consultar - Error al leer comando\n";
		salida[0]=0;
		return 0;
	}
	else if(strcmp(tok, "u")==0 || strcmp(tok, "us")==0){
		
		if(strcmp(tok, "us")==0){
			actualizar_usuario=true;
		}
	
		//update
		//u ruta_indice binario
		//formato nuevo (aunque no se use grupos)
		//us ruta_indice ruta_grupos binario user_id
		cout<<"ServidorVocabularioPrefijos::consultar - actualizando indices\n";
		
		char *archivo_ii;
		//char *archivo_grupos=NULL;
		bool binario;
		
		tok = strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1){
			cout<<"ServidorVocabularioPrefijos::consultar - Error al leer archivo_ii\n";
			salida[0]=0;
			return 0;
		}
		archivo_ii = tok;
		
		tok = strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1){
			cout<<"ServidorVocabularioPrefijos::consultar - Error al leer archivo_grupos\n";
			salida[0]=0;
			return 0;
		}
		//archivo_grupos=tok;
		
		tok = strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1){
			cout<<"ServidorVocabularioPrefijos::consultar - Error al leer binario\n";
			salida[0]=0;
			return 0;
		}
		binario = atoi(tok)>0;
		
		if(actualizar_usuario){
			
			tok = strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorVocabularioPrefijos::consultar - Error al leer user_id\n";
				salida[0]=0;
				return 0;
			}
			user_id = atoi(tok);
			last_update_user = user_id;
			last_update = true;
			
			//rankeador->getIndice()->actualizar_usuario(archivo_ii, archivo_grupos, user_id);
			actualizarUsuario(archivo_ii, binario, user_id);
			
			cout<<"ServidorVocabularioPrefijos::consultar - proceso terminado\n";
			
			salida[0]=0;
			return 0;
			
		}//if... actualizar usuario
		else{
			cout<<"ServidorVocabularioPrefijos::consultar - Recarga completa deshabilitada\n";
		
//			cout<<"ServidorVocabularioPrefijos::consultar - recargando desde "<<archivo_ii<<", binario: "<<binario<<"\n";
//			
//			if(vocabularios!=NULL){
//				map<int, ArbolPrefijosEstandar*>::iterator it;
//				for(it=vocabularios->begin(); it!=vocabularios->end(); it++){
//					delete it->second;
//				}
//				vocabularios->clear();
//				delete vocabularios;
//				vocabularios=NULL;
//			}
//			
//			IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
//			if(binario){
//				//indice->cargarBinario(archivo_ii);
//				cout<<"ServidorVocabularioPrefijos::consultar - carga binaria deshabilitada\n";
//			}
//			else{
//				//verificar estado del indice
//				indice->cargar(archivo_ii);
//			}
//			vocabularios=new map<int, ArbolPrefijosEstandar*>();
//			cargarVocabularios(indice);
//			delete indice;
//			
//			cout<<"ServidorVocabularioPrefijos::consultar - proceso terminado\n";
//			
//			salida[0]=0;
//			return 0;
//		
	
		}//else... actualizacion completa
		
	}
	else if(strcmp(tok, "vm")==0){
		//Consulta multiusuario:
		//vm k n_users u1 n_at at1 at2... atn n_terms1 t1 t2... tn u2... 
		//cout<<"ServidorVocabularioPrefijos::consultar - multiusuario\n";
		int n_users=0;
		int n_terms=0;
		//k (sugerencias esperadas en total)
		tok=strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1 || atoi(tok)==0 || atoi(tok)>100){
			cout<<"ServidorVocabularioPrefijos::consultar - Error al leer k\n";
			salida[0]=0;
			return 0;
		}
		k=atoi(tok);
		//n_users
		tok=strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1 || atoi(tok)==0 || atoi(tok)>100){
			cout<<"ServidorVocabularioPrefijos::consultar - Error al leer n_users\n";
			salida[0]=0;
			return 0;
		}
		n_users=atoi(tok);
		for(int i=0; i<n_users; i++){
			//user_id
			tok=strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorVocabularioPrefijos::consultar - Error al leer user_id\n";
				salida[0]=0;
				return 0;
			}
			user_id=atoi(tok);
			
			//n_atributos
			tok=strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorVocabularioPrefijos::consultar - Error al leer n_atributos\n";
				salida[0]=0;
				return 0;
			}
			n_atributos=atoi(tok);
			for(int j=0; j<n_atributos; j++){
				tok=strtok(NULL, " ");
				if(tok==NULL || strlen(tok)<1){
					cout<<"ServidorVocabularioPrefijos::consultar - Error al leer atributo\n";
					salida[0]=0;
					return 0;
				}
				mapa_atributos[user_id].push_back(string(tok));
			}
			
			//n_terms
			tok=strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorVocabularioPrefijos::consultar - Error al leer n_terms\n";
				salida[0]=0;
				return 0;
			}
			n_terms=atoi(tok);
			for(int j=0; j<n_terms; j++){
				tok=strtok(NULL, " ");
				if(tok==NULL || strlen(tok)<1){
					cout<<"ServidorVocabularioPrefijos::consultar - Error al leer termino\n";
					salida[0]=0;
					return 0;
				}
				mapa_consulta[user_id].push_back(string(tok));
			}//for... cada termino del usuario
		}//for... cada usuario
		//revisar resultados
		//cout<<"ServidorVocabularioPrefijos::consultar - k: "<<k<<", n_users: "<<n_users<<"\n";
		//for(it_consulta=mapa_consulta.begin(); it_consulta!=mapa_consulta.end(); it_consulta++){
			//cout<<"ServidorVocabularioPrefijos::consultar - user_id: "<<it_consulta->first<<" (";
			//for(it_terms=(it_consulta->second).begin(); it_terms!=(it_consulta->second).end(); it_terms++){
			//	cout<<(*it_terms)<<" ";
			//}
			//cout<<")\n";
		//}//for... cada usuario
	}
	else{
		cout<<"ServidorVocabularioPrefijos::consultar - Comando desconocido ("<<tok<<")\n";
		salida[0]=0;
		return 0;
	}
	
	//Fin Lectura de Comando
	
	//proceso generico con mapa de consulta
	//genera un mapa de resultados
	map<int, list<string> > mapa_respuesta;
	map<int, list<string> >::iterator it_respuesta;
	list<string>::iterator it_res;
	
	map<int, ArbolPrefijosEstandar*>::iterator it_voc;
	ArbolPrefijosEstandar *indice;
	
	for(it_consulta=mapa_consulta.begin(); it_consulta!=mapa_consulta.end(); it_consulta++){
		user_id=it_consulta->first;
		//cout<<"ServidorVocabularioPrefijos::consultar - consultando indice de user "<<user_id<<"\n";
		it_voc=vocabularios->find(user_id);
		if(it_voc!=vocabularios->end()){
			indice=it_voc->second;
			//extender ultima palabra
//			cout<<"ServidorVocabularioPrefijos::consultar - consultando indice\n";
//			indice->consultar(mapa_consulta[user_id].back().c_str(), &(mapa_respuesta[user_id]));
			indice->consultar(mapa_consulta[user_id].back().c_str(), &(mapa_respuesta[user_id]), k);
			
			//cout<<"ServidorVocabularioPrefijos::consultar - resultado: ";
			//for(it_res=mapa_respuesta[user_id].begin(); it_res!=mapa_respuesta[user_id].end(); it_res++){
			//	cout<<(*it_res)<<" ";
			//}
			//cout<<"\n";
			
			//el primer resultado (y solo ese) puede ser la palabra buscada
			//solo en ese caso puede tener el mismo largo
			it_res=mapa_respuesta[user_id].begin();
			if(it_res!=mapa_respuesta[user_id].end() && it_res->size() == mapa_consulta[user_id].back().size() ){
				//cout<<"ServidorVocabularioPrefijos::consultar - eliminando palabra repetida ("<<(*it_res)<<")\n";
				mapa_respuesta[user_id].erase(it_res);
			}
			
		}
		else{
			//cout<<"ServidorVocabularioPrefijos::consultar - indice no encontrado\n";
		}
		
	}
	
	//desduplicacion de sugerencias?
	//Esto queda fuera pues las consultas por usuario son independientes en todo sentido
	
	//escribir el resultado
	
	//nu u1 nt1 t1 v1 t2 v2... tn vn u2 nt2...
	
	//nuevo formato debe diferenciar grupos de terminos para componer el and
	//es decir: (ng1 t11 t12 t13) (ng2 t21 t22 t23)
	//de modo que pueda realizarse (g1 AND g2)
	//nu u1 ng ng1 t1 v1 t2 v2... tn vn ng2 t1 v1... u2...
	
	int total_grupos=0;
	
	salida[0]=0;
	//n_users
	sprintf(salida, "%d ", (int)(mapa_consulta.size()));
	
	for(it_consulta=mapa_consulta.begin(); it_consulta!=mapa_consulta.end(); it_consulta++){
		user_id=it_consulta->first;
		
		//user_id
		sprintf(salida+strlen(salida), "%d ", user_id);
		
		//n_atributos
		n_atributos=mapa_atributos[user_id].size();
		sprintf(salida+strlen(salida), "%d ", n_atributos);
		for(it_terms=mapa_atributos[user_id].begin(); it_terms!=mapa_atributos[user_id].end(); it_terms++){
			sprintf(salida+strlen(salida), "%s ", it_terms->c_str());
		}
		
		total_grupos=(it_consulta->second).size();
		//n_grupos
		sprintf(salida+strlen(salida), "%d ", total_grupos);
		
		//para todos los grupos salvo el ultimo
		it_terms=(it_consulta->second).begin();
		for(int i=0; i<total_grupos-1; i++){
		//for(it_terms=(it_consulta->second).begin(); it_terms!=(it_consulta->second).end(); it_terms++){
			sprintf(salida+strlen(salida), "1 %s %2.2f ", it_terms->c_str(), (float)puntaje_normal);
			it_terms++;
		}
		
		//ultimo grupo (con extension), el iterador quedo consistente del for anterior
		sprintf(salida+strlen(salida), "%d %s %2.2f ", (unsigned int)(1+mapa_respuesta[user_id].size()), it_terms->c_str(), (float)puntaje_normal);
		for(it_terms=mapa_respuesta[user_id].begin(); it_terms!=mapa_respuesta[user_id].end(); it_terms++){
			sprintf(salida+strlen(salida), "%s %2.2f ", it_terms->c_str(), (float)puntaje_sugerencia);
		}
		
	}
	
	return strlen(salida);

}



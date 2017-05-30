
#include "ServidorConsultas.h"

ServidorConsultas::ServidorConsultas(){
	rankeador=NULL;
	comp=Comparador();
}

ServidorConsultas::ServidorConsultas(const char *archivo_ii, bool binario){
	
	cout<<"ServidorConsultas - inicio (archivo: "<<archivo_ii<<", binario: "<<binario<<")\n";
	
	IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
	if(binario){
		//indice->cargarBinario(archivo_ii);
		cout<<"ServidorConsultas - carga binaria deshabilitada\n";
	}
	else{
		indice->cargar(archivo_ii);
	}
	
	rankeador=new RankeadorBasico(indice);
	
	comp=Comparador();
	
	cout<<"ServidorConsultas - inicio\n";
	
}

ServidorConsultas::ServidorConsultas(const char *archivo_ii, const char *archivo_grupos, bool binario){
	
	cout<<"ServidorConsultas - inicio (archivo: "<<archivo_ii<<", binario: "<<binario<<")\n";
	
	IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
	if(binario){
		//indice->cargarBinario(archivo_ii, archivo_grupos);
		cout<<"ServidorConsultas - carga binaria deshabilitada\n";
	}
	else{
		indice->cargar(archivo_ii, archivo_grupos);
	}
	
	rankeador=new RankeadorBasico(indice);
	
	comp=Comparador();
	
	cout<<"ServidorConsultas - inicio\n";
	
}

ServidorConsultas::~ServidorConsultas(){
	cout<<"ServidorConsultas::~ServidorConsultas - inicio\n";
	if(rankeador!=NULL){
		delete rankeador;
		rankeador=NULL;
	}
	cout<<"ServidorConsultas::~ServidorConsultas - fin\n";
}

//recibe el c-string con la consulta, y escribe en el c-string de salida (supone que tiene memoria suficiente)
int ServidorConsultas::consultar(char *consulta, char *salida){
	cout<<"ServidorConsultas::consultar - inicio\n";
	
	char *tok;
	int k = 0;
	
	map<int, list<string> > mapa_atributos;
	map<int, list<string> >::iterator it_atributos;
	list<string>::iterator it_lista_terms;
	
	pair<string, float> ***pares = NULL;
	int **n_terms_usuario = NULL;
	int *n_grupos_usuario = NULL;
	int *usuarios = NULL;
	int n_usuarios = 0;
	int n_atributos = 0;
	
	list<pair<string, float> >::iterator it_terms;
	int user_id;
	int doc_id;
	float valor;
	
	bool consulta_or = false;
	bool actualizar_usuario = false;
	last_update_user = -1;
	last_update = false;
	
	//Comando
	tok=strtok(consulta, " ");
	if(tok==NULL || strlen(tok)<1){
		cout<<"ServidorConsultas::consultar - Error al leer comando\n";
		salida[0]=0;
		return 0;
	}
	else if(strcmp(tok, "u")==0 || strcmp(tok, "us")==0){
		
		if(strcmp(tok, "us") == 0){
			actualizar_usuario = true;
		}
		
		//update
		//u ruta_indice ruta_grupos binario
		//us ruta_indice ruta_grupos binario user_id
		cout<<"ServidorConsultas::consultar - actualizando indice\n";
		char *archivo_ii;
		char *archivo_grupos;
		//bool binario;
		
		tok = strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1){
			cout<<"ServidorConsultas::consultar - Error al leer archivo_ii\n";
			salida[0]=0;
			return 0;
		}
		archivo_ii = tok;
		
		tok = strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1){
			cout<<"ServidorConsultas::consultar - Error al leer archivo_grupos\n";
			salida[0]=0;
			return 0;
		}
		archivo_grupos = tok;
		
		tok = strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1){
			cout<<"ServidorConsultas::consultar - Error al leer binario\n";
			salida[0]=0;
			return 0;
		}
		//binario=atoi(tok)>0;
		
		if(actualizar_usuario){
			
			tok = strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorConsultas::consultar - Error al leer user_id\n";
				salida[0]=0;
				return 0;
			}
			user_id = atoi(tok);
			last_update_user = user_id;
			last_update = true;
			
			//notar que aqui deberia hacer el if/else por binario (como en el caso de abajo)
			//lo dejo porque POR AHORA no estamos usando el indice binario
			rankeador->getIndice()->actualizarUsuario(archivo_ii, archivo_grupos, user_id);
			
			cout<<"ServidorConsultas::consultar - proceso terminado\n";
			
			salida[0]=0;
			return 0;
			
		}//if... actualizar usuario
		else{
			
			cout<<"ServidorConsultas::consultar - Recarga completa deshabilitada\n";
			
//			cout<<"ServidorConsultas::consultar - recargando desde "<<archivo_ii<<", "<<archivo_grupos<<", binario: "<<binario<<"\n";
//		
//			if(rankeador!=NULL){
//				delete rankeador;
//				rankeador=NULL;
//			}
//			IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
//			if(binario){
//				//indice->cargarBinario(archivo_ii, archivo_grupos);
//				cout<<"ServidorConsultas::consultar - carga binaria deshabilitada\n";
//			}
//			else{
//				//Revisar estado del indice
//				indice->cargar(archivo_ii, archivo_grupos);
//			}
//			rankeador=new RankeadorBasico(indice);
//		
//			cout<<"ServidorConsultas::consultar - proceso terminado\n";
//		
//			salida[0]=0;
//			return 0;

		}//else... actualizacion completa
		
	}
	else if(strcmp(tok, "ima")==0 || strcmp(tok, "imo")==0){
		
		if(strcmp(tok, "imo")==0){
			consulta_or=true;
		}
		
		//Consulta multiusuario:
		//im[a/o] k n_users u1 n_terms1 t1 v1 t2 v2... tn vn u2 n_terms2... 
		//el nuevo formato (con grupos) es:
		//---header---|--------------------------usuario 1----------------------|
		//                  |--header---|----------grupo 1--------------|---grupo 2---|
		//im[a/o] k n_users u1 n_grupos n_terms_g1 t1 v1 t2 v2... tn vn n_terms_g2... u2 n_grupos...
		cout<<"ServidorConsultas::consultar - multiusuario\n";
		int user_id;
		int n_terms;
		float valor;
		int n_grupos;
		//k
		tok=strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1 || atoi(tok)==0 || atoi(tok)>100){
			cout<<"ServidorConsultas::consultar - Error al leer k\n";
			salida[0]=0;
			return 0;
		}
		k=atoi(tok);
		//n_users
		tok=strtok(NULL, " ");
		if(tok==NULL || strlen(tok)<1 || atoi(tok)==0 || atoi(tok)>100){
			cout<<"ServidorConsultas::consultar - Error al leer n_users\n";
			salida[0]=0;
			return 0;
		}
		n_usuarios=atoi(tok);
		pares=new pair<string, float>**[n_usuarios];
		usuarios=new int[n_usuarios];
		n_grupos_usuario=new int[n_usuarios];
		n_terms_usuario=new int*[n_usuarios];
		//cout<<"ServidorConsultas::consultar - iterando por "<<n_usuarios<<" usuarios\n";
		for(int i=0; i<n_usuarios; i++){
			//user_id
			tok=strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorConsultas::consultar - Error al leer user_id\n";
				salida[0]=0;
				return 0;
			}
			user_id=atoi(tok);
			usuarios[i]=user_id;
			//cout<<"ServidorConsultas::consultar - user "<<usuarios[i]<<"\n";
			
			//n_atributos
			tok=strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorConsultas::consultar - Error al leer n_atributos\n";
				salida[0]=0;
				return 0;
			}
			n_atributos=atoi(tok);
			for(int j=0; j<n_atributos; j++){
				tok=strtok(NULL, " ");
				if(tok==NULL || strlen(tok)<1){
					cout<<"ServidorConsultas::consultar - Error al leer atributo\n";
					salida[0]=0;
					return 0;
				}
				mapa_atributos[user_id].push_back(string(tok));
			}
			
			//n_grupos
			tok=strtok(NULL, " ");
			if(tok==NULL || strlen(tok)<1){
				cout<<"ServidorConsultas::consultar - Error al leer n_grupos\n";
				salida[0]=0;
				return 0;
			}
			n_grupos=atoi(tok);
			pares[i]=new pair<string, float>*[n_grupos];
			n_grupos_usuario[i]=n_grupos;
			n_terms_usuario[i]=new int[n_grupos];
			//cout<<"ServidorConsultas::consultar - n_grupos "<<n_grupos_usuario[i]<<"\n";
			
			for(int j=0; j<n_grupos; j++){
				
				//n_terms
				tok=strtok(NULL, " ");
				if(tok==NULL || strlen(tok)<1){
					cout<<"ServidorConsultas::consultar - Error al leer n_terms\n";
					salida[0]=0;
					return 0;
				}
				n_terms=atoi(tok);
				pares[i][j]=new pair<string, float>[n_terms];
				n_terms_usuario[i][j]=n_terms;
				//cout<<"ServidorConsultas::consultar - n_terms "<<n_terms_usuario[i][j]<<"\n";
				
				for(int k=0; k<n_terms; k++){
					//term
					tok=strtok(NULL, " ");
					if(tok==NULL || strlen(tok)<1){
						cout<<"ServidorConsultas::consultar - Error al leer termino\n";
						salida[0]=0;
						return 0;
					}
					string s(tok);
					//valor
					tok=strtok(NULL, " ");
					if(tok==NULL || strlen(tok)<1){
						cout<<"ServidorConsultas::consultar - Error al leer valor\n";
						salida[0]=0;
						return 0;
					}
					valor=atof(tok);
					//ingresar al mapa
//					mapa_consulta[user_id].push_back(pair<string, float>(s, valor));
					pares[i][j][k].first=s;
					pares[i][j][k].second=valor;
				}//for... cada termino del grupo
				
			}//for... cada grupo del usuario
			
		}//for... cada usuario
		
	}
	else{
		cout<<"ServidorConsultas::consultar - Comando desconocido ("<<tok<<")\n";
		salida[0]=0;
		return 0;
		
	}
	
	cout<<"ServidorConsultas::consultar - Buscando para construir resultado\n";
	//busqueda generica multiusuario
	list< pair<int, float> > *list_res=NULL;
	list< pair<int, float> >::iterator it_res;
	map<int, float> mapa_res_temp;
	map<int, float>::iterator it_res_temp;
	
	//int *indices (indice actual de cada par)
	//int *n_indice (total de terminos en cada grupo de pares)
	//int n_indices (total de grupos)
	//int mod (indice siendo modificado actualmente)
	
	int *indices;
	int *n_indice;
	int n_indices;
	int mod;
	
	list< pair<string, float> > *lista_q=new list< pair<string, float> >();
	
	//pair<string, float> **pares por usuario
	
	bool salir;
	
	for(int i=0; i<n_usuarios; i++){
		user_id=usuarios[i];
		
		//omitir iteracion si no hay grupos
		if(n_grupos_usuario[i]==0){
			cout<<"ServidorConsultas::consultar - Omitiendo consulta de usuario "<<user_id<<" (consulta vacia)\n";
			continue;
		}
		
		n_indice=n_terms_usuario[i];
		n_indices=n_grupos_usuario[i];
		indices=new int[n_indices];
		for(int j=0; j<n_indices; j++){
			indices[j]=0;
		}
		mod=0;
		salir=false;
		
		while(true){
		
			//cout<<"indices: "<<indices[0]<<", "<<indices[1]<<", i: "<<i<<"\n";
			
			//prepara consulta con los terminos actuales
			for(int j=0; j<n_indices; j++){
				lista_q->push_back(pares[i][j][indices[j]]);
			}
			
			//realizar consulta
			if(consulta_or){
				cout<<"1\n";
				list_res=rankeador->consultar(user_id, lista_q, &(mapa_atributos[user_id]));
				cout<<"2\n";
			}
			else{
				list_res=rankeador->consultarAnd(user_id, lista_q, &(mapa_atributos[user_id]));
			}
			//cout<<"consulta terminada con "<<list_res->size()<<" resultados\n";
			lista_q->clear();
			for(it_res=list_res->begin(); it_res!=list_res->end(); it_res++){
				doc_id=it_res->first;
				valor=it_res->second;
				if( mapa_res_temp.find(doc_id)==mapa_res_temp.end() 
					|| ( mapa_res_temp.find(doc_id)!=mapa_res_temp.end() && valor > mapa_res_temp[doc_id]) ){
					mapa_res_temp[doc_id]=valor;
				}
			}
			list_res->clear();
			delete list_res;
			
			//indices y condicion de salida
			//cout<<"actualizando indices\n";
			indices[mod]++;
			while(indices[mod]>=n_indice[mod]){
				indices[mod]=0;
				mod++;
				if(mod>=n_indices){
					salir=true;
					break;
				}
				indices[mod]++;
			}
			if(salir){
				break;
			}
			
		}//while... compara todos los grupos con todos
		
		delete [] indices;
		
	}//for... cada usuario
	
	delete lista_q;
	
	//cout<<"ServidorConsultas::consultar - Ordenando resultados\n";
	//ordenar los datos por puntaje (estan en mapa de des-duplicacion)
	list_res=new list< pair<int, float> >();
	for(it_res_temp=mapa_res_temp.begin(); it_res_temp!=mapa_res_temp.end(); it_res_temp++){
		list_res->push_back( pair<int, float>(it_res_temp->first, it_res_temp->second) );
	}
	list_res->sort(comp);
	
	//cout<<"ServidorConsultas::consultar - Escribiendo resultados\n";
	//escribir respuesta
	salida[0]=0;
	
	//total de resultados rankeados (distinto si hay o no grupos en el servicio)
	sprintf(salida, "%d ", (int)(list_res->size()));
	
	int contador=0;
	for(it_res=list_res->begin(); it_res!=list_res->end(); it_res++){
		cout<<"res["<<contador<<"]: "<<it_res->first<<", "<<it_res->second<<"\n";
		sprintf(salida+strlen(salida), "%d ", it_res->first);
		if(++contador == k){
			break;
		}
	}
	
	//cout<<"ServidorConsultas::consultar - Borrando estructuras\n";
	list_res->clear();
	delete list_res;
	
	if(pares!=NULL){
		for(int i=0; i<n_usuarios; i++){
			for(int j=0; j<n_grupos_usuario[i]; j++){
				delete [] pares[i][j];
			}
			delete [] pares[i];
		}
		delete [] pares;
	}
	if(n_terms_usuario!=NULL){
		for(int i=0; i<n_usuarios; i++){
			delete [] n_terms_usuario[i];
		}
		delete [] n_terms_usuario;
	}
	if(n_grupos_usuario!=NULL){
		delete n_grupos_usuario;
	}
	if(usuarios!=NULL){
		delete [] usuarios;
	}
	
	cout<<"ServidorConsultas::consultar - fin ("<<contador<<" resultados)\n";
	return strlen(salida);
	
}







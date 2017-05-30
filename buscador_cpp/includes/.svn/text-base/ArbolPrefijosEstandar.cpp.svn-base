#include <ArbolPrefijosEstandar.h>

ArbolPrefijosEstandar::ArbolPrefijosEstandar(){
if(debug) cout<<"ArbolPrefijosEstandar - inicio (vacio)\n";
	raiz = NULL;
	buffer= NULL;	
	max_palabra=0;	
}

ArbolPrefijosEstandar::ArbolPrefijosEstandar(list<string*> *lista){
if(debug) cout<<"ArbolPrefijosEstandar - inicio (lista con "<<lista->size()<<" string)\n";
	set<string> aux;
	max_palabra=0;
	for(list<string*>::iterator it=lista->begin();it!=lista->end();it++){
		aux.insert((*(*it)));
		if((*it)->size()>max_palabra){
			max_palabra=(*it)->size();
		}
	}
	buffer=new char[max_palabra+1];
if(debug) cout<<"ArbolPrefijosEstandar - creando raiz con "<<(aux.size())<<" palabras...\n";
	//raiz=new NodoEstandar('\0', aux);
	raiz=new NodoEstandar();
	raiz->setLetra('\0');
	raiz->setHijos(aux);
	aux.clear();		
}

ArbolPrefijosEstandar::ArbolPrefijosEstandar(list<char*> *lista){
if(debug) cout<<"ArbolPrefijosEstandar - inicio (lista con "<<lista->size()<<" char*)\n";
	set<string> aux;
	max_palabra=0;
	for(list<char *>::iterator it=lista->begin();it!=lista->end();it++){
		string s(*it);
		aux.insert(s);
		if(s.size()>max_palabra){
			max_palabra=s.size();
		}
	}
	buffer = new char[max_palabra+1];
	
	//raiz=new NodoEstandar('\0', aux);
	raiz=new NodoEstandar();
	raiz->setLetra('\0');
	raiz->setHijos(aux);
	aux.clear();			
}

ArbolPrefijosEstandar::~ArbolPrefijosEstandar(){
	if(raiz!=NULL){
		delete raiz;
		raiz=NULL;
	}
	if(buffer!=NULL){
		delete [] buffer;
		buffer=NULL;
	}
	max_palabra=0;
}

void ArbolPrefijosEstandar::print(){
	if(raiz==NULL){
		cout<<"raiz es null\n";
	}
	else{
		unsigned char max_childs=raiz->numHijos();
		for(unsigned char j=0; j<max_childs;j++){
			raiz->getHijo(j)->print(buffer, 0);
		}
	}
}

void ArbolPrefijosEstandar::consultar(const char* pal, list<string> *res){
if(debug) cout<<"ArbolPrefijosEstandar::consultar - inicio ("<<pal<<")\n";
	int largo=strlen(pal);
	//búscando la pal
	NodoEstandar *aux=raiz->getNodoPal(pal,largo);
	bool es_palabra= false;
	if(aux!=NULL){
		//if(aux->getType()=='H')
		if(aux->numHijos()==0){
			es_palabra=true;
			string aux(pal);
if(debug) cout<<"ArbolPrefijosEstandar::consultar - Agregando Hoja "<<aux<<"\n";
			res->push_back(aux);
			return;
		}
		es_palabra=aux->esPalabra();
		if(es_palabra){
			string aux(pal);
if(debug) cout<<"ArbolPrefijosEstandar::consultar - Agregando Nodo "<<aux<<"\n";
			res->push_back(aux);
		}
if(debug) cout<<"Buscando hijos...\n";
		//buscar si hay más palabras dado que se sabe que es un nodo interno
		int dis_sig_pal=aux->disMinSigPal();
if(debug) cout<<"distancia siguiente palabra = "<<dis_sig_pal<<"\n";
		//armando el char * buffer
		for(int i= 0; i<largo;i++){
			buffer[i]=pal[i];
		}
		//en buffer se tiene el coienzo de la palabra
		
		//se busca por todos los hijos las palabras coinsidentes
		unsigned int max_childs=aux->numHijos();
		for(unsigned int j=0; j<max_childs;j++){
			aux->getHijo(j)->printEnList(buffer, largo, res, dis_sig_pal+largo);
		}
if(debug) cout<<"ArbolPrefijosEstandar::consultar - fin ("<<res->size()<<" resultados)\n";							
	}
	else{
if(debug) cout<<"ArbolPrefijosEstandar::consultar - fin (nodo no encontrado, "<<res->size()<<" resultados)\n";
		return;
	}
}

void ArbolPrefijosEstandar::consultar(const char *palabra, list<string> *res, unsigned int k){
	if(raiz==NULL){
if(debug) cout<<"ArbolPrefijosEstandar::consultar - raiz nula\n";
		return;
	}
	NodoEstandar *nodo=raiz->getNodoPal(palabra, 0);
	if(nodo==NULL){
if(debug) cout<<"ArbolPrefijosEstandar::consultar - nodo ("<<palabra<<") no encontrado\n";
		return;
	}
	set< pair<int, string> > res_nodos;
	set< pair<int, string> >::iterator it_res;
	strcpy(buffer, palabra);
	//el -1 es pues el "nodo" comienza escribiendose a si mismo (y ya fue escrito)
	nodo->consultar(buffer, strlen(palabra)-1, &res_nodos, k);
	for(it_res=res_nodos.begin(); it_res!=res_nodos.end(); it_res++){
if(debug) cout<<"ArbolPrefijosEstandar::consultar - agregando "<<(it_res->second)<<"\n";
		res->push_back(it_res->second);
	}
	res_nodos.clear();
}





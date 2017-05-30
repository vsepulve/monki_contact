
#include <NodoEstandar.h>

NodoEstandar::NodoEstandar(){
//if(debug) cout<<"NodoEstandar - inicio (vacio)\n";
	letra = '\0';
	//pal=false;
	hijos=NULL;
	n_hijos=0;
}

NodoEstandar::NodoEstandar(char _letra){
//if(debug) cout<<"NodoEstandar - inicio (letra: "<<_letra<<")\n";
	letra = _letra;
	//pal=false;
	hijos=NULL;
	n_hijos=0;
}

void NodoEstandar::setLetra(char _letra){
	letra=_letra;
}

void NodoEstandar::setHijos(set<string> &palabras_hijos){
//if(debug) cout<<"NodoEstandar::setHijos - inicio (set con "<<palabras_hijos.size()<<" palabras)\n";
	
	int ld=0;
	char let='\0';
	for(set<string>::iterator it = palabras_hijos.begin(); it!=palabras_hijos.end(); it++){
		if(let!=(*it)[0]){
			ld++;
			let=(*it)[0];
		}
	}
	//número de hijos
	//esta limitacion es pues estamos usando el primer bit de n_hijos como marca es_palabra
	if(ld>127){
		cout<<"NodoEstandar - Demasiados hijos ("<<ld<<" con maximo 127)\n";
		ld=127;
	}
	setNumHijos(ld);
//if(debug) cout<<"NodoEstandar - creando arreglo con "<<(int)numHijos()<<" hijos\n";
	hijos=new NodoEstandar[numHijos()];
	
	let = (*(palabras_hijos.begin()))[0];
	set <string> aux;
	ld=0;
	bool hijo_es_palabra=false;
//if(debug) cout<<"NodoEstandar::setHijos - creando hijos...\n";
	for(set<string>::iterator it=palabras_hijos.begin(); it!=palabras_hijos.end() && ld<numHijos(); it++){
//if(debug) cout<<"probando "<<let<<" con "<<(*it)<<"\n";
		if(let!=(*it)[0]){
//if(debug) cout<<"creando nodo para "<<let<<"\n";
			//creo el hijo correspondiente
			if(aux.size()!=0){
//if(debug) cout<<"NodoEstandar::setHijos - creando hijo "<<let<<" con "<<aux.size()<<" palabras\n";
				//hijos[ld]=NodoEstandar(let, aux);
				hijos[ld].setLetra(let);
				hijos[ld].setHijos(aux);
				if(hijo_es_palabra){
					hijos[ld].setPalabra(true);
				}
				hijo_es_palabra=false;
//if(debug) cout<<"NodoEstandar::setHijos - (esPalabra?: "<<hijos[ld].esPalabra()<<")\n";
				ld++;
			}
			else{
//if(debug) cout<<"NodoEstandar::setHijos - creando hijo "<<let<<" hoja (sin hijos)\n";
				//hijos[ld]=NodoHojaEstandar(let);
				//hijos[ld]=NodoEstandar(let);
				hijos[ld].setLetra(let);
				hijos[ld].setPalabra(true);
				hijo_es_palabra=false;
//if(debug) cout<<"NodoEstandar::setHijos - (esPalabra?: "<<hijos[ld].esPalabra()<<")\n";
				ld++;
			}
//if(debug) cout<<"nodo "<<let<<" creado\n";
			aux.clear();
			if((it->substr(1)).size()!=0){
				aux.insert(it->substr(1));
			}
			else{
				hijo_es_palabra=true;
			}
			let=(*it)[0];
//if(debug) cout<<"letra cambiada a "<<let<<"\n";
		}
		else{
//if(debug) cout<<"misma letra, agregando ("<<it->substr(1)<<")\n";
			if((it->substr(1)).size()!=0){
				aux.insert(it->substr(1));
			}
			else{
				hijo_es_palabra=true;
			}
//if(debug) cout<<"agregado\n";
		}
	}
	if(aux.size()!=0){
//if(debug) cout<<"NodoEstandar::setHijos - creando hijo final "<<let<<" con "<<aux.size()<<" palabras\n";
		//hijos[ld]=NodoEstandar(let, aux);
		hijos[ld].setLetra(let);
		hijos[ld].setHijos(aux);
		if(hijo_es_palabra){
			hijos[ld].setPalabra(true);
		}
		hijo_es_palabra=false;
//if(debug) cout<<"NodoEstandar::setHijos - (esPalabra?: "<<hijos[ld].esPalabra()<<")\n";
		ld++;
	}
	else{
//if(debug) cout<<"NodoEstandar::setHijos - creando hijo final "<<let<<" hoja (sin hijos)\n";
		//hijos[ld]=NodoHojaEstandar(let);
		//hijos[ld]=NodoEstandar(let);
		hijos[ld].setLetra(let);
		hijos[ld].setPalabra(true);
//if(debug) cout<<"NodoEstandar::setHijos - (esPalabra?: "<<hijos[ld].esPalabra()<<")\n";
		ld++;
	}
	aux.clear();
}

void NodoEstandar::setPalabra(bool _pal){
	if(_pal){
		n_hijos |= 0x80;
	}
	else{
		n_hijos &= 0x7f;
	}
//	pal=_pal;
}

NodoEstandar* NodoEstandar::getHijo(unsigned char pos){
	NodoEstandar *r=NULL;
	if(pos < numHijos()){
		r=&(hijos[pos]);
	}
	return r;
}

char NodoEstandar::getLetra(){
	return letra;
}

void NodoEstandar::print(char * buff,int i){
	buff[i]=getLetra();
	if(esPalabra()){
		buff[i+1]='\0';
		cout<<buff<<"\n";
	}
	for(int j=0; j<numHijos();j++){
		this->getHijo(j)->print(buff,i+1);
	}
}

void NodoEstandar::printEnList(char *buff, int i, list<string> *res, int max_level){
//	cout<< " NodoEstandar::printEnList - inicio (i: "<<i<<", max_levels: "<<max_level<<", letra: "<<letra<<", es_pal: "<<pal<<")\n";
	if(i>=max_level){
		return;
	}
	else if(i==max_level-1){
		buff[i]=getLetra();
		if(esPalabra()){
			buff[i+1]='\0';
//			cout<<"NodoEstandar::printEnList - Agregando "<<(buff)<<"\n";
			res->push_back(string(buff));
		}
		
	}
	else{
		buff[i]=getLetra();
		if(esPalabra()){
			buff[i+1]='\0';
//			cout<<"NodoEstandar::printEnList - Agregando "<<(buff)<<"\n";
			res->push_back(string(buff));
		}
		for(int j=0; j<numHijos();j++){
			getHijo(j)->printEnList(buff, i+1, res, max_level);
		}
	}

}

void NodoEstandar::consultar(char *buff, unsigned int nivel, set< pair<int, string> > *res, unsigned int k){
if(debug) cout<<"NodoEstandar::consultar - inicio (nodo "<<getLetra()<<")\n";
	
	set< pair<int, string> >::reverse_iterator ultimo=res->rbegin();
	if(res->size()==k && nivel+1 > (ultimo->second).size()){
if(debug) cout<<"NodoEstandar::consultar - terminando por largo ("<<nivel<<" >= "<<(ultimo->second).size()<<")\n";
		return;
	}
	
	buff[nivel]=getLetra();
	buff[nivel+1]=0;
	
	//si este nodo es palabra, se agrega
	if(esPalabra()){
		if(res->size()<k){
if(debug) cout<<"NodoEstandar::consultar - agregando "<<buff<<" por numero elementos ("<<res->size()<<" < "<<k<<")\n";
			res->insert( pair<int, string>(strlen(buff), string(buff)) );
		}
		else{
			//res tiene al menos k elementos
if(debug) cout<<"NodoEstandar::consultar - set con "<<res->size()<<" elementos\n";
//				set< pair<int, string> >::reverse_iterator ultimo=res->rbegin();
			if(nivel+1 < (ultimo->second).size()){
if(debug) cout<<"NodoEstandar::consultar - agregando por largos "<<(nivel+1)<<" < "<<(ultimo->second).size()<<"\n";
if(debug) cout<<"NodoEstandar::consultar - borrando "<<(ultimo->second)<<"\n";
				//truco estupido pues el erase no recibe reverse_iterator
				//maxima falla de la stl
				res->erase( --ultimo.base() );
if(debug) cout<<"NodoEstandar::consultar - agregando "<<buff<<"\n";
				res->insert( pair<int, string>(strlen(buff), string(buff)) );
			}
		}
	}
	
if(debug) cout<<"NodoEstandar::consultar - revisando "<<(int)numHijos()<<" hijos...\n";
	//repetir recursivamente en los hijos
	for(int i=0; i<numHijos(); i++){
		getHijo(i)->consultar(buff, nivel+1, res, k);
	}
	
}
	
NodoEstandar::~NodoEstandar(){
//	cout<<"NodoEstandar::~NodoEstandar\n";
	//pal=false;
	if(hijos!=NULL){
//		for(int i=0;i<(int)numHijos();i++){
//			delete hijos[i];
//		}
		delete [] hijos;
		hijos=NULL;
	}
	n_hijos=0;
}

NodoEstandar* NodoEstandar::getNodoPal(const char *pal, int largopal){
	//buscar si dentro de los hijos esta la primera letra de pal

	//si largopal es positivo (largopal no puede ser negativo)
	//if(largopal)
	if(strlen(pal)){
		//revisar si la primera letra de pal esta dentro de los hijos
		
		unsigned char ini=0;
		unsigned char fin = numHijos()-1;
		unsigned char m = 0; 
		char caracter_a_buscar = pal[0];
		while(ini < fin){
			m=ini + ((fin-ini)/2);
			if(getHijo(m)->getLetra() < caracter_a_buscar){
				ini=m+1;
			}
			else{
				fin=m;
			}
		}
		
		//reviso que la letra sea la correcta
		if(getHijo(ini)->getLetra()==caracter_a_buscar){
			//veo si es la ultima letra de la palabra
			//if(largopal==1)
			if(strlen(pal)==1){
				return getHijo(ini);
			}
			else{
				//reviso si no llegue a una hoja
				//if(getHijo(ini)->getType()=='I')
				if(getHijo(ini)->numHijos() > 0){
					return getHijo(ini)->getNodoPal(pal+1,largopal-1);
				}
				else{
					return NULL;
				}
			}
		}
		else{
			return NULL;
		}
		
	}						
	return NULL;
}

unsigned char NodoEstandar::numHijos(){
	//return n_hijos;
	return n_hijos & 0x7f;
}

void NodoEstandar::setNumHijos(unsigned char _n_hijos){
	n_hijos |= _n_hijos & 0x7f;
}
	
int NodoEstandar::disMinSigPal(){
//if(debug) cout<<"NodoEstandar::disMinSigPal - inicio ("<<(int)numHijos()<<" hijos)\n";
//	if(getHijo(0)->getType()=='H'){
	if(getHijo(0)->numHijos()==0){
//if(debug) cout<<"NodoEstandar::disMinSigPal - hijo 0 es hoja\n";
		return 1;
	}
//	if(((NodoEstandar*)getHijo(0))->esPalabra()){
	if(getHijo(0)->esPalabra()){
//if(debug) cout<<"NodoEstandar::disMinSigPal - hijo 0 es nodo palabra\n";
		return 1;
	}
//if(debug) cout<<"NodoEstandar::disMinSigPal - hijo 0 no es palabra, buscando los demas...\n";
	
	int dist_min=1 + getHijo(0)->disMinSigPal();
	for(unsigned int i=1; i<numHijos(); i++){
//		if(getHijo(i)->getType()=='H')
		if(getHijo(i)->numHijos()==0){
			return 1;
		}
		else if(getHijo(i)->esPalabra()){
			return 1;
		}
		int dist_aux=1 + getHijo(i)->disMinSigPal();
		if(dist_min>dist_aux){
			dist_min=dist_aux;
		}
	}
	
	return dist_min;			
}

bool NodoEstandar::esPalabra(){
	return (n_hijos & 0x80)>0;
//	return pal;
}

//char NodoEstandar::getType(){
//	return 'I';
//}


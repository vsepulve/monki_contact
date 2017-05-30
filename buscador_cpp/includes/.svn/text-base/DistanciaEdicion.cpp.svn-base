
#include "DistanciaEdicion.h"

inline int DistanciaEdicion::min(int i1, int i2, int i3){
	if(i1<i2){
		if(i1<i3){
			return i1;
		}
		else{
			return i3;
		}
	}
	else{
		if(i2<i3){
			return i2;
		}
		else{
			return i3;
		}
	}
}

DistanciaEdicion::DistanciaEdicion(){
	max_palabra=255;
	m=new int*[max_palabra+1];
	for(int i=0; i<max_palabra+1; i++){
		m[i]=new int[max_palabra+1];
	}
}

DistanciaEdicion::DistanciaEdicion(int _max_palabra){
	max_palabra=_max_palabra;
	m=new int*[max_palabra+1];
	for(int i=0; i<max_palabra+1; i++){
		m[i]=new int[max_palabra+1];
	}
}

DistanciaEdicion::~DistanciaEdicion(){
	if(m!=NULL){
		for(int i=0; i<max_palabra+1; i++){
			if(m[i]!=NULL){
				delete [] m[i];
			}
		}
		delete [] m;
		m=NULL;
	}
}

double DistanciaEdicion::d(Dato &d1, Dato &d2){
	//verifricacion de tipos
	if( typeid(d1)!=typeid(DatoTexto) || typeid(d2)!=typeid(DatoTexto) ){
		printf("(DistanciaEdicion::d) - Ambos objetos deben ser DatoTexto\n");
		return DBL_MAX;
	}
	DatoTexto *p1, *p2;
	p1=dynamic_cast<DatoTexto*>(&d1);
	p2=dynamic_cast<DatoTexto*>(&d2);
	//datos previos
	contador++;
	double r=0.0;
	//preparar la matriz
	int n1=p1->n;
	int n2=p2->n;
	int n_max;
	if(n1>n2)
		n_max=n1;
	else
		n_max=n2;
	if(n_max>max_palabra){
		cout<<"(DistanciaLCSS::d) - Palabra mayor al maximo ("<<n_max<<" de "<<max_palabra<<"), recreando matriz...\n";
		if(m!=NULL){
			for(int i=0; i<max_palabra+1; i++){
				if(m[i]!=NULL){
					delete [] m[i];
					}
			}
			delete [] m;
		}
		max_palabra=n_max;
		m=new int*[max_palabra+1];
		for(int i=0; i<max_palabra+1; i++){
			m[i]=new int[max_palabra+1];
		}
	}
	
	for(int i=0; i<=n1; i++){
		m[i][0]=i;
	}
	for(int j=0; j<=n2; j++){
		m[0][j]=j;
	}
	//generar matriz de costo
	int costo;
	for(int j=1; j<=n2; j++){
		for(int i=1; i<=n1; i++){
			if((p1->s[i-1]) == (p2->s[j-1])){
				costo=0;
			}
			else{
				costo=1;
			}
			//insertar, borrar, reemplazar
			m[i][j]=min(m[i-1][j]+1, 
					m[i][j-1]+1, 
					m[i-1][j-1]+costo);
		}
	}
	r=m[n1][n2];
	if(normalizar){
		//factor=...
		r/=factor;
	}
	return r;
}


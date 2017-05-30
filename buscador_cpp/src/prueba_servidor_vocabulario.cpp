
#include <stdlib.h>
#include <iostream>

#include "ServidorVocabulario.h"

using namespace std;

int main(int argc, char* argv[]){
	
	ServidorVocabulario *servidor=new ServidorVocabulario("./3.ii.txt");
	
	int user_id, k, n_terms, largo_res;
	int max_linea=1024;
	char *consulta=new char[max_linea];
	char *respuesta=new char[max_linea];
	
//	sprintf(consulta, "5 marcelo iturbe ");	
//	largo_res=servidor->consultar(consulta, respuesta);
//	cout<<"largo_res: "<<largo_res<<"\n";
//	cout<<"res: "<<respuesta<<"\n";
	
	//La consulta debe tener el siguiente formato:
	//"user_id t1 t2... tn "
	//donde:
	// - user_id es el usuario, por ejemplo "5"
	// - ti es el i-esimo termino, por ejemplo "marcelo" o "iturbe"
	
	cout<<"\n";
	cout<<"Ingrese consulta (\"user_id t1 t2 t3... \")\n";
	cout<<"Ejemplo: 5 marcelo iturbe \n";
	cout<<">q (o quit) para salir\n";
	cout<<"\n";
	while(true){
		
		cout<<">";
		cin.getline(consulta, max_linea);
		if(strcmp(consulta, "q")==0 || strcmp(consulta, "quit")==0){
			cout<<"Saliendo...\n";
			break;
		}
		
		cout<<"Consulta: "<<consulta<<"\n";
		largo_res=servidor->consultar(consulta, respuesta);
		cout<<"largo_res: "<<largo_res<<"\n";
		cout<<"res: "<<respuesta<<"\n";
		
		cout<<"\n";
	}
	
	delete [] consulta;
	delete [] respuesta;
	
	delete servidor;
	
}




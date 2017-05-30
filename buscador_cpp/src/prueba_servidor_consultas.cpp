
#include <stdlib.h>
#include <iostream>

#include "ServidorConsultas.h"

using namespace std;

int main(int argc, char* argv[]){
	
	ServidorConsultas *servidor=new ServidorConsultas("./3.ii.txt");
	
	int user_id, k, n_terms, largo_res;
	int max_linea=1024;
	char *consulta=new char[max_linea];
	char *respuesta=new char[max_linea];
	
//	sprintf(consulta, "5 10 2 marcelo iturbe 2 1 ");	
//	largo_res=servidor->consultar(consulta, respuesta);
//	cout<<"largo_res: "<<largo_res<<"\n";
//	cout<<"res: "<<respuesta<<"\n";
	
	//La consulta debe tener el siguiente formato:
	//"user_id k n_terms t1 t2... tn v1 v2... vn "
	//donde:
	// - user_id es el usuario, por ejemplo "5"
	// - k es el numero de resultados esperados, por ejemplo "10"
	// - n_terms es el numero total de terminos, por ejemplo "2"
	// - ti es el i-esimo termino, por ejemplo "marcelo" o "iturbe"
	// - vi es el i-esimo valor, el peso del i-esimo termino, por ejemplo "2" o "1"
	
	cout<<"\n";
	cout<<"Ingrese consulta (\"user_id k n_terms t1 t2 t3... v1 v2 v3... \")\n";
	cout<<"Ejemplo: 5 10 2 marcelo iturbe 2 1 \n";
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




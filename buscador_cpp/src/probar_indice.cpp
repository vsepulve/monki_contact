
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <fstream>

using namespace std;

int main(int argc, char* argv[]){
	
	if(argc!=2){
		cout<<"\n";
		cout<<"Modo de Uso:\n>./probar_indice ruta_indice\n";
		cout<<"Revisa el indice en busca de errores.\n";
		cout<<"Retorna 0 si no hay errores, o 1 si encuentra algun error (se pueden agregar codigos de error adicionales).\n";
		cout<<"\n";
		return 0;
	}
	
	const char *archivo_indice=argv[1];
	
	//lectura de prueba del indice
	//notar que esto deberia ser un algoritmo del indice mismo
	
	fstream lector(archivo_indice, fstream::in);
	int max_linea=10*1024*1024;
	char *linea=new char[max_linea];
	char *tok;
	
	int n_users, n_atributos, n_terms, n_docs;
	int user_id, doc_id;
	float valor;
	
	lector.getline(linea, max_linea);
	//cout<<"IndiceInvertidoAtributos::cargar - n_users (desde "<<linea<<")\n";
	if(linea==NULL || strlen(linea)<1 || atoi(linea)==0){
		cout<<"1\n";
		return 1;
	}
	n_users=atoi(linea);
	
	for(int i=0; i<n_users; i++){
		
		//user id
		lector.getline(linea, max_linea);
		//cout<<"IndiceInvertidoAtributos::cargar - user_id (desde "<<linea<<")\n";
		if(linea==NULL || strlen(linea)<1 || atoi(linea)==0){
			cout<<"1\n";
			return 1;
		}
		user_id=atoi(linea);
		
		//atributos
		lector.getline(linea, max_linea);
		//cout<<"IndiceInvertidoAtributos::cargar - n_atributos (desde "<<linea<<")\n";
		if(linea==NULL || strlen(linea)<1 || atoi(linea)==0){
			cout<<"1\n";
			return 1;
		}
		n_atributos=atoi(linea);
		
		for(int i_at=0; i_at<n_atributos; i_at++){
			
			lector.getline(linea, max_linea);
			//cout<<"IndiceInvertidoAtributos::cargar - atributo (desde "<<linea<<")\n";
			if(linea==NULL || strlen(linea)<1){
				cout<<"1\n";
				return 1;
			}
			string atributo(linea);
			
			//numero terms
			lector.getline(linea, max_linea);
			//cout<<"IndiceInvertidoAtributos::cargar - n_terms (desde "<<linea<<")\n";
			if(linea==NULL || strlen(linea)<1 || atoi(linea)==0){
				cout<<"1\n";
				return 1;
			}
			n_terms=atoi(linea);
		
			for(int j=0; j<n_terms; j++){
				lector.getline(linea, max_linea);
				if(linea==NULL || strlen(linea)<1){
					cout<<"1\n";
					return 1;
				}
				
				tok=strtok(linea, " \n\t");
				if(tok==NULL || strlen(tok)<1){
					cout<<"1\n";
					return 1;
				}
				string palabra(tok);
				
				tok=strtok(NULL, " \n\t");
				if(tok==NULL || strlen(tok)<1 || atoi(tok)==0){
					cout<<"1\n";
					return 1;
				}
				n_docs=atoi(tok);
				
				for(int k=0; k<n_docs; k++){
					
					tok=strtok(NULL, " \n\t");
					if(tok==NULL || strlen(tok)<1 || atoi(tok)==0){
						cout<<"1\n";
						return 1;
					}
					doc_id=atoi(tok);
					
					tok=strtok(NULL, " \n\t");
					if(tok==NULL || strlen(tok)<1 || atof(tok)==0.0){
						cout<<"1\n";
						return 1;
					}
					valor=atof(tok);
					
				}
				
			}//for... cada termino
			
		}//for... cada atributo
		
	}//for... cada usuario
	
	lector.close();
	//cout<<"IndiceInvertidoAtributos::cargar - lectura de users terminada\n";
	delete [] linea;
	
	cout<<"0\n";
	return 0;
	
}





#include <stdlib.h>
#include <iostream>

#include "ServidorVocabularioPrefijos.h"
#include "ServidorConsultas.h"

using namespace std;

//Funcion Tiempo (compilar con -lrt)
#include <time.h>
#include <sys/timeb.h>
void diff_time( timespec *t_fin, timespec *t_ini, timespec *delta )
{
  if( ( (*t_fin).tv_nsec - (*t_ini).tv_nsec ) < 0 )
  {
    if( (*t_fin).tv_sec == (*t_ini).tv_sec )
    {
      (*delta).tv_sec  = 0;
      (*delta).tv_nsec = 1000000000 + (*t_fin).tv_nsec - (*t_ini).tv_nsec;
    }
    else
    {
      (*delta).tv_sec  = (*t_fin).tv_sec - (*t_ini).tv_sec - 1;
      (*delta).tv_nsec = 1000000000 + (*t_fin).tv_nsec - (*t_ini).tv_nsec;
    }
  }
  else
  {
    if( (*t_fin).tv_sec == (*t_ini).tv_sec )
    {
      (*delta).tv_sec  = 0;
      (*delta).tv_nsec = (*t_fin).tv_nsec - (*t_ini).tv_nsec;
    }
    else
    {
      (*delta).tv_sec  = (*t_fin).tv_sec - (*t_ini).tv_sec;
      (*delta).tv_nsec = (*t_fin).tv_nsec - (*t_ini).tv_nsec;
    }
  }
}
//Fin Funcion Tiempo

int main(int argc, char* argv[]){
	
	if(argc!=3){
		cout<<"\n";
		cout<<"Modo de Uso:\n>./prueba_servidores_user ruta_indice user_id\n";
		cout<<"binario=0 si los archivos son de texto, binario=1 si los archivos son binarios\n";
		cout<<"\n";
		return 0;
	}
	
	timespec t_ini, t_fin, latencia;
	long long nano_completo;
	
	const char *archivo_indice=argv[1];
	int user_id=atoi(argv[2]);
	
	clock_gettime( CLOCK_REALTIME, &t_ini );
	
//	ServidorVocabulario *servidor_voc=new ServidorVocabulario(archivo_indice, binario);
	ServidorVocabularioPrefijos *servidor_voc=new ServidorVocabularioPrefijos(archivo_indice, true);
//	ServidorConsultas *servidor=new ServidorConsultas(archivo_indice, binario);
//	ServidorConsultas *servidor=new ServidorConsultas(archivo_indice, archivo_grupos, binario);
	ServidorConsultas *servidor=new ServidorConsultas(archivo_indice, true);
	
	//grupos
	//servidor->cargarGrupos(archivo_grupos, binario);
	
	int k, largo_res;
	int max_linea=1024;
	char *entrada=new char[max_linea];
	char *consulta=new char[max_linea];
	char *respuesta=new char[max_linea];
	
	k=10;
	
	clock_gettime( CLOCK_REALTIME, &t_fin );
	diff_time( &t_fin, &t_ini, &latencia );
	nano_completo=latencia.tv_sec*1000000000+latencia.tv_nsec;
		
	cout<<"Tiempo Inicio (mili): "<<((long double)nano_completo/1000000)<<"\n";
	
	
	cout<<"\n";
	cout<<"Ingrese consulta (\"term1 term2... \")\n";
	cout<<"Ejemplo: marcelo iturbe \n";
	cout<<">q (o quit) para salir\n";
	cout<<"\n";
	
	while(true){
		
		cout<<">";
		cin.getline(entrada, max_linea);
		if(strcmp(entrada, "q")==0 || strcmp(entrada, "quit")==0){
			cout<<"Saliendo...\n";
			break;
		}
		
		
		
		//preparar consulta a vocabulario
		sprintf(consulta, "m 5 1 %d ", user_id);
		
		list<string> lista;
		char *tok;
		tok=strtok(entrada, " ");
		while(tok!=NULL){
			lista.push_back(string(tok));
			tok=strtok(NULL, " ");
		}
		
		sprintf(consulta+strlen(consulta), "%d ", (int)(lista.size()));
		for(list<string>::iterator it=lista.begin(); it!=lista.end(); it++){
			sprintf(consulta+strlen(consulta), "%s ", it->c_str());
		}
		
		cout<<"Consulta voc: "<<consulta<<"\n";
		largo_res=servidor_voc->consultar(consulta, entrada);
		cout<<"respuesta voc: "<<entrada<<"\n";
		
		//preparar consulta final
//		sprintf(consulta, "%d %d %s ", user_id, k, entrada);
		sprintf(consulta, "m %d %s", k, entrada);
		
		cout<<"Consulta indice: "<<consulta<<"\n";
		largo_res=servidor->consultar(consulta, respuesta);
		cout<<"respuesta indice: "<<respuesta<<"\n";
		
		cout<<"\n";
	}
	
	delete [] consulta;
	delete [] respuesta;
	
	delete servidor_voc;
	
	delete servidor;
	
}




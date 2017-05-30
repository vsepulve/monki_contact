
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
	
	if(argc!=5){
		cout<<"\n";
		cout<<"Modo de Uso:\n>./prueba_servidores ruta_indice ruta_grupos binario user_id\n";
		cout<<"binario=0 si los archivos son de texto, binario=1 si los archivos son binarios\n";
		cout<<"\n";
		return 0;
	}
	
	timespec t_ini, t_fin, latencia;
	long long nano_completo;
	
	const char *archivo_indice=argv[1];
	const char *archivo_grupos=argv[2];
	bool binario=atoi(argv[3])>0;
	int user_id=atoi(argv[4]);
	
	clock_gettime( CLOCK_REALTIME, &t_ini );
	
//	ServidorVocabulario *servidor_voc=new ServidorVocabulario(archivo_indice, binario);
	ServidorVocabularioPrefijos *servidor_voc=new ServidorVocabularioPrefijos(archivo_indice, binario);
//	ServidorConsultas *servidor=new ServidorConsultas(archivo_indice, binario);
	ServidorConsultas *servidor=new ServidorConsultas(archivo_indice, archivo_grupos, binario);
	
	//grupos
	//servidor->cargarGrupos(archivo_grupos, binario);
	
	int k;
	int largo_res;
	int max_linea=1024;
	char entrada[max_linea];
	char consulta[max_linea];
	char respuesta[max_linea];
	
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
		sprintf(consulta, "m 8 1 %d 1 all ", user_id);
//		sprintf(consulta, "m 8 1 %d 4 name org edu location ", user_id);
//		sprintf(consulta, "m 8 1 %d ", user_id);
//		sprintf(consulta, "m 5 3 %d ", user_id);
		
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
		
//		sprintf(consulta+strlen(consulta), "33 ");
//		sprintf(consulta+strlen(consulta), "%d ", (int)(lista.size()));
//		for(list<string>::iterator it=lista.begin(); it!=lista.end(); it++){
//			sprintf(consulta+strlen(consulta), "%s ", it->c_str());
//		}
//		sprintf(consulta+strlen(consulta), "35 1 gmail");
		
		cout<<"Consulta voc: "<<consulta<<"\n";
		largo_res=servidor_voc->consultar(consulta, entrada);
		cout<<"respuesta voc: "<<entrada<<" ("<<largo_res<<")\n";
		
		//preparar consulta final
//		sprintf(consulta, "%d %d %s ", user_id, k, entrada);
//		sprintf(consulta, "m %d %s", k, entrada);
		sprintf(consulta, "m %d %s", k, entrada);
		
		cout<<"Consulta indice: "<<consulta<<"\n";
		largo_res=servidor->consultar(consulta, respuesta);
		cout<<"respuesta indice: "<<respuesta<<" ("<<largo_res<<")\n";
		
		cout<<"\n";
	}
	
	
	delete servidor_voc;
	
	delete servidor;
	
}




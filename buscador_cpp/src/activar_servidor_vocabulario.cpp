
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>

#include <stdlib.h>
#include <iostream>

#include <signal.h>

#include "ServidorVocabularioPrefijos.h"

using namespace std;

sig_atomic_t alarm_counter;

void alarm_handler(int signal) {
    alarm_counter++;
}

void setup_alarm_handler() {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_handler = alarm_handler;
    sa.sa_flags = 0;
    if (sigaction(SIGALRM, &sa, 0) < 0){
        cout<<"Can't establish signal handler\n";
        return;
    }
}
// call setup_alarm_handler in main

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

int limpiar_consulta(char *buff_entrada, int n_entrada, char *temp){
	//,;.:/-+@ _#
	char c;
	int largo_salida=0;
	for(int i=0; i<n_entrada; ++i){
		c=buff_entrada[i];
		if( ( c>='a' && c<='z' )
			|| ( c>='0' && c<='9' ) 
			|| c==',' || c==';' || c=='.' || c==':' 
			|| c=='/' || c=='-' || c=='+' || c=='@' 
			|| c==' ' || c=='_' || c=='#'){
			temp[largo_salida++]=c;
		}
	}
	temp[largo_salida]=0;
	memcpy(buff_entrada, temp, largo_salida);
	buff_entrada[largo_salida]=0;
	return largo_salida;
}

int main(int argc, char* argv[]){
	
	const char *ruta_log="./log_voc.txt";
	//const char *ruta_log="/root/logs/log_voc.txt";
	
	if(argc!=4){
		cout<<"\n";
		cout<<"Modo de Uso:\n>./servidor puerto_consulta ruta_indice binario\n";
		cout<<"binario=0 si los archivos son de texto, binario=1 si los archivos son binarios\n";
		cout<<"\n";
		return 0;
	}
	
	timespec t_ini, t_fin, latencia;
	timespec t_ini_q, t_fin_q;
	unsigned long long nano_completo, nano_q;
	
	setup_alarm_handler();
	
	time_t now;
	tm *ltm;
	
	clock_gettime( CLOCK_PROCESS_CPUTIME_ID, &t_ini );
	
	int puerto=atoi(argv[1]);
	const char *archivo_indice=argv[2];
	bool binario=atoi(argv[3])>0;
	
//	ServidorVocabulario *servidor=new ServidorVocabulario(archivo_indice, binario);
	ServidorVocabularioPrefijos *servidor=new ServidorVocabularioPrefijos(archivo_indice, binario);
	
	int sock_servidor, sock_cliente;
	socklen_t clilen;
	struct sockaddr_in serv_addr, cli_addr;
	int n;
	
	sock_servidor = socket(AF_INET, SOCK_STREAM, 0);
	if (sock_servidor < 0){
		cout<<"Error abriendo el socket\n";
		return 0;
	}
	memset((char*)&serv_addr, 0, sizeof(serv_addr));
	
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = INADDR_ANY;
	serv_addr.sin_port = htons(puerto);
	
	if (bind(sock_servidor, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0){
		cout<<"Error en binding\n";
		return 0;
	}
	listen(sock_servidor, 100);
	
	int max_bytes=2048;
	
	char *buff_entrada = new char[max_bytes];
	char *linea = new char[max_bytes];
	
	clock_gettime( CLOCK_PROCESS_CPUTIME_ID, &t_fin );
	diff_time( &t_fin, &t_ini, &latencia );
	nano_completo = latencia.tv_sec*1000000000+latencia.tv_nsec;
	
	cout<<"Tiempo Inicio (mili): "<<((long double)nano_completo/1000000)<<"\n";
	
	double milis_query, milis_total;
	
	while(true){
		
		milis_query = 0;
		milis_total = 0;
		
		cout<<"esperando conexion...\n";
		clilen = sizeof(cli_addr);
		sock_cliente = accept(sock_servidor, (struct sockaddr *)&cli_addr, &clilen);
		cout<<"conexion aceptada...\n";
		
		clock_gettime( CLOCK_PROCESS_CPUTIME_ID, &t_ini );
		
		if (sock_cliente < 0){
			cout<<"Error aceptando conexion\n";
			
			//preparar salida vacia
			sprintf(linea, "0 ");
			
		}//if... error en conectar
		else{
			alarm(1);
			n = read(sock_cliente, buff_entrada, max_bytes);
			alarm(0);
			
			if (n <= 0){
				buff_entrada[0] = 0;
				cout<<"Error de lectura\n";
				
				//preparar salida vacia
				sprintf(linea, "0 ");
				continue;
				
			}//if... error en lectura
			else{
				buff_entrada[n] = 0;
				n = limpiar_consulta(buff_entrada, n, linea);
				cout<<"Mensaje de entrada: "<<buff_entrada<<" (largo "<<n<<")\n";
				
				//log
				fstream salida_log(ruta_log, fstream::out | fstream::app);
				now = time(0);
				ltm = localtime(&now);
				sprintf(linea, "[%d-%.2d-%.2d %.2d:%.2d:%.2d] \"%s\" \n", (1900+ltm->tm_year), (1+ltm->tm_mon), (ltm->tm_mday), (1+ltm->tm_hour), (1+ltm->tm_min), (1+ltm->tm_sec), buff_entrada);
				salida_log.write(linea, strlen(linea));
				salida_log.close();
				linea[0] = 0;
				
				//condicion de salida
				if(strcmp(buff_entrada, "-q") == 0){
					cout<<"Saliendo...\n";
					break;
				}
				
				clock_gettime( CLOCK_PROCESS_CPUTIME_ID, &t_ini_q );
				
				//buscar resultados
				servidor->consultar(buff_entrada, linea);
				
				clock_gettime( CLOCK_PROCESS_CPUTIME_ID, &t_fin_q );
				diff_time( &t_fin_q, &t_ini_q, &latencia );
				nano_q = latencia.tv_sec*1000000000+latencia.tv_nsec;
				milis_query = ((double)(long double)nano_q/1000000);
				
				cout<<"milisec query: "<<milis_query<<"\n";
				
				cout<<"Mensaje de salida: "<<linea<<"\n";
				
			}//else... lectura ok
		}//else... coneccion ok
		
		n = write(sock_cliente, linea, strlen(linea));
		if (n < 0){
			cout<<"Error escribiendo\n";
		}
		close(sock_cliente);
		
		clock_gettime( CLOCK_PROCESS_CPUTIME_ID, &t_fin );
		diff_time( &t_fin, &t_ini, &latencia );
		nano_completo = latencia.tv_sec*1000000000+latencia.tv_nsec;
		milis_total = (double)((long double)nano_completo/1000000);
		
		cout<<"milisec total: "<<milis_total<<"\n";
		
		unsigned int n_res = strlen(linea);
//		char *tok = strtok(linea, " ");
//		if(tok != NULL && strlen(tok)>0){
//			n_res = atoi(tok);
//		}
		
		//log
		fstream salida_log(ruta_log, fstream::out | fstream::app);
		if( servidor->lastUpdate() ){
			sprintf(linea, "UPDATE USER %d QUERY %.2f TOTAL %.2f \n", servidor->lastUpdatedUser(), milis_query, milis_total);
		}
		else{
			sprintf(linea, "RESULT LENGTH %d QUERY %.2f TOTAL %.2f \n", n_res, milis_query, milis_total);
		}
		salida_log.write(linea, strlen(linea));
		salida_log.close();
		linea[0] = 0;
		
	}
	
	close(sock_servidor);
	
	delete [] buff_entrada;
	delete [] linea;
	
	delete servidor;
	
	return 0;
	
}




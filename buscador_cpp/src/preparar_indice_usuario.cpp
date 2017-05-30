
#include <stdlib.h>
#include <iostream>

#include <vector>
#include <list>

#include "IndiceInvertidoAtributos.h"

using namespace std;

int main(int argc, char* argv[]){
	
	if(argc!=5){
		cout<<"\n";
		cout<<"Modo de Uso:\n>./preparar_indice_usuario ruta_indice ruta_grupos user_id indice_salida\n";
		cout<<"Lee el indice y el archivo de grupos de texto, y genera el binario con los datos del usuario\n";
		cout<<"\n";
		return 0;
	}
	
	const char *archivo_indice=argv[1];
	const char *archivo_grupos=argv[2];
	int user_buscado=atoi(argv[3]);
	const char *archivo_indice_salida=argv[4];
	
	IndiceInvertidoAtributos *indice=new IndiceInvertidoAtributos();
	indice->cargar(archivo_indice, archivo_grupos);
	
	indice->guardarBinario(archivo_indice_salida, user_buscado);
	
}




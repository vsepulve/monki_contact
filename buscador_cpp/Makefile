SRCDIR = src
BINDIR = bin
DATADIR = data
LIBDIR = lib
INCLDIR = includes
CPPFLAGS = -O3 -Wall -I./includes/ -L./lib/
#MYSQLCFLAGS = $(mysql_config --cflags)
MYSQLCFLAGS = -I/usr/include/mysql  -DBIG_JOINS=1  -fno-strict-aliasing   -DUNIV_LINUX -DUNIV_LINUX
#MYSQLLIBS = $(mysql_config --libs)
MYSQLLIBS = -Wl,-Bsymbolic-functions -rdynamic -L/usr/lib/mysql -lmysqlclient

CPP = g++

all: prueba_servidores

# Programas

prueba_mysql: 
	$(CPP) $(MYSQLCFLAGS)  $(SRCDIR)/prueba_mysql.cpp -o $(BINDIR)/prueba_mysql $(MYSQLLIBS)

prueba_servidores: Dato DatoTexto Distancia DistanciaEdicion DistanciaEdicionPesos Indice IndiceInvertido IndiceInvertidoAtributos IndiceLineal IndicePivotesSimple ListaInvertida RankeadorBasico ServidorConsultas ServidorVocabularioPrefijos ArbolPrefijosEstandar NodoEstandar
	$(CPP) $(LIBDIR)/Dato.o $(LIBDIR)/DatoTexto.o $(LIBDIR)/Distancia.o $(LIBDIR)/DistanciaEdicion.o $(LIBDIR)/DistanciaEdicionPesos.o $(LIBDIR)/Indice.o $(LIBDIR)/IndiceInvertido.o $(LIBDIR)/IndiceInvertidoAtributos.o $(LIBDIR)/IndiceLineal.o $(LIBDIR)/IndicePivotesSimple.o $(LIBDIR)/ListaInvertida.o $(LIBDIR)/RankeadorBasico.o $(LIBDIR)/ServidorConsultas.o $(LIBDIR)/ServidorVocabularioPrefijos.o $(LIBDIR)/NodoEstandar.o $(LIBDIR)/ArbolPrefijosEstandar.o $(CPPFLAGS) $(SRCDIR)/prueba_servidores.cpp -o $(BINDIR)/prueba_servidores -lrt

prueba_servidores_user: Dato DatoTexto Distancia DistanciaEdicion DistanciaEdicionPesos Indice IndiceInvertido IndiceLineal IndicePivotesSimple ListaInvertida RankeadorBasico ServidorConsultas ServidorVocabularioPrefijos ArbolPrefijosEstandar NodoEstandar
	$(CPP) $(LIBDIR)/Dato.o $(LIBDIR)/DatoTexto.o $(LIBDIR)/Distancia.o $(LIBDIR)/DistanciaEdicion.o $(LIBDIR)/DistanciaEdicionPesos.o $(LIBDIR)/Indice.o $(LIBDIR)/IndiceInvertido.o $(LIBDIR)/IndiceLineal.o $(LIBDIR)/IndicePivotesSimple.o $(LIBDIR)/ListaInvertida.o $(LIBDIR)/RankeadorBasico.o $(LIBDIR)/ServidorConsultas.o $(LIBDIR)/ServidorVocabularioPrefijos.o $(LIBDIR)/NodoEstandar.o $(LIBDIR)/ArbolPrefijosEstandar.o $(CPPFLAGS) $(SRCDIR)/prueba_servidores_user.cpp -o $(BINDIR)/prueba_servidores_user -lrt

activar_servidor_consultas: Dato DatoTexto Distancia DistanciaEdicion DistanciaEdicionPesos Indice IndiceInvertidoAtributos IndiceLineal IndicePivotesSimple ListaInvertida RankeadorBasico ServidorConsultas
	$(CPP) $(LIBDIR)/Dato.o $(LIBDIR)/DatoTexto.o $(LIBDIR)/Distancia.o $(LIBDIR)/DistanciaEdicion.o $(LIBDIR)/DistanciaEdicionPesos.o $(LIBDIR)/Indice.o $(LIBDIR)/IndiceInvertidoAtributos.o $(LIBDIR)/IndiceLineal.o $(LIBDIR)/IndicePivotesSimple.o $(LIBDIR)/ListaInvertida.o $(LIBDIR)/RankeadorBasico.o $(LIBDIR)/ServidorConsultas.o $(CPPFLAGS) $(SRCDIR)/activar_servidor_consultas.cpp -o $(BINDIR)/activar_servidor_consultas -lrt

activar_servidor_vocabulario: Dato DatoTexto Distancia DistanciaEdicion DistanciaEdicionPesos Indice IndiceInvertidoAtributos IndiceLineal IndicePivotesSimple ListaInvertida RankeadorBasico ServidorConsultas ServidorVocabularioPrefijos ArbolPrefijosEstandar NodoEstandar
	$(CPP) $(LIBDIR)/Dato.o $(LIBDIR)/DatoTexto.o $(LIBDIR)/Distancia.o $(LIBDIR)/DistanciaEdicion.o $(LIBDIR)/DistanciaEdicionPesos.o $(LIBDIR)/Indice.o $(LIBDIR)/IndiceInvertidoAtributos.o $(LIBDIR)/IndiceLineal.o $(LIBDIR)/IndicePivotesSimple.o $(LIBDIR)/ListaInvertida.o $(LIBDIR)/RankeadorBasico.o $(LIBDIR)/ServidorConsultas.o $(LIBDIR)/ServidorVocabularioPrefijos.o $(LIBDIR)/NodoEstandar.o $(LIBDIR)/ArbolPrefijosEstandar.o $(CPPFLAGS) $(CPPFLAGS) $(SRCDIR)/activar_servidor_vocabulario.cpp -o $(BINDIR)/activar_servidor_vocabulario -lrt

prueba_datos: Dato DatoTexto 
	$(CPP) $(LIBDIR)/Dato.o $(LIBDIR)/DatoTexto.o $(CPPFLAGS) $(SRCDIR)/prueba_datos.cpp -o $(BINDIR)/prueba_datos

prueba_arbol_prefijos: ArbolPrefijosEstandar NodoEstandar 
	$(CPP) $(LIBDIR)/ArbolPrefijosEstandar.o $(LIBDIR)/NodoEstandar.o $(CPPFLAGS) $(SRCDIR)/prueba_arbol_prefijos.cpp -o $(BINDIR)/prueba_arbol_prefijos

preparar_indice_usuario: IndiceInvertidoAtributos ListaInvertida 
	$(CPP) $(LIBDIR)/IndiceInvertidoAtributos.o $(LIBDIR)/ListaInvertida.o $(CPPFLAGS) $(SRCDIR)/preparar_indice_usuario.cpp -o $(BINDIR)/preparar_indice_usuario

probar_indice:  
	$(CPP) $(CPPFLAGS) $(SRCDIR)/probar_indice.cpp -o $(BINDIR)/probar_indice

# Modulos

Dato:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/Dato.o $(INCLDIR)/Dato.cpp

DatoTexto:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/DatoTexto.o $(INCLDIR)/DatoTexto.cpp

Distancia:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/Distancia.o $(INCLDIR)/Distancia.cpp

DistanciaEdicion:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/DistanciaEdicion.o $(INCLDIR)/DistanciaEdicion.cpp

DistanciaEdicionPesos:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/DistanciaEdicionPesos.o $(INCLDIR)/DistanciaEdicionPesos.cpp

Indice:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/Indice.o $(INCLDIR)/Indice.cpp

IndiceInvertido:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/IndiceInvertido.o $(INCLDIR)/IndiceInvertido.cpp

IndiceInvertidoAtributos:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/IndiceInvertidoAtributos.o $(INCLDIR)/IndiceInvertidoAtributos.cpp

IndiceLineal:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/IndiceLineal.o $(INCLDIR)/IndiceLineal.cpp

IndicePivotesSimple:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/IndicePivotesSimple.o $(INCLDIR)/IndicePivotesSimple.cpp

ListaInvertida:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/ListaInvertida.o $(INCLDIR)/ListaInvertida.cpp

RankeadorBasico:
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/RankeadorBasico.o $(INCLDIR)/RankeadorBasico.cpp

ServidorConsultas: 
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/ServidorConsultas.o $(INCLDIR)/ServidorConsultas.cpp

ArbolPrefijosEstandar: 
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/ArbolPrefijosEstandar.o $(INCLDIR)/ArbolPrefijosEstandar.cpp

NodoEstandar: 
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/NodoEstandar.o $(INCLDIR)/NodoEstandar.cpp

ServidorVocabularioPrefijos: 
	$(CPP) $(CPPFLAGS) -c -o $(LIBDIR)/ServidorVocabularioPrefijos.o $(INCLDIR)/ServidorVocabularioPrefijos.cpp

clean:
	rm bin/*
	rm lib/*
	

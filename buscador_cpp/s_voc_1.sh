#!/bin/bash

echo "Iniciando Servicios"

screen -dmS serv_voc_1 /root/buscador_cpp/bin/activar_servidor_vocabulario 31001 /root/datos/indices_usuarios/indice_0.ii.txt 0
# screen -dmS serv_index_1 /root/buscador_cpp/bin/activar_servidor_consultas 30001 /root/datos/indices_usuarios/indice_32.ii.txt /root/datos/indices_usuarios/grupos_32.txt 0




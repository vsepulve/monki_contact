#!/bin/bash


echo "Realizando Actualizacion"
cd /root/core_java/
java UpdateServices /root/datos/pc.config /root/datos/indices_usuarios/indice /root/datos/indices_usuarios/grupos 0 localhost

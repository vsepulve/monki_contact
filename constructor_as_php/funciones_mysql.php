<?php

/*incluimos las variables de configuracion */

require_once("variables.php");


function conectar(){

  global $HOST;
  global $USER;
  global $PASS;
  global $DATABASE;
  $con = mysql_connect ($HOST,$USER,$PASS,false,65536) or die ("\nERROR EN CONEXION: ".mysql_error()."\n");
  $base_datos = mysql_select_db ($DATABASE,$con)or die ("\nERROR AL SELECCIONAR BASE DE DATOS: ".mysql_error()."\n");
  return $con;

}

 

function ejecutar($sql,$con){
  $result = mysql_query ($sql,$con);// or die ("\nERROR EN LA CONSULTA: $sql DETALLE:".mysql_error()."\n");
  return $result;
}

?>
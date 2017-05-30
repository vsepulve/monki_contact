<?php

########## Prueba ##########

$max_queries = 10000;

echo "Queries Voc en localhost\n";
$file = @fopen('queries_voc.txt', "r");
$host = "localhost";
$port = "31001";
$contador = 0;
$micro_total = 0;
while (!feof($file)) {
	$query = trim(fgets($file)) ;
	
	$tiempo_ini = microtime(true);
	direct_query($query, $host, $port);
	$tiempo_fin = microtime(true);
	$micro_total += ($tiempo_fin - $tiempo_ini) ;
#	echo "Tiempo: ".number_format(($tiempo_fin-$tiempo_ini)*1000, 2)." ms\n";
	usleep(300000);
	
	$contador++;
	if($contador >= $max_queries){
		break;
	}
}
fclose($file) ;
$micro_total /= $contador;
echo "Tiempo medio: ".number_format($micro_total*1000, 2)."\n";


echo "Queries Voc en 173.204.95.19\n";
$file = @fopen('queries_voc.txt', "r");
$host = "173.204.95.19";
$port = "31001";
$contador = 0;
$micro_total = 0;
while (!feof($file)) {
	$query = trim(fgets($file)) ;
	
	$tiempo_ini = microtime(true);
	direct_query($query, $host, $port);
	$tiempo_fin = microtime(true);
	$micro_total += ($tiempo_fin - $tiempo_ini) ;
#	echo "Tiempo: ".number_format(($tiempo_fin-$tiempo_ini)*1000, 2)." ms\n";
	usleep(300000);
	
	$contador++;
	if($contador >= $max_queries){
		break;
	}
}
fclose($file) ;
$micro_total /= $contador;
echo "Tiempo medio: ".number_format($micro_total*1000, 2)."\n";

echo "Queries Index en localhost\n";
$file = @fopen('queries_index.txt', "r");
$host = "localhost";
$port = "30001";
$contador = 0;
$micro_total = 0;
while (!feof($file)) {
	$query = trim(fgets($file)) ;
	
	$tiempo_ini = microtime(true);
	direct_query($query, $host, $port);
	$tiempo_fin = microtime(true);
	$micro_total += ($tiempo_fin - $tiempo_ini) ;
#	echo "Tiempo: ".number_format(($tiempo_fin-$tiempo_ini)*1000, 2)." ms\n";
	usleep(300000);
	
	$contador++;
	if($contador >= $max_queries){
		break;
	}
}
fclose($file) ;
$micro_total /= $contador;
echo "Tiempo medio: ".number_format($micro_total*1000, 2)."\n";


echo "Queries Index en 173.204.95.19\n";
$file = @fopen('queries_index.txt', "r");
$host = "173.204.95.19";
$port = "30001";
$contador = 0;
$micro_total = 0;
while (!feof($file)) {
	$query = trim(fgets($file)) ;
	
	$tiempo_ini = microtime(true);
	direct_query($query, $host, $port);
	$tiempo_fin = microtime(true);
	$micro_total += ($tiempo_fin - $tiempo_ini) ;
#	echo "Tiempo: ".number_format(($tiempo_fin-$tiempo_ini)*1000, 2)." ms\n";
	usleep(300000);
	
	$contador++;
	if($contador >= $max_queries){
		break;
	}
}
fclose($file) ;
$micro_total /= $contador;
echo "Tiempo medio: ".number_format($micro_total*1000, 2)."\n";



########## Fin Prueba ##########


########## Funciones ##########

function direct_query($query, $server, $port){

		//Conexion Vocabulario
		$conexion = fsockopen($server, $port);
		if($conexion){
#				echo "Connected $port\n";

#				echo "Q: $query\n";
				fputs($conexion, utf8_encode($query));
				fflush($conexion);

				$result = utf8_decode(fread($conexion, 1024));
				fclose($conexion);
#				echo "R: $result\n";
		}
		else{
				return NULL;
		}


}



########## Fin Funcion ##########


?>



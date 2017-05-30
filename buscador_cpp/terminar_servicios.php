<?php

########## Prueba ##########

terminate_java_service("localhost", 20001);
terminate_java_service("localhost", 20002);
terminate_java_service("localhost", 20003);
terminate_java_service("localhost", 20004);
terminate_java_service("localhost", 20005);

terminate_java_service("localhost", 21001);
terminate_java_service("localhost", 21002);
terminate_java_service("localhost", 21003);
terminate_java_service("localhost", 21004);
terminate_java_service("localhost", 21005);

exec("ps -fea | grep serv_ | awk '{system(\"kill \" $2)}'", $salida);
	foreach($salida as $linea){
		echo " -> ".$linea."<br>";
	}

########## Fin Prueba ##########


########## Funcion ##########

function terminate_java_service($server, $port){
	
	$timeout = 1;

	$query="-q";
	
	//Conexion Vocabulario
	$conexion = fsockopen($server, $port);
	if($conexion){
		//Enviar Consulta previa
		fputs($conexion, utf8_encode($query));
		fflush($conexion);
		//Cerrar Conexion Vocabulario
		fclose($conexion);
		return true;
	}
	else{
		return false;
	}
	//Conexion Consultas
	$conexion = fsockopen($server, $port);
	if($conexion){
		//Enviar Consulta final
		fputs($conexion, utf8_encode($query));
		fflush($conexion);
		//Cerrar Conexion Consultas
		fclose($conexion);
		return true;
	}
	else{
		return false;
	}
}

########## Fin Funcion ##########


?>

<html>
<head>
<title>Prueba</title>
</head>
<body>
<h1>Prueba</h1>


</body>
</html>


<?php

########## Prueba ##########

//k
$k=10;
$users[] = 0;
# $users[] = 4390;
$server = "localhost";
$port_voc = 31001;
$port_index = 30001;

# direct_query("vm 10 1 0 1 all 1", "localhost", 31001);
# direct_query("ima 10 1 0 1 all 1 1 a 1", "localhost", 30001);

# direct_query("us indice_0.ii.txt grupos_0.txt 0 0", "localhost", 31001);
# direct_query("us indice_0.ii.txt grupos_0.txt 0 0", "localhost", 30001);

$i = 0;
while(true){
	$query = readline("Consulta vocabulario: ");
	
	$tiempo_ini = microtime(true);

	//preparar consulta (convertirla en arreglo de terminos sin acentos)
	$query = strtolower($query);
	$entradas = array("á", "é", "í", "ó", "ú", "ñ");
	$salidas = array("a", "e", "i", "o", "u", "n");
	$query = str_replace($entradas, $salidas, $query);
	//debe haber certeza de que el arreglo SOLO TIENE TERMINOS VALIDOS, por lo que revisamos
	$query_terms_aux = explode(" ", $query);
	$query_terms = array();
	foreach($query_terms_aux as $term){
		if(strlen($term)>0){
			$query_terms[] = $term;
		}
	}
	
	$arr = search_multi_v2($users, $query_terms, $k, $server, $port_voc, $port_index);
	
	$tiempo_fin = microtime(true);
	
	echo "Tiempo Total: ".number_format(($tiempo_fin-$tiempo_ini)*1000, 2)." ms\n";


	echo "-----\n";
}


########## Fin Prueba ##########


########## Funciones ##########

function direct_query($query, $server, $port){

        //Conexion Vocabulario
        $conexion = fsockopen($server, $port);
        if($conexion){
                echo "Connected $port\n";

                echo "Q: $query\n";
                fputs($conexion, utf8_encode($query));
                fflush($conexion);

                $result=utf8_decode(fread($conexion, 1024));
                fclose($conexion);
                echo "R: $result\n";
        }
        else{
                return NULL;
        }


}



function search_multi_v2(&$users, &$query_terms, $k, $server, $port_voc, $port_index){
	
	//Conexion Vocabulario
	$conexion = fsockopen($server, $port_voc);
	if($conexion){
		echo "Connected $port_voc\n";
		$q_voc="vm 9 ".count($users);
		foreach($users as $user){
			$q_voc.=" ".$user." 1 all ".count($query_terms);
			foreach($query_terms as $term){
				$q_voc.=" ".($term);
			}
		}
		
		echo "Q: $q_voc\n";
		fputs($conexion, utf8_encode($q_voc));
		fflush($conexion);
		
		$r_voc=utf8_decode(fread($conexion, 1024));
		fclose($conexion);
		echo "R: $r_voc\n";
	}
	else{
		return NULL;
	}
	
	//Conexion Indice
	$conexion = fsockopen($server, $port_index);
	if($conexion){
		echo "Connected $port_index\n";
		$q_index="ima $k ".$r_voc;
		
		echo "Q: $q_index\n";
		fputs($conexion, utf8_encode($q_index));
		fflush($conexion);
		
		$r_index=trim(utf8_decode(fread($conexion, 1024)));
		fclose($conexion);
		echo "R: $r_index\n";
		
		$arr=split("[ ]", $r_index);
		return $arr;
		
	}
	else{
		return NULL;
	}
	
}



########## Fin Funcion ##########


?>



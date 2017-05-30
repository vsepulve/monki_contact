<?php

$server = "localhost";
$puertos_voc = array(31001, 31002, 31003, 31004, 31005);
$puertos_index = array(30001, 30002, 30003, 30004, 30005);
$n_servicios = 1;

$ruta_buscador = "/root/buscador_cpp";
$update_services = false;

for($i=0; $i<$n_servicios; $i++){
	$puerto = $puertos_voc[$i];
	
	$comando = "ps -fea | grep \"activar_servidor_vocabulario $puerto \" | grep -v \"grep\" | wc -l";
	
#	echo "Ejecutando \"$comando\"\n";
	$salida = "";
	exec($comando, $salida);
#	echo "\"".$salida[0]."\"\n";
	
	if($salida[0] < 1){
		//reiniciar servicio
		$update_services = true;
		echo "[".date("Y-m-d H-i-s")."] Puerto Voc $puerto Mal\n";
		$comando = "$ruta_buscador/s_voc_".($i+1).".sh";
		echo "Ejecutando \"$comando\"\n";
		exec($comando, $salida);
	}
	else{
		echo "[".date("Y-m-d H-i-s")."] Puerto Voc $puerto Ok\n";
	}

}//for... cada servicio


for($i=0; $i<$n_servicios; $i++){
	$puerto = $puertos_index[$i];
	
	$comando = "ps -fea | grep \"activar_servidor_consultas $puerto \" | grep -v \"grep\" | wc -l";
	
#	echo "Ejecutando \"$comando\"\n";
	$salida = "";
	exec($comando, $salida);
#	echo "\"".$salida[0]."\"\n";
	
	if($salida[0] < 1){
		//reiniciar servicio
		$update_services = true;
		echo "[".date("Y-m-d H-i-s")."] Puerto Index $puerto Mal\n";
		$comando = "$ruta_buscador/s_index_".($i+1).".sh";
		echo "Ejecutando \"$comando\"\n";
		exec($comando, $salida);
	}
	else{
		echo "[".date("Y-m-d H-i-s")."] Puerto Index $puerto Ok\n";
	}

}//for... cada servicio

if($update_services){
	
#	$message = "[".date("Y-m-d H-i-s")."] Error en servicios, revisar.\r\n /人 ◕‿‿◕ 人\\ \r\n";
#	$message = wordwrap($message, 70, "\r\n");
#	$headers = 'From: check.services@monki';
	# mail('palabragris@gmail.com, ulcango@gmail.com', '[Monki] Error en Servicios', $message, $headers);
	
	require_once "Mail.php";

	$from = "Notificaciones Monki <notificaciones@mon.ki>";
	$to = "palabragris@gmail.com, ulcango@gmail.com";
	$subject = "[Monki] Error en Servicios";
	$body = "[".date("Y-m-d H-i-s")."] Error en servicios, revisar.\r\n /人 ◕‿‿◕ 人\\ \r\n";
	$body = wordwrap($body, 70, "\r\n");
	$host = "aspmx.l.google.com";

	$headers = array ('From' => $from, 'To' => $to, 'Subject' => $subject);
	$smtp = Mail::factory('smtp', array ('host' => $host, 'auth' => false));
	$mail = $smtp->send($to, $headers, $body);

	// sleep para asegurar que los servicios esten listos para el update
	sleep(10);
	
	//Solo invocar el update si NO esta corriendo ya
	$comando = "ps -fea | grep \"java UpdateServices \" | grep -v \"grep\" | wc -l";
	if($salida[0] < 1){
		echo "[".date("Y-m-d H-i-s")."] Updeteando servicios...\n";
		exec("/root/core_java/s_update_services.sh", $salida);
	}
}

?>


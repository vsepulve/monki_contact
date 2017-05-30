<?php

require_once "Mail.php";

$from = "Notificaciones Monki <notificaciones@mon.ki>";
$to = "palabragris@gmail.com, ulcango@gmail.com";
$subject = "[Monki] Mensaje de Prueba";
$body = "[".date("Y-m-d H-i-s")."] Servicios ok.\r\n /人 ◕‿‿◕ 人\\ \r\n";
$body = wordwrap($body, 70, "\r\n");
$host = "aspmx.l.google.com";

$headers = array ('From' => $from, 'To' => $to, 'Subject' => $subject);
$smtp = Mail::factory('smtp', array ('host' => $host, 'auth' => false));
$mail = $smtp->send($to, $headers, $body);

#$message = "[".date("Y-m-d H-i-s")."] Servicios ok.\r\n /人 ◕‿‿◕ 人\\ \r\n";
#$message = wordwrap($message, 70, "\r\n");
#$headers = 'From: check.services@monki';

#if(! mail('palabragris@gmail.com, ulcango@gmail.com', '[Monki] Prueba Mail', $message, $headers)){
#	echo "Error al enviar\n";	
#}
#else{
#	echo "Envio aceptado\n";
#}

?>


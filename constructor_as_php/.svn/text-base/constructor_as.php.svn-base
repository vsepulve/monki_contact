<?php

//incluimos el archivo con las funciones
include ("funciones_negocio.php");
$initial_status = "processing";
$final_status = "grouped";
global $conexion;

echo "Conectando a ($HOST/$DATABASE) ...\n"; 

// se obtiene mapa de user,grupo,contactos
$mapa = get_map_user_group_contact($initial_status);

$num_rows = sizeof($mapa);
$porcentaje = 10;
$count=0;

//por cada usuario
foreach($mapa as $user_id => $grupo_contacts){
    // por cada grupo del usuario
	echo "usuario -> $user_id";
	    foreach ($grupo_contacts as $group_id => $contactos){
		    echo "Procesando usuario:$user_id -> Grupo:$group_id -> ";
		    for($i=0;$i<count($contactos);$i++){
			  echo $contactos[$i]." - ";
		    }

		    echo "\n";
		    //se elimina active storage del par user,grupo (version previa)
		    delete_user_group($user_id,$group_id);

		    // se obtiene los contactos del usuario y grupo
		    $contactos = $mapa[$user_id][$group_id];

		    // se obtienen los datos para AS
		    $emails=get_emails($contactos);
		    $names=get_names($contactos);
		    $phones=get_phones($contactos);
		    $postaladdresses=get_postaladdresses($contactos);
		    $ims=get_ims($contactos);
		    $extended_prop=get_extended_prop($contactos);
		    $organizations=get_organizations($contactos);
		    $educations=get_educations($contactos);
		    $demographics=get_demographics($contactos);

		    // nuevas tablas para AS
		    $images = get_images($contactos);
		    $profiles = get_profiles($contactos);

/*
		    echo "Valores a insertar\n";
		    echo "user: $user_id - grupo = $group_id";

		    print_r($names);
		    print_r($emails);
		    print_r($phones);
		    print_r($postaladdresses);
		    print_r($ims);
		    print_r($organizations);
		    print_r($educations);
		    print_r($demographics);
*/
		    // se insertan los datos en AS
		    insert_emails($group_id,$emails);
		    insert_names($group_id,$names);
		    insert_phones($group_id,$phones);
		    insert_postaladdresses($group_id,$postaladdresses);
		    insert_ims($group_id,$postaladdresses);
		    insert_extendedProperty($group_id,$extended_prop);
		    insert_organizations($group_id,$organizations);
		    insert_educations($group_id,$educations);
		    insert_demographics($group_id,$demographics);

		    insert_images($group_id,$images);
		    insert_profiles($group_id,$profiles);

		    insert_contact($group_id,$user_id,$contactos,$names);

		    cleanPhones($user_id,$group_id);

		    
	    }

    $count++;
}
//actualizacion de los estados de los contactos
//update_contacts($initial_status,$final_status);

//se cierra conexion
mysql_close($conexion);
echo "Fin";

?>

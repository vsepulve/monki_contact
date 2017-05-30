<?

include ("funciones_mysql.php");

$conexion = conectar();

function get_contacts_group($user_id,$group_id){
  global $conexion;

  
//   $sql = "call get_contacts_same_group($user_id,$group_id);";
  $sql = "select contactId from as_groups where user_Id=$user_id and groupId=$group_id";


  $result = ejecutar($sql,$conexion);
  $contactos = array();

  while($contacto= mysql_fetch_row($result)){
      $contactos[] = $contacto[0];
  }

  return $contactos;
}

function update_contacts($from_status,$to_status){
  global $conexion;
  $sql = "update contacts set status='".$to_status."' where status='".$from_status."'";
  ejecutar($sql,$conexion);

 
}


function get_user_group($initial_status){
  global $conexion;
  global $initial_status;
  $sql = "select distinct user_Id,groupId from as_groups where groupId in (select G.groupId from as_groups as G inner join contacts as C on (G.contactId=C.contactId) where C.status='".$initial_status."') order by user_Id";
  $result = ejecutar($sql,$conexion);
  while ($row = mysql_fetch_row($result)){
	$pares[]= array("user_Id"=>$row[0],"groupId"=>$row[1]);
  }

  return $pares;
}

function get_map_user_group_contact($initial_status){
  global $conexion;
  global $initial_status;
  $sql = "select distinct G.user_Id,G.groupId,G.contactId from as_groups as G inner join contacts C on (G.contactId = C.contactId and C.status='".$initial_status."') order by G.user_Id,G.groupId;";
  $result = ejecutar($sql,$conexion);

  $mapa = array();

  while ($row = mysql_fetch_row($result)){
	$mapa[$row[0]][$row[1]][] = $row[2];
  }

//   print_r($mapa);
  return $mapa;
}


function delete_user_group($user,$group){
  global $conexion;

  $sql="call delete_user_groups(".$user.",".$group.")";

  ejecutar($sql,$conexion);

}

function get_contacts_like_string($contactos){
  $salida=" (";
  for($i=0;$i<count($contactos);$i++){
      $salida.=$contactos[$i].",";
  }
  $salida = rtrim($salida,",");
  $salida .= ") ";
  return $salida;
}

function get_emails($contactos){
  global $conexion;

  $emails = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_mails_no_primary(".$contactos[$i].");";
      $sql = "select lower(address) as address,address,emailID,contactId,user_Id,accountSrc,label,`type`,`primary`,source,insertDate from email e where e.contactId=".$contactos[$i]." and e.primary=0 and address<>''";

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $emails[$row[0]] = array("address"=>$row[1],"emailID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"label"=>$row[6],"type"=>$row[7],"primary"=>$row[8],"source"=>$row[9],"insertDate"=>$row[10]);
      }
  }
  
  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_mails_primary(".$contactos[$i].");";
      $sql = "select lower(address) as address,address,emailID,contactId,user_Id,accountSrc,label,`type`,`primary`,source,insertDate from email e where e.contactId=".$contactos[$i]." and e.primary=1  and address<>''";

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $emails[$row[0]] = array("address"=>$row[1],"emailID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"label"=>$row[6],"type"=>$row[7],"primary"=>$row[8],"source"=>$row[9],"insertDate"=>$row[10]);
      }

  }


  return $emails;
}


function get_names($contactos){
  global $conexion;

  $names = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_names(".$contactos[$i].");";
      $sql = "select lower(fullName) as fullName,fullName,nameId,contactId,user_Id,accountSrc,firstName,lastName,source,insertDate from names where contactId=".$contactos[$i];

	echo "$sql\n";

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $names[$row[0]] = array("fullName"=>$row[1],"nameId"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"firstName"=>$row[6],"lastName"=>$row[7],"source"=>$row[8],"insertDate"=>$row[9]);
      }
  }


  return $names;

}

function get_phones($contactos){
  global $conexion;
  
  $phones = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_phones(".$contactos[$i].");";
      $sql = "select lower(Text) as Text,Text,phoneID,contactId,user_Id,accountSrc,label,`type`,`primary`,source,insertDate from phonenumber where contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $phones[$row[0]] = array("Text"=>$row[1],"phoneID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"label"=>$row[6],"type"=>$row[7],"primary"=>$row[8],"source"=>$row[9],"insertDate"=>$row[10]);
      }
  }
  return $phones;
}

function get_postaladdresses($contactos){
  global $conexion;
  
  $postal = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_postaladdresses(".$contactos[$i].");";
      $sql = "select lower(Text) as Text,Text,paID,contactId,user_Id,accountSrc,label,`type`,`primary`,source,insertDate from postalAddress where contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $postal[$row[0]] = array("Text"=>$row[1],"paID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"label"=>$row[6],"type"=>$row[7],"primary"=>$row[8],"source"=>$row[9],"insertDate"=>$row[10]);
      }
  }
  return $postal;
}

function get_ims($contactos){
  global $conexion;

  $ims = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_ims(".$contactos[$i].");";
      $sql = "select lower(address) as address,address,imID,contactId,user_Id,accountSrc,label,`type`,protocol,`primary`,source,insertDate from im where contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $ims[$row[0]] = array("address"=>$row[1],"imID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"label"=>$row[6],"type"=>$row[7],"protocol"=>$row[8],"primary"=>$row[9],"source"=>$row[10],"insertDate"=>$row[11]);
      }
  }
  return $ims;
}

function get_extended_prop($contactos){
  global $conexion;

  $properties = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_extended_prop(".$contactos[$i].");";
      $sql = "select name,epID,contactId,user_Id,value,source,insertDate from extendedProperty where contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $properties[$row[0]] = array("name"=>$row[0],"epID"=>$row[1],"contactId"=>$row[2],"user_Id"=>$row[3],"value"=>$row[4],"source"=>$row[5],"insertDate"=>$row[6]);
      }
  }
  return $properties;
}


function get_organizations($contactos){
  global $conexion;

  $organizations = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_organizations(".$contactos[$i].");";
      $sql = "select lower(orgName) as orgName,orgName,orgID,contactId,user_Id,accountSrc,label,`type`,`primary`,department,orgTitle,startDate,endDate,jobStatus,location,description,industry,source,insertDate from organization where contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $organizations[$row[0]] = array("orgName"=>$row[1],"orgID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"label"=>$row[6],"type"=>$row[7],"primary"=>$row[8],"department"=>$row[9],"orgTitle"=>$row[10],"startDate"=>$row[11],"endDate"=>$row[12],"jobStatus"=>$row[13],"location"=>$row[14],"description"=>$row[15],"industry"=>$row[16],"source"=>$row[17],"insertDate"=>$row[18]);

      }
  }
  return $organizations;
}



function get_educations($contactos){
  global $conexion;

  $educations = array();

  for($i=0;$i<count($contactos);$i++){
//       $sql = "call get_educations(".$contactos[$i].");";
      $sql = "select lower(schoolName) as schoolName,schoolName,eduId,contactId,user_Id,accountSrc,degree,fieldOfStudy,startYear,endYear,source,srcId from education where contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $educations[$row[0]] = array("schoolName"=>$row[1],"eduId"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"degree"=>$row[6],"fieldOfStudy"=>$row[7],"startYear"=>$row[8],"endYear"=>$row[9],"source"=>$row[10],"srcId"=>$row[11]);
      }
  }
  return $educations;
}

function get_demographics($contactos){
  global $conexion;

  $demographics = array();

  for($i=0;$i<count($contactos);$i++){
      $sql = "select lower(hometown) as hometown,hometown,demoId,contactId,user_Id,accountSrc,location,gender,website,source from demographics where contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $demographics[$row[0]] = array("hometown"=>$row[1],"demoId"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"location"=>$row[6],"gender"=>$row[7],"website"=>$row[8],"source"=>$row[9]);
      }
  }
  return $demographics;
}

function get_images($contactos){
  global $conexion;

  $images = array();

  for($i=0;$i<count($contactos);$i++){

	$sql = "select lower(i.url),i.url,i.imageID,i.contactId,c.user_Id,i.accountSrc,i.srcID,i.source,i.application,i.access,i.insertDate from images as i inner join contacts as c on (i.contactId=c.contactId) where c.contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $images[$row[0]] = array("url"=>$row[1],"imageID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"srcID"=>$row[6],"source"=>$row[7],"application"=>$row[8],"access"=>$row[9],"insertDate"=>$row[10]);
      }
  }
  return $images;
}


function get_profiles($contactos){
  global $conexion;

  $profiles = array();

  for($i=0;$i<count($contactos);$i++){

	$sql = "select lower(p.value),p.value,p.profileID,p.contactId,c.user_Id,p.accountSrc,p.srcID,p.source,p.application,p.type,p.access,p.insertDate from profiles as p inner join contacts as c on (p.contactId=c.contactId) where c.contactId=".$contactos[$i];

      $result = ejecutar($sql,$conexion);
      while($row = mysql_fetch_row($result)){
	  $profiles[$row[0]] = array("value"=>$row[1],"profileID"=>$row[2],"contactId"=>$row[3],"user_Id"=>$row[4],"accountSrc"=>$row[5],"srcID"=>$row[6],"source"=>$row[7],"application"=>$row[8],"type"=>$row[9],"access"=>$row[10],"insertDate"=>$row[11]);
      }
  }
  return $profiles;
}


function mysql_str($string){
    if(is_null($string)){
      return "NULL";
    }

      $string=str_replace("\"","",$string);
      return "\"$string\"";

}

//Inserciones en tablas AS
function insert_images($group_id,$images){
  global $conexion;
  foreach($images as $entry){
	$sql = "insert into as_images(imageID,groupId,user_Id,accountSrc,srcID,source,application,url,access,insertDate) ";
	$values = " values(".$entry["imageID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["accountSrc"]).",".mysql_str($entry["srcID"]).",".mysql_str($entry["source"]).",".mysql_str($entry["application"]).",".mysql_str($entry["url"]).",".mysql_str($entry["access"]).",".mysql_str($entry["insertDate"]).")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function insert_profiles($group_id,$profiles){
  global $conexion;
  foreach($profiles as $entry){
	$sql = "insert into as_profiles(profileID,groupId,user_Id,accountSrc,srcID,source,application,`type`,value,access,insertDate) ";
	$values = " values(".$entry["profileID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["accountSrc"]).",".mysql_str($entry["srcID"]).",".mysql_str($entry["source"]).",".mysql_str($entry["application"]).",".mysql_str($entry["type"]).",".mysql_str($entry["value"]).",".mysql_str($entry["access"]).",".mysql_str($entry["insertDate"]).")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}


function insert_emails($group_id,$emails){
  global $conexion;
  foreach($emails as $entry){
	$sql = "insert into as_email(emailID,groupId,user_Id,address,label,`type`,`primary`,source,insertDate) ";
	$values = " values(".$entry["emailID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["address"]).",".mysql_str($entry["label"]).",".mysql_str($entry["type"]).",".mysql_str($entry["primary"]).",".mysql_str($entry["source"]).",".$entry["insertDate"].")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}


function insert_names($group_id,$names){
  global $conexion;
  foreach($names as $entry){
	$sql = "insert into as_names(nameId,groupId,user_Id,fullName,firstName,lastName,source,insertDate) ";
	$values = " values(".$entry["nameId"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["fullName"]).",".mysql_str($entry["firstName"]).",".mysql_str($entry["lastName"]).",".mysql_str($entry["source"]).",".$entry["insertDate"].")";
	$sql=$sql.$values;

	ejecutar($sql,$conexion);
  }
}


function insert_phones($group_id,$phones){
  global $conexion;
  foreach($phones as $entry){
	$sql = "insert into as_phonenumber(phoneID,groupId,user_Id,label,`type`,`primary`,Text,source,insertDate) ";
	$values = " values(".$entry["phoneID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["label"]).",".mysql_str($entry["type"]).",".mysql_str($entry["primary"]).",".mysql_str($entry["Text"]).",".mysql_str($entry["source"]).",".$entry["insertDate"].")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function insert_postaladdresses($group_id,$postal){
  global $conexion;
  foreach($postal as $entry){
	$sql = "insert into as_postalAddress(paID,groupId,user_Id,label,`type`,`primary`,Text,source,insertDate) ";
	$values = " values(".$entry["paID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["label"]).",".mysql_str($entry["type"]).",".mysql_str($entry["primary"]).",".mysql_str($entry["Text"]).",".mysql_str($entry["source"]).",".$entry["insertDate"].")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function insert_ims($group_id,$ims){
  global $conexion;
  foreach($ims as $entry){
	$sql = "insert into as_im (imID,groupId,user_Id,address,label,`type`,protocol,`primary`,source,insertDate) ";
	$values = " values(".$entry["imID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["address"]).",".mysql_str($entry["label"]).",".mysql_str($entry["type"]).",".mysql_str($entry["protocol"]).",".mysql_str($entry["primary"]).",".mysql_str($entry["source"]).",".$entry["insertDate"].")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function insert_extendedProperty($group_id,$properties){
  global $conexion;
  foreach($properties as $entry){
	$sql = "insert into as_extendedProperty(epID,groupId,user_Id,name,value,source,insertDate) ";
	$values = " values(".$entry["epID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["name"]).",".mysql_str($entry["value"]).",".mysql_str($entry["source"]).",".$entry["insertDate"].")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function insert_organizations($group_id,$organization){
  global $conexion;
  foreach($organization as $entry){
	$sql = "insert into as_organization(orgID,groupId,user_Id,label,`type`,`primary`,orgName,department,orgTitle,startDate,endDate,location,`description`,industry,jobStatus,source,insertDate) ";
	$values = " values(".$entry["orgID"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["label"]).",".mysql_str($entry["type"]).",".mysql_str($entry["primary"]).",".mysql_str($entry["orgName"]).",".mysql_str($entry["department"]).",".mysql_str($entry["orgTitle"]).",".mysql_str($entry["startDate"]).",".mysql_str($entry["endDate"]).",".mysql_str($entry["location"]).",".mysql_str($entry["description"]).",".mysql_str($entry["industry"]).",".mysql_str($entry["jobStatus"]).",".mysql_str($entry["source"]).",".$entry["insertDate"].")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function insert_educations($group_id,$educations){
  global $conexion;
  foreach($educations as $entry){
	$sql = "insert into as_education(eduId,groupId,user_Id,schoolName,degree,fieldOfStudy,startYear,endYear,srcId,source) ";
	$values = " values(".$entry["eduId"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["schoolName"]).",".mysql_str($entry["degree"]).",".mysql_str($entry["fieldOfStudy"]).",".mysql_str($entry["startYear"]).",".mysql_str($entry["endYear"]).",".mysql_str($entry["srcId"]).",".mysql_str($entry["source"]).")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function insert_demographics($group_id,$demographics){
  global $conexion;
  foreach($demographics as $entry){
	$sql = "insert into as_demographics(demoId,groupId,user_Id,hometown,location,gender,website,source) ";
	$values = " values(".$entry["demoId"].",$group_id,".$entry["user_Id"].",".mysql_str($entry["hometown"]).",".mysql_str($entry["location"]).",".mysql_str($entry["gender"]).",".mysql_str($entry["website"]).",".mysql_str($entry["source"]).")";
	$sql=$sql.$values;
	ejecutar($sql,$conexion);
  }
}

function get_sources($contactos){
  global $conexion;
  $from_gmail =0;
  $from_linkedin=0;
  $from_face=0;
  $str_sources="";
  for($i=0;$i<count($contactos);$i++){
    $sql = "select srcName from contacts where contactId=".$contactos[$i];
    $result = ejecutar($sql,$conexion);
    while($row = mysql_fetch_row($result)){
      if($row[0]=="gmail" and $from_gmail==0){
	  $str_sources = $str_sources.$row[0].","; 
	  $from_gmail++;
      }
      if($row[0]=="facebook" and $from_face==0){
	  $str_sources = $str_sources.$row[0].",";
	  $from_face++;
      }
      if($row[0]=="linkedin" and $from_linkedin==0){
	  $str_sources = $str_sources.$row[0].",";
	  $from_linkedin++;
      }
    }
  }

  $str_sources = rtrim($str_sources,",");
  return $str_sources;
}



function insert_contact($group_id,$user_id,$contactos,$names){
    global $conexion;
    $title="";
    
    // se obtiene nombre mas largo
    foreach($names as $name){
	if(strlen(mysql_str($name["fullName"])) > strlen(mysql_str($title))){
	  $title = mysql_str($name["fullName"]);
	}
    }

    $title = mysql_str($title);
    $link=mysql_str(" ");
    $srcId = mysql_str("$group_id"."-"."$user_id");
    $allText=mysql_str("$group_id"."-"."$user_id");
    $srcName = mysql_str(get_sources($contactos));
    
    $sql="insert into as_contacts(groupId,user_Id,srcId,srcName,title,link,allText) ";
    $values = " values($group_id,$user_id,$srcId,$srcName,$title,$link,$allText);";

    $sql = $sql.$values;

    ejecutar($sql,$conexion);
}


function cleanPhones($user_id,$group_id){
  global $conexion;
  $sql="update as_phonenumber set Text=replace(replace(Text,' ',''),'-','') where groupId=$group_id and user_Id=$user_id";
  ejecutar($sql,$conexion);

}




?>

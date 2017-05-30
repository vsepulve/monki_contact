package obs.spaces.util;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.io.File;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.io.FileNotFoundException;

import obs.spaces.data.DatoPerfilDoc;

public class GeneradorVocabularioPerfiles{
	
	static String separadores = "[ .,;:\n()\"\t-\\/<>@]";
	
	public GeneradorVocabularioPerfiles(){
	}
	
	//Genera TODOS los vocabularios del perfil
	//El llamador puede, luego, escoger aquellos de los atributos que quiera
	//Esto deberia ser mas eficiente y seguro (a menos que se quisiera solo uno, lo que no deberia pasar)
	public Map<String, Map<String, Double>> generarVocabularios(DatoPerfilDoc perfil){
		
		Map<String, Map<String, Double>> mapa_atributos = new TreeMap<String, Map<String, Double>>();
		
		//Atributos
		//name: fullname, mail completo, (mail prefijo), phones (por ahora)
		//org: organizations, mail sufijo (excepto el ultimo termino del dominio)
		//location: address, hometown, lives
		//edu: education
		//hometown: hometown
		//lives: lives
		
		String attribute;
		String texto;
		String[] palabras;
		String[] palabras_aux;
		String palabra;
		double valor_inicial;
		Map<String, Double> voc_local;
		
		List<String> lista_textos;
		
		//Preparacion de mails (asegurar una lista correcta)
		List<String> lista_mails = new LinkedList<String>();
		lista_textos = perfil.getEmails();
		for(String mail : lista_textos){
			palabras = mail.split("[ ,;:\n()\"\t\\/<>]");
			for(int i=0; i<palabras.length; i++){
				lista_mails.add(palabras[i]);
			}
		}
		
		////////// INICIO NAME //////////
		voc_local = new TreeMap<String, Double>();
		//FullName
		valor_inicial = 3;
		texto = perfil.getFullName();
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length() > 100) palabra = palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1 + voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		//twitter
		valor_inicial = 1;
		texto = perfil.getTwitter();
		if(texto != null && texto.length() > 0 ){
			//asumo que el id esta correcto
			//solo saco los espacios en blanco por seguridad
			palabra = texto.toLowerCase().replace(" ", "_");
			if(voc_local.containsKey(palabra)){
				voc_local.put(palabra, 1 + voc_local.get(palabra));
			}
			else{
				voc_local.put(palabra, valor_inicial);
			}
		}//if... !=null
		
		//mails
		valor_inicial = 1;
		for(String mail : lista_mails){
			palabra = mail.toLowerCase();
			if(palabra == null || palabra.length() < 1) continue;
			if(palabra.length()>100) palabra=palabra.substring(0, 100);
			if(voc_local.containsKey(palabra)){
				voc_local.put(palabra, 1 + voc_local.get(palabra));
			}
			else{
				voc_local.put(palabra, valor_inicial);
			}
		}//while... cada mail
		
		if(voc_local.size() > 0){
			mapa_atributos.put("name", voc_local);
		}
		////////// FIN NAME //////////
		
		
		////////// INICIO ORG //////////
		voc_local = new TreeMap<String, Double>();
		
		//getOrganizations() 1 => x1
		valor_inicial = 1;
		texto = "";
		lista_textos = perfil.getOrganizations(1);
		for(String org : lista_textos){
			texto += org + " ";
		}
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1 + voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		//getOrganizations() 0 => x0.5
		valor_inicial = 1;
		texto = "";
		lista_textos = perfil.getOrganizations(0);
		for(String org : lista_textos){
			texto += org + " ";
		}
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 0.5 + voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, 0.5 * valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		//getOrganizations() 2 => x0.1
		valor_inicial = 1;
		texto = "";
		lista_textos = perfil.getOrganizations(2);
		for(String org : lista_textos){
			texto += org + " ";
		}
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 0.1 + voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, 0.1 * valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		List<String> sufijos = new LinkedList<String>();
		
		for(String mail : lista_mails){
			palabras = mail.split("[@]");
			if(palabras.length == 2){
				//para este fin omito el ultimo termino del sufijo (<prefijo>@<sufijo valido>.fin)
				if(palabras[1].indexOf('.') > 0){
					sufijos.add(palabras[1].substring(0, palabras[1].lastIndexOf('.')));
				}
			}
		}//for... cada mail
		
		valor_inicial = 1;
		texto = "";
		for(String sufijo : sufijos){
			texto += sufijos + " ";
		}
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1 + voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		if(voc_local.size() > 0){
			mapa_atributos.put("org", voc_local);
		}
		////////// FIN ORG //////////
		
		
		////////// INICIO LOCATION //////////
		voc_local = new TreeMap<String, Double>();
		
		//getAddress() 1
		valor_inicial = 1;
		texto = perfil.getAddress();
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1+voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		//getHometown() 1
		valor_inicial = 1;
		texto = perfil.getHometown();
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1+voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		//getLives() 1
		valor_inicial = 1;
		texto = perfil.getLives();
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1+voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
			
		if(voc_local.size() > 0){
			mapa_atributos.put("location", voc_local);
		}
		////////// FIN LOCATION //////////
		
		
		////////// INICIO EDU //////////
		voc_local = new TreeMap<String, Double>();
		
		//getContent() 1
		valor_inicial = 1;
		texto = perfil.getContent();
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1+voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
			
		if(voc_local.size() > 0){
			mapa_atributos.put("edu", voc_local);
		}
		////////// FIN EDU //////////
		
		
		////////// INICIO HOMETOWN //////////
		voc_local = new TreeMap<String, Double>();
		
		//getHometown() 1
		valor_inicial = 1;
		texto = perfil.getHometown();
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1+voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		if(voc_local.size() > 0){
			mapa_atributos.put("hometown", voc_local);
		}
		////////// FIN HOMETOWN //////////
		
		
		////////// INICIO LIVES //////////
		voc_local = new TreeMap<String, Double>();
		
		//getLives() 1
		valor_inicial = 1;
		texto = perfil.getLives();
		if(texto != null && texto.length() > 0 ){
			palabras = texto.toLowerCase().split(separadores);
			for(int i=0; i<palabras.length; i++){
				palabra = palabras[i];
				if(palabra == null || palabra.length() < 1) continue;
				if(palabra.length()>100) palabra=palabra.substring(0, 100);
				if(voc_local.containsKey(palabra)){
					voc_local.put(palabra, 1+voc_local.get(palabra));
				}
				else{
					voc_local.put(palabra, valor_inicial);
				}
			}//for... cada palabra
		}//if... !=null
		
		if(voc_local.size() > 0){
			mapa_atributos.put("lives", voc_local);
		}
		////////// FIN LIVES //////////
		
		return mapa_atributos;
		
	}
	
}




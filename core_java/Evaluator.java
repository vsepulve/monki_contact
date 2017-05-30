import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

public class Evaluator{

	public static void main(String[]args){

		if(args.length != 4){
			System.out.println("");
			System.out.println("Usage:");
			System.out.println("> java Evaluator file_completos num_iterations num_test num_cells");
			System.out.println("");
			return;
		}
		
		String file_celula1 = args[0];
		int iteraciones = Integer.parseInt(args[1]);
        String num_cells = args[2];
        String num_test = args[3];
        String agrupacion;

        for(int i=1;i<=iteraciones;++i){

		    BufferedReader reader1 = null;
		    BufferedReader reader2 = null;
            String file_celula2 = "prueba"+num_test+"/"+num_cells+"/it_"+i;
            Map<String, List<String>> contacto_grupos = new TreeMap<String,List<String>>();

            Map<String, List<String>> grupos_completos = new TreeMap<String,List<String>>();
            Map<String, List<String>> grupos_iteracion = new TreeMap<String,List<String>>();
            Map<String, String> contactos_iteracion = new TreeMap<String,String>();

            Set<String> grupos_conflictivos = new TreeSet<String>();

            int conflictos = 0;
		    try{
                System.out.println("Cargando informacion de "+file_celula1);
			    reader1 = new BufferedReader(new FileReader(file_celula1));
			    while((agrupacion = reader1.readLine())!=null){
                    String[] grupos = agrupacion.split(" ");
                    String contact_id = grupos[0];
                    String group_id = grupos[1];

                    //Guardo en grupos_completos (grupos -> contactos del grupo)
                    if(grupos_completos.containsKey(group_id)){
                        grupos_completos.get(group_id).add(contact_id);
                    }
                    else{
                        List<String> lista=new LinkedList<String>();
		                lista.add(contact_id);
		                grupos_completos.put(group_id, lista);
                    }

                    if(contacto_grupos.containsKey(contact_id)){//CONFLICTO
                        contacto_grupos.get(contact_id).add(group_id);
                        String grupo_previo = contacto_grupos.get(contact_id).get(0);

                        if(!grupo_previo.equals(group_id)){
                            ++conflictos;
                            grupos_conflictivos.add(grupo_previo);//guardo grupo conflictivo ESTO NO DEBERIA PASAR ACA
                            System.out.println("WARNING: En grupos completos hay un contacto en dos grupos");
                        }
			        }
			        else{
				        List<String> lista=new LinkedList<String>();
				        lista.add(group_id);
				        contacto_grupos.put(contact_id, lista);
			        }


                }

                reader1.close();

		    }catch(Exception e){
			    System.err.println("Evaluator - Error al leer configuracion ("+file_celula1+")");
			    e.printStackTrace();
			    return;
		    }
		    try{
                System.out.println("Cargando informacion de "+file_celula2);
			    reader2 = new BufferedReader(new FileReader(file_celula2));
			    while((agrupacion = reader2.readLine())!=null){
                    String[] grupos2 = agrupacion.split(" ");
                    String contact_id = grupos2[0];
                    String group_id = grupos2[1];
                    contactos_iteracion.put(contact_id, group_id);

                    //Guardo en grupos_iteracion (grupos -> contactos del grupo)
                    if(grupos_iteracion.containsKey(group_id)){
                        grupos_iteracion.get(group_id).add(contact_id);
                    }
                    else{
                        List<String> lista=new LinkedList<String>();
		                lista.add(contact_id);
		                grupos_iteracion.put(group_id, lista);
                    }

                    if(contacto_grupos.containsKey(contact_id)){
                        contacto_grupos.get(contact_id).add(group_id);
                        String grupo_previo = contacto_grupos.get(contact_id).get(0);

                        if(!grupo_previo.equals(group_id)){
                            ++conflictos;
                            grupos_conflictivos.add(grupo_previo);//guardo grupo conflictivo, es el que se guardo de los completos
                        }
			        }
			        else{
				        List<String> lista=new LinkedList<String>();
				        lista.add(group_id);
				        contacto_grupos.put(contact_id, lista);
			        }
                }
                reader2.close();
		    }catch(Exception e){
			    System.err.println("Evaluator - Error al leer configuracion ("+file_celula2+")");
			    e.printStackTrace();
			    return;
		    }


            System.out.println(i);//contactos en mas de un grupo?
            System.out.println(grupos_conflictivos.size());//contactos en mas de un grupo?
            System.out.println(grupos_iteracion.size());

            /********************** Evaluar grupos conflictivos ***********************/

            double hay_completos = 0;
            int grupos_con_mas_contactos = 0;
            int deberian_haber_completos = grupos_conflictivos.size();
            int grupos_con_menos_contactos = 0;
        
            for(String grupo : grupos_conflictivos){                            //reviso cada grupo conflictivo

                int hay = 0;
                int hay_totales = 0;
                int deberian_haber = 0;
                List<String> contactos;
                List<String> contactos_parciales;
                if(grupos_completos.containsKey(grupo)){                        //deberia estar en los grupos completos
                    contactos = grupos_completos.get(grupo);                    //saco los contactos del grupo completo

                    deberian_haber = contactos.size();
                    Set<String> grupos_parciales = new TreeSet<String>();
                    String mayor = null;
                    for(String contacto : contactos){
                        grupos_parciales.add(contactos_iteracion.get(contacto)); //grupos que tengo que revisar, grupos en que quedaron los contactos del grupo completo
                    }

                    int size = 0;                                               //deberia revisar solo el con mas contactos
                    for(String grupo_parcial : grupos_parciales){                            
                        if(grupos_iteracion.get(grupo_parcial).size()>size){
                            size = grupos_iteracion.get(grupo_parcial).size();
                            mayor = grupo_parcial;
                        }
                    }
                    contactos_parciales = grupos_iteracion.get(mayor);
                    hay_totales = contactos_parciales.size();
                    //mayor = grupo que tengo que comparar con grupo
                    //contactos = contactos de grupo
                    //contactos_parciales = contactos de mayor

                    for(String contacto : contactos){                           //para cada contacto del grupo completo
                        if(contactos_parciales.contains(contacto))              //cuento cuantos contactos de grupo completo hay en grupo parcial
                            ++hay;
                    }

                    if(hay_totales > deberian_haber){//si hay mas de los que deberian haber
                        //System.out.println("hay "+(deberian_haber - hay_totales)+" contactos demas en grupo "+grupo);
                        //contactos_parciales.removeAll(contactos);
                        ++grupos_con_mas_contactos;
                    }
                }

                if(hay-deberian_haber<=0){//Veo la porcion completa que existe
                    hay_completos += ((double)hay/(double)deberian_haber);
                    if(hay-deberian_haber<0)
                        ++grupos_con_menos_contactos;
                }
                //sigo con siguiente grupo conflictivo
            }

            int hay_completos_totales = grupos_completos.size()-grupos_conflictivos.size();
            //System.out.println("Hay "+grupos_con_mas_contactos+" grupos con mas contactos");
            System.out.println(grupos_con_menos_contactos);
            //System.out.println("Similitud entre BBDD completas: "+(double)Math.round(((hay_completos+hay_completos_totales)/(deberian_haber_completos+hay_completos_totales))*10000)/100+"%");
            System.out.println((double)Math.round((hay_completos/deberian_haber_completos)*10000)/100+"%\n");
        }
    }
}   

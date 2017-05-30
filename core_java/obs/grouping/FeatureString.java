package obs.grouping;

import obs.grouping.Feature;

public class FeatureString extends Feature implements Comparable<Feature>{

    String palabra;
    public FeatureString(String _palabra){
        palabra = _palabra;
    }
    public int compareTo(Feature feature){
    	if(palabra==null || palabra.length()<1
    		|| feature==null || !(feature instanceof FeatureString)){
    		return 1;	
    	}
    	else{
            FeatureString featurestring = (FeatureString)feature;
            String text = featurestring.getString();
            if(text==null || text.length()<1){
            	return 1;
            }
            return palabra.compareTo(text);
        }
    }
    
    public String getString(){
        return palabra;
    }
    
    public String toString(){
        return palabra;
    }

}

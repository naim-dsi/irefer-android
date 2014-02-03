package com.dsinv.irefer2;

import java.util.Comparator;
import java.util.Map;

public class ListOfMapComparator implements Comparator<Map> {
    private String key;
    private boolean numericSort;

    public ListOfMapComparator(String key) {
        this.key = key;
        this.numericSort = false;
    }
    public ListOfMapComparator(String key, boolean numericSort) {
        this.key = key;
        this.numericSort = numericSort;
    }

    //public int compare(Object a, Object b) {
    @Override
    public int compare(Map object1, Map object2) {
        //Map map1 = (Map)a;
        //Map map2 = (Map)b;
        try {
        	System.out.println("SMM::["+key+"] = "+object1.get(key));
            String str1 = ((Object)object1.get(key)).toString();
            String str2 = ((Object)object2.get(key)).toString();
            //log.debug(str1+"-"+str2+"="+str1.compareTo(str2));
            if(numericSort)
                return leftPad(str2, 5, '0').compareTo(leftPad(str1, 5, '0'));
            else
                return str1.compareTo(str2);
        } catch (Exception ex) {
        	ex.printStackTrace();
        	return 0;
        }
    }
    
    private String leftPad(String s, int length, char pad) {
        StringBuffer buffer = new StringBuffer(s);
        while (buffer.length() < length) {
            buffer.insert(0, pad);
        }
        return buffer.toString();
    }
	
}

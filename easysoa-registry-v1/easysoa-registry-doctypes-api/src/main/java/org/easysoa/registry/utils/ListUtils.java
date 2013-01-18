package org.easysoa.registry.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static List<String> toStringList(Serializable[] array) {
        List<String> list = new ArrayList<String>();
        if (array == null) {
            return list;
        }
        for (Serializable element : array) {
            list.add(element.toString());
        }
        return list;
    }


    /*// generic varargs don't work
    public static ArrayList<T> list(T...objs) {
        ArrayList<T> list = new ArrayList<T>(objs.length);
        for (T obj : objs) {
            list.add(obj);
        }
        return list;
    }
    */
    
}

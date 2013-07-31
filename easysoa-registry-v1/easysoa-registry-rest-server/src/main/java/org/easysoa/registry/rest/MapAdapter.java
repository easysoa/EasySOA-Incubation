package org.easysoa.registry.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, Address>> {
    
    public static class AdaptedMap {
         
        public List<Entry> entry = new ArrayList<Entry>();
  
    }
     
    public static class Entry {
         
        public String key;
         
        public Address value;
   
    }
 
    @Override
    public Map<String, Address> unmarshal(AdaptedMap adaptedMap) throws Exception {
        Map<String, Address> map = new HashMap<String, Address>();
        for(Entry entry : adaptedMap.entry) {
            map.put(entry.key, entry.value);
        }
        return map;
    }
 
    @Override
    public AdaptedMap marshal(Map<String, Address> map) throws Exception {
        AdaptedMap adaptedMap = new AdaptedMap();
        for(Map.Entry<String, Address> mapEntry : map.entrySet()) {
            Entry entry = new Entry();
            entry.key = mapEntry.getKey();
            entry.value = mapEntry.getValue();
            adaptedMap.entry.add(entry);
        }
        return adaptedMap;
    }
 
}

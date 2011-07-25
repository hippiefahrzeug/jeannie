package com.sb.jeannie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeyValuePrettyPrinter {
	private Map<String, String> store;
	
	public KeyValuePrettyPrinter() {
		store = new HashMap<String, String>();
	}
	
	public void add(String key, String value) {
		store.put(key, value);
	}
	
	public List<String> prettyPrint() {
		List<String> result = new ArrayList<String>();
		Set<String> keyset = store.keySet();
		ArrayList<String> keys = new ArrayList<String>(keyset);
		Collections.sort(keys);

    	int ml = 0;
    	for (String key : keys) {
    		ml = key.length() > ml ? key.length() : ml;
		}
    	
    	for (String key : keys) {
    		String t = String.format("%-" + ml + "s = %s", key, store.get(key));
			result.add(t);
		}

		return result;
	}
}

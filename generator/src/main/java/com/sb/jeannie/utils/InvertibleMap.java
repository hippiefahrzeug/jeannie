package com.sb.jeannie.utils;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvertibleMap<K, V> extends HashMap<K, V> {
    private final static Logger LOG = LoggerFactory.getLogger(InvertibleMap.class);
	private static final long serialVersionUID = -3107250520319810105L;
	private HashMap<V, K> inverse = new HashMap<V, K>();
	
	public V put(K key, V value) {
		if (inverse.containsKey(value)) {
			K k = inverse.get(value);
			LOG.error("offending key: {}", k);
			LOG.error("offending value: {}", value);
			LOG.error("value already exists and will be overwritten by the new value!");
			LOG.error("NOTE: value exist more than once and thus");
			LOG.error("      I can't uniquely associate a key.");
			LOG.error("HINT: make sure that input values only exist once");
			LOG.error("      if you need to retrieve keys by their value.");
		}
		inverse.put(value, key);
		return super.put(key, value);
	}
	
	public K getInverse(V value) {
		return inverse.get(value);
	}
}

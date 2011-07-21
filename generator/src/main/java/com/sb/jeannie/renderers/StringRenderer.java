package com.sb.jeannie.renderers;

import java.util.Locale;

import org.stringtemplate.v4.AttributeRenderer;

public class StringRenderer implements AttributeRenderer {

	public String toString(Object o, String formatString, Locale locale) {
		if (o == null || formatString == null) {
			return (String)o;
		}
		if (formatString.equals("uc")) {
			String t = o.toString();
			String res = t.substring(0, 1).toUpperCase() + t.substring(1);
			return res;
		}
		if (formatString.equals("toUpperCase")) {
			String t = o.toString();
			String res = t.toUpperCase();
			return res;
		}
		return o.toString();
	}

}

package org.calavera.lighthouseApi.resources;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.restlet.data.Form;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractResource {
    
    private static final Pattern PATTERN = Pattern.compile(
    "(\\d{4})(?:-(\\d{2}))?(?:-(\\d{2}))?(?:([Tt])?(?:(\\d{2}))?(?::(\\d{2}))?(?::(\\d{2}))?(?:\\.(\\d{3}))?)?([Zz])?(?:([+-])(\\d{2}):(\\d{2}))?");
    
    public abstract Form asForm();
    
    public static AbstractResource instance(Node node) {
    	AbstractResource resource = null;
    	if (node.getNodeName().equals("project")) {
    		resource = new Project(node);
    	} else if (node.getNodeName().equals("ticket")) {
    		resource = new Ticket(node);
    	} else if (node.getNodeName().equals("changeset")) {
    		resource = new Changeset(node);
    	}
    	return resource;
    }
    
    protected String getNodeContent(Node node, String key) {
        NodeList elementList = ((Element)node).getElementsByTagName(key);
        if (elementList != null && elementList.getLength() > 0
                && elementList.item(0).getTextContent().length() > 0) {
            return elementList.item(0).getTextContent();
        }
        return null;
    }
    
    protected synchronized Date parse(String date) {
        if (date == null) return null;
        
		Matcher m = PATTERN.matcher(date);
		if (m.find()) {
			if(m.group(4)==null) 
				throw new IllegalArgumentException("Invalid Date Format");
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			int hoff = 0, moff = 0, doff = -1;
			if (m.group(10) != null) {
				doff = m.group(10).equals("-") ? 1 : -1;
				hoff = doff * (m.group(11) != null ? Integer.parseInt(m.group(11)) : 0);
				moff = doff * (m.group(12) != null ? Integer.parseInt(m.group(12)) : 0);
			}
			c.set(Calendar.YEAR,        Integer.parseInt(m.group(1)));
			c.set(Calendar.MONTH,       m.group(2) != null ? Integer.parseInt(m.group(2))-1 : 0);
			c.set(Calendar.DATE,        m.group(3) != null ? Integer.parseInt(m.group(3)) : 1);
			c.set(Calendar.HOUR_OF_DAY, m.group(5) != null ? Integer.parseInt(m.group(5)) + hoff: 0);
			c.set(Calendar.MINUTE,      m.group(6) != null ? Integer.parseInt(m.group(6)) + moff: 0);
			c.set(Calendar.SECOND,      m.group(7) != null ? Integer.parseInt(m.group(7)) : 0);
			c.set(Calendar.MILLISECOND, m.group(8) != null ? Integer.parseInt(m.group(8)) : 0);
			return c.getTime();
		} else {
			throw new IllegalArgumentException("Invalid Date Format");
		}
	}
	
	protected static String format(Date date) {
	    if (date == null) return null;
	    
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTime(date);
        sb.append(c.get(Calendar.YEAR));
        sb.append('-');
        int f = c.get(Calendar.MONTH);
        if (f < 9) sb.append('0');
        sb.append(f+1);
        sb.append('-');
        f = c.get(Calendar.DATE);
        if (f < 10) sb.append('0');
        sb.append(f);
        sb.append('T');
        f = c.get(Calendar.HOUR_OF_DAY);
        if (f < 10) sb.append('0');
        sb.append(f);
        sb.append(':');
        f = c.get(Calendar.MINUTE);
        if (f < 10) sb.append('0');
        sb.append(f);
        sb.append(':');
        f = c.get(Calendar.SECOND);
        if (f < 10) sb.append('0');
        sb.append(f);
        sb.append('.');
        f = c.get(Calendar.MILLISECOND);
        if (f < 100) sb.append('0');
        if (f < 10) sb.append('0');
        sb.append(f);
        sb.append('Z');
        return sb.toString();
      }
}
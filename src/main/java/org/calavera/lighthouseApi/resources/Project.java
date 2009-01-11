package org.calavera.lighthouseApi.resources;

import org.restlet.data.Form;

import org.w3c.dom.Node;

public class Project extends AbstractResource {
	
	public final int id;
	public final String name;
	
	public Project(Node node) {
		id = Integer.valueOf(getNodeContent(node, "id"));
		name = getNodeContent(node, "name");
	}
	
	public Form asForm() {
	    return null;
    }
}

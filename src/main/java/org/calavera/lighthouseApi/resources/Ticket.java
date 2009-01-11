package org.calavera.lighthouseApi.resources;

import java.util.Date;

import org.restlet.data.Form;

import org.w3c.dom.Node;

public class Ticket extends AbstractResource {
	
	public final int number;
	public final int projectId;
	public final String permalink;
	public final Date createdAt;
	public String title;
	public String body;
	public String state;
	
	public Ticket(Node node) {
		number = Integer.valueOf(getNodeContent(node, "number"));
		projectId = Integer.valueOf(getNodeContent(node, "project-id"));
		permalink = getNodeContent(node, "permalink");
		title = getNodeContent(node, "title");
		body = getNodeContent(node, "body");
		createdAt = parse(getNodeContent(node, "created-at"));
		state = getNodeContent(node, "state");
	}
	
	public Form asForm() {
	    Form form = new Form();
        form.add("ticket[state]", state);
        form.add("ticket[title]", title);
        form.add("ticket[body]", body);
        return form;
	}
	
	public String getLink() {
	    return String.format("/projects/%d/tickets/%d-%s", projectId, number, permalink);
	}
}

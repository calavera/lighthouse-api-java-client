package org.calavera.lighthouseApi.resources;

import java.util.Date;

import org.restlet.data.Form;

import org.w3c.dom.Node;

public class Changeset extends AbstractResource {
    
    public int revision;
    public final int projectId;
    public String title;
    public String body;
    public String changes;
    public Date changedAt;
    
    public Changeset(Node node) {
        revision = Integer.valueOf(getNodeContent(node, "revision"));
        projectId = Integer.valueOf(getNodeContent(node, "project-id"));
        title = getNodeContent(node, "title");
        body = getNodeContent(node, "body");
        changes = getNodeContent(node, "changes");
        changedAt = parse(getNodeContent(node, "changed-at"));
    }
    
    public Form asForm() {
	    Form form = new Form();
	    form.add("changeset[revision]", String.valueOf(revision));
        form.add("changeset[title]", title);
        form.add("changeset[body]", body);
        form.add("changeset[changes]", changes);
        form.add("changeset[changed-at]", format(changedAt));
        return form;
	}
}
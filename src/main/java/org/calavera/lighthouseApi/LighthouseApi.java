package org.calavera.lighthouseApi;

import java.util.ArrayList;
import java.util.Collection;

import org.calavera.lighthouseApi.resources.AbstractResource;
import org.calavera.lighthouseApi.resources.Changeset;
import org.calavera.lighthouseApi.resources.Project;
import org.calavera.lighthouseApi.resources.Ticket;
import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.SaxRepresentation;
import org.w3c.dom.Node;

public class LighthouseApi {
	
	private final String lighthouseUrl;
	private final String account;
	private String username;
	private String password;
	private String token;
	private Client client = new Client(Protocol.HTTP);
	
	public LighthouseApi(String account) {
		this.account = account;
		this.lighthouseUrl = String.format("http://%s.lighthouseapp.com", this.account);
	}
	
	public void addCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public void addCredentials(String token) {
		this.token = token;
		this.username = this.token;
		this.password = "x";
	}
	
	public String getLighthouseUrl() {
	    return lighthouseUrl;
	}
	
	public Collection<Project> getProjects() throws Exception {
		Request request = getRequest(Method.GET, lighthouseUrl + "/projects.xml");
		
		return getNodes(request, "//project");
	}
	
	public Project getProject(int id) throws Exception {
		Request request = getRequest(Method.GET,
				String.format("%s/projects/%d.xml", lighthouseUrl, id));
		
		return (Project) getNode(request, "//project");
	}
	
	public Collection<Ticket> getTickets(int projectId) {
		Request request = getRequest(Method.GET,
			String.format("%s/projects/%d/tickets.xml?q=all", lighthouseUrl, projectId));
		
		return getNodes(request, "//ticket");		
	}
	
	public Ticket getTicket(int projectId, int ticketNumber) {
	    Request request = getRequest(Method.GET,
	        String.format("%s/projects/%d/tickets/%d.xml", lighthouseUrl, projectId, ticketNumber));
	        
	    return (Ticket) getNode(request, "//ticket");
	}
	
	public int putTicket(Ticket ticket) {
	    Request request = getRequest(Method.PUT, 
	        String.format("%s/projects/%d/tickets/%d.xml", lighthouseUrl, ticket.projectId, ticket.number),
	        ticket.asForm().getWebRepresentation());
	    
	    Response response = getResponse(request);
	    return response.getStatus().getCode();
	}
	
	public Collection<Changeset> getChangesets(int projectId) {
	    Request request = getRequest(Method.GET,
			String.format("%s/projects/%d/changesets.xml", lighthouseUrl, projectId));
		
		return getNodes(request, "//changeset");
	}
	
	public Changeset initChangeset(int projectId) {
	    Request request = getRequest(Method.GET,
			String.format("%s/projects/%d/changesets/new.xml", lighthouseUrl, projectId));
		
		return (Changeset) getNode(request, "//changeset");
	}
	
	public int postChangeset(Changeset changeset) {
	    Request request = getRequest(Method.POST,
			String.format("%s/projects/%d/changesets.xml", lighthouseUrl, changeset.projectId),
			changeset.asForm().getWebRepresentation());

	    Response response = getResponse(request);
	    return response.getStatus().getCode();
	}
	
	private <T extends AbstractResource>Collection<T> getNodes(Request request, String xpath) {
		Collection<T> nodes = new ArrayList<T>();
		
		Response response = getResponse(request);
		if (response.getStatus().isSuccess()) {
			SaxRepresentation sax = response.getEntityAsSax();
			
			for (Node node : sax.getNodes(xpath)) {
				AbstractResource resource = AbstractResource.instance(node);
				nodes.add((T) resource);
			}
		}
		
		return nodes;
	}
	
	private AbstractResource getNode(Request request, String xpath) {
		AbstractResource resource = null;
		
		Response response = getResponse(request);
	    if (response.getStatus().isSuccess()) {
	        SaxRepresentation sax = response.getEntityAsSax();
	        resource = AbstractResource.instance(sax.getNode(xpath));
	    }
	    
	    return resource;
	}
	
	private Response getResponse(Request request) {
		return client.handle(request);
	}
	
	private Request getRequest(Method method, String url) {
	    return getRequest(method, url, null);
	}
	
	private Request getRequest(Method method, String url, Representation representation) {
	    Request request = representation != null? new Request(method, url, representation) : new Request(method, url);
		
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme, username, password);  
		
		request.setChallengeResponse(authentication);
		
		return request;
	}
}

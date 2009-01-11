package org.calavera.lighthouseApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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
		Collection<Project> projects = new ArrayList<Project>();
		
		Request request = getRequest(Method.GET, lighthouseUrl + "/projects.xml");
		
		Response response = getResponse(request);
		if (response.getStatus().isSuccess()) { 
			SaxRepresentation sax = response.getEntityAsSax();
			
			for (Node node : sax.getNodes("//project")) {
				Project project = new Project(node);
				projects.add(project);
			}
		}

		return projects;
	}
	
	public Project getProject(int id) throws Exception {
		Project project = null;
		
		Request request = getRequest(Method.GET,
				String.format("%s/projects/%d.xml", lighthouseUrl, id));
		
		Response response = getResponse(request);
		if (response.getStatus().isSuccess()) {
			SaxRepresentation sax = response.getEntityAsSax();
			project = new Project(sax.getNode("//project"));
		}
		return project;
	}
	
	public Collection<Ticket> getTickets(int projectId) {
		Collection<Ticket> tickets = new ArrayList<Ticket>();
		
		Request request = getRequest(Method.GET,
			String.format("%s/projects/%d/tickets.xml?q=all", lighthouseUrl, projectId));
		
		Response response = getResponse(request);
		if (response.getStatus().isSuccess()) {
			SaxRepresentation sax = response.getEntityAsSax();
			
			for (Node node : sax.getNodes("//ticket")) {
				Ticket ticket = new Ticket(node);
				tickets.add(ticket);
			}
		}
		return tickets;
	}
	
	public Ticket getTicket(int projectId, int ticketNumber) {
	    Ticket ticket = null;
	    
	    Request request = getRequest(Method.GET,
	        String.format("%s/projects/%d/tickets/%d.xml", lighthouseUrl, projectId, ticketNumber));
	        
	    Response response = getResponse(request);
	    if (response.getStatus().isSuccess()) {
	        SaxRepresentation sax = response.getEntityAsSax();
	        ticket = new Ticket(sax.getNode("//ticket"));
	    }
	    
	    return ticket;
	}
	
	public int putTicket(Ticket ticket) {
	    Request request = getRequest(Method.PUT, 
	        String.format("%s/projects/%d/tickets/%d.xml", lighthouseUrl, ticket.projectId, ticket.number),
	        ticket.asForm().getWebRepresentation());
	    
	    Response response = getResponse(request);
	    return response.getStatus().getCode();
	}
	
	public Collection<Changeset> getChangesets(int projectId) {
	    Collection<Changeset> changesets = null;
	    
	    Request request = getRequest(Method.GET,
			String.format("%s/projects/%d/changesets.xml", lighthouseUrl, projectId));
		
		Response response = getResponse(request);
		if (response.getStatus().isSuccess()) {
			SaxRepresentation sax = response.getEntityAsSax();
			
			for (Node node : sax.getNodes("//ticket")) {
				Changeset changeset = new Changeset(node);
				changesets.add(changeset);
			}
		}
	    
	    return changesets;
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

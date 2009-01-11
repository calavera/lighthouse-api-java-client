import org.calavera.lighthouseApi.LighthouseApi

scenario "Accesing to the Lighthouse projects", {
	given "an account and a token", {
		lighthouse = new LighthouseApi("put-here-your-accout")
		lighthouse.addCredentials("put-here-your-api-token")
	}
	
	when "access to the resources list", {
		projects = lighthouse.getProjects()
	}
	
	then "the projects collection should not be empty", {
		projects.size.shouldBe 2
	}
	
	when "use a valid id", {
		project = lighthouse.getProject(projects.iterator().next().id)
	}
	
	then "project should not be null", {
		project.shouldNotBe null
		project.name.shouldNotBe null
	}
	
	when "tickets are requested", {
		tickets = lighthouse.getTickets(project.id)
	}
	
	then "tickets should not be empty", {
		tickets.size.shouldNotBe 0
	}
	
	when "a specific ticket is requested", {
	    ticket = lighthouse.getTicket(project.id, tickets.iterator().next().number)
	}
	
	then "ticket should not be empty", {
	    ticket.shouldNotBe null
	    ticket.createdAt.shouldNotBe null
	    ticket.state.shouldNotBe null
	    ticket.permalink.shouldNotBe null
	}
	
	when "modify a ticket", {
	    ticket.state = 'open'
	    status = lighthouse.putTicket(ticket)
	}
	
	then "status code should be 200", {
	    status.shouldBe 200
	}
}
import org.calavera.lighthouseApi.LighthouseApi

scenario "test the tickets api", {
    given "a lighthouse account and a project", {
        lighthouse = new LighthouseApi("put-here-your-accout")
        lighthouse.addCredentials("put-here-your-api-token")
		
		project = lighthouse.getProjects().iterator().next()
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
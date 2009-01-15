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
}
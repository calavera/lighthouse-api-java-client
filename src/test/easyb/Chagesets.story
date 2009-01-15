import java.util.Date

import org.calavera.lighthouseApi.LighthouseApi
import org.calavera.lighthouseApi.resources.Changeset

scenario "test the changesets api", {
    given "a lighthouse account and a project", {
        lighthouse = new LighthouseApi("put-here-your-accout")
        lighthouse.addCredentials("put-here-your-api-token")
		
		project = lighthouse.getProjects().iterator().next()
    }
    
    when "retrieve the list of changests", {
        changesets = lighthouse.getChangesets(project.id)
    }
    
    then "the list should be empty", {
        changesets.shouldNotBe null
    }
    
    when "a new changeset is posted", {
        changeset = lighthouse.initChangeset(project.id)
        changeset.revision = changesets.size + 1
        changeset.changedAt = new Date()
        changeset.title = 'new changeset from the java client'
        status = lighthouse.postChangeset(changeset)
    }
    
    then "the status code should be 201", {
        status.shouldBe 201
    }
    
    and "then the list size should be 1", {
        previous_size = changesets.size
        changesets = lighthouse.getChangesets(project.id)
        changesets.size.shouldBeEqual previous_size+1
    }
    
}

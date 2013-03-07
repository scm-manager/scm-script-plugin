importPackage(Packages.sonia.scm.repository);
importPackage(Packages.sonia.scm.repository.api);

var repositoryManager = injector.getInstance(RepositoryManager);
var repositoryServiceFactory = injector.getInstance(RepositoryServiceFactory);

var repositories = repositoryManager.getAll();
for ( var i=0; i<repositories.size(); i++ ){
    var repository = repositories.get(i);
    println("<b>" + repository.type + "/" + repository.name + "</b>");
    
    var repositoryService = repositoryServiceFactory.create(repository);
    var changesets = repositoryService.getLogCommand()
                                      .setPagingLimit(5)
                                      .getChangesets()
                                      .getChangesets();
    
    for ( var j=0; j<changesets.size(); j++){
        var changeset = changesets.get(j);
        println(" - " + changeset.description);
    }
    repositoryService.close();
}
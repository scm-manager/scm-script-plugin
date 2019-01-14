// @flow
import type { Sample } from "../../types";

const samples: Sample[] = [
  {
    title: "List installed plugins",
    description:
      "This script uses the plugin api to list all installed plugins",
    type: "Groovy",
    content: `import sonia.scm.plugin.*;

PluginManager pluginManager = injector.getInstance(PluginManager.class);
for (PluginInformation plugin : pluginManager.installed) {
    println "\${plugin.name}@\${plugin.version}"
}`
  },
  {
    title: "Create a new repository",
    description: "How to create a new git repository with a script",
    type: "Groovy",
    content: `import sonia.scm.repository.*;

RepositoryManager repositoryManager = injector.getInstance(RepositoryManager.class);

Repository repository = new Repository();
repository.setType("git");
repository.setName("created-from-script");
repository.setDescription("This repository was created via a groovy script");

Repository created = repositoryManager.create(repository);

println "created repository \${created.namespace}/\${created.name}";`
  }
];

export default samples;

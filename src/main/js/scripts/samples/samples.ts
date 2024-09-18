/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import { Sample } from "../../types";

const samples: Sample[] = [
  {
    title: "List installed plugins",
    description: "This script uses the plugin api to list all installed plugins",
    type: "Groovy",
    content: `import sonia.scm.plugin.*;

PluginManager pluginManager = injector.getInstance(PluginManager.class);
for (InstalledPlugin plugin : pluginManager.installed) {
    PluginInformation information = plugin.descriptor.information
    println "\${information.name}@\${information.version}"
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
  },
  {
    title: "Restart",
    description: "To restart the server, you can use this script",
    type: "Groovy",
    content: `import sonia.scm.lifecycle.Restarter

def restarter = injector.getInstance(Restarter)
println "restarting SCM-Manager"
restarter.restart(Script, "Manual restart from groovy script")`
  },
  {
    title: "Clear Caches",
    description: "How to clear all caches (like caches for source listings, commits, etc.)",
    type: "Groovy",
    content: `import sonia.scm.cache.CacheManager

def cacheManager = injector.getInstance(CacheManager)
def caches = cacheManager.caches.values()

for (def cache : caches) {
  println "clear \${cache.size()} entries from cache \${cache.name}"
  cache.clear()
}`
  }
];

export default samples;

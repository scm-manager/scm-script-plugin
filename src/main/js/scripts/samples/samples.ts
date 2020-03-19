///
/// MIT License
///
/// Copyright (c) 2020-present Cloudogu GmbH and Contributors
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
///

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
  }
];

export default samples;

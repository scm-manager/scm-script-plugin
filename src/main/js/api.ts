/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import { apiClient } from "@scm-manager/ui-components";
import { ExecutionHistoryEntry, Listeners, Script, ScriptExecutionResult, ScriptLinks } from "./types";
import { Links } from "@scm-manager/ui-types";

// modifying headers is not supported in the apiclient
export function run(link: string, language: string, content: string): Promise<ScriptExecutionResult> {
  const headers: Record<string, string> = {
    "Accept": "application/vnd.scmm-script-execution-result+json;v=2"
  };
  return apiClient.postText(link + "?lang=" + language, content, headers)
    .then(resp => resp.json());
}

export function store(link: string, script: Script) {
  return apiClient.post(link, script, "application/vnd.scmm-script+json;v=2");
}

export function modify(link: string, script: Script) {
  return apiClient.put(link, script, "application/vnd.scmm-script+json;v=2");
}

export function remove(link: string) {
  return apiClient.delete(link);
}

export function findById(id: string) {
  return apiClient.get("/plugins/scripts/" + id).then(resp => resp.json());
}

export function findAll(link: string) {
  return apiClient.get(link).then(resp => resp.json());
}

export function findAllListeners(link: string): Promise<Listeners> {
  return apiClient.get(link).then(resp => resp.json());
}

export function storeListeners(link: string, listeners: Listeners): Promise<void> {
  return apiClient.put(link, listeners, "application/vnd.scmm-script-listener-collection+json;v=2");
}

export function findAllEventTypes(link: string): Promise<string[]> {
  return apiClient
    .get(link)
    .then(resp => resp.json())
    .then(collection => collection.eventTypes);
}

export function createScriptLinks(list: string, links: Links): ScriptLinks {
  const scriptLinks = {
    list
  };

  for (const rel in links) {
    scriptLinks[rel] = links[rel].href;
  }
  return scriptLinks;
}

export function findAllScriptLinks(indexLink: string): Promise<ScriptLinks> {
  // we need only _links and not the _embedded scripts
  return apiClient
    .get(indexLink + "?fields=_links")
    .then(resp => resp.json())
    .then(json => json._links)
    .then(links => createScriptLinks(indexLink, links));
}

export function findHistory(link: string): Promise<ExecutionHistoryEntry[]> {
  return apiClient
    .get(link)
    .then(resp => resp.json())
    .then(history => {
      return history.entries || [];
    });
}

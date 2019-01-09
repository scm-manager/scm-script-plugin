// @flow
import { apiClient, urls } from "@scm-manager/ui-components";
import type { Script } from "./types";

// TODO extend api client
export function run(language: string, content: string) {
  const url = urls.withContextPath(
    "/api/v2/plugins/scripts/run?lang=" + language
  );

  return fetch(url, {
    credentials: "same-origin",
    headers: {
      Cache: "no-cache",
      Accept: "text/plain",
      "Content-Type": "text/plain"
    },
    method: "POST",
    body: content
  }).then(resp => resp.text());
}

export function store(script: Script) {
  return apiClient.post(
    "/plugins/scripts",
    script,
    "application/vnd.scmm-script+json;v=2"
  );
}

export function findById(id: string) {
  return apiClient.get("/plugins/scripts/" + id).then(resp => resp.json());
}

export function findAll() {
  return apiClient.get("/plugins/scripts").then(resp => resp.json());
}

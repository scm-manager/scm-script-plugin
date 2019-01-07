// @flow
import { urls } from "@scm-manager/ui-components";

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

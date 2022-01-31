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
import {ExecutionHistoryEntry, Listeners, Script, ScriptCollection, ScriptExecutionResult, ScriptLinks} from "./types";
import { Link, Links } from "@scm-manager/ui-types";
import { useMutation, useQuery, useQueryClient } from "react-query";

const SCRIPT_CONTENT_TYPE = "application/vnd.scmm-script+json;v=2";
const LISTENERS_TYPE = "application/vnd.scmm-script-listener-collection+json;v=2";

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

// React-Query

const getScriptCacheKey = (id?: string) => ["script", id || "NEW"];
const getScriptsCacheKey = () => ["scripts"];
const getListenersCacheKey = () => ["scripts", "listeners"];
const getEventTypesCacheKey = () => ["scripts", "eventTypes"];

export const useScript = (id: string) => {
  const { error, isLoading, data } = useQuery<Script, Error>(getScriptCacheKey(id), () =>
    apiClient.get("/plugins/scripts/" + id).then(resp => resp.json())
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useScripts = (link: string) => {
  const { error, isLoading, data } = useQuery<ScriptCollection, Error>(getScriptsCacheKey(), () =>
    apiClient.get(link).then(resp => resp.json())
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useListeners = (link: string) => {
  const { error, isLoading, data } = useQuery<Listeners, Error>(getListenersCacheKey(), () =>
    apiClient.get(link).then(resp => resp.json())
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useEventTypes = (link: string) => {
  const { error, isLoading, data } = useQuery<string[], Error>(getEventTypesCacheKey(), () =>
    apiClient.get(link).then(resp => resp.json()).then(collection => collection.eventTypes)
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useUpdateScript = (script: Script) => {
  const queryClient = useQueryClient();
  const { isLoading, error, mutate } = useMutation<unknown, Error, Script>(
    s => {
      return apiClient.put((script._links.update as Link).href, s, SCRIPT_CONTENT_TYPE);
    },
    {
      onSuccess: () => {
        return queryClient.invalidateQueries(getScriptCacheKey(script.id));
      }
    }
  );

  return {
    isLoading,
    error,
    update: (s: Script) => mutate(s)
  };
};

export const useRunScript = (link: string, callback?: (result: ScriptExecutionResult) => void) => {
  const { isLoading, error, mutate } = useMutation<unknown, Error, Script>(async s => {
    const headers: Record<string, string> = {
      Accept: "application/vnd.scmm-script-execution-result+json;v=2"
    };
    const result: ScriptExecutionResult = await apiClient
      .postText(link + "?lang=" + s.type, s.content || "", headers)
      .then(resp => resp.json());
    if (callback) {
      callback(result);
    }
    return result;
  });

  return {
    isLoading,
    error,
    run: (s: Script) => mutate(s)
  };
};

export const useDeleteScript = (script: Script, callback?: () => void) => {
  const queryClient = useQueryClient();
  const { isLoading, error, mutate } = useMutation<unknown, Error, void>(
    () => {
      const response = apiClient.delete((script._links.delete as Link).href);
      if (callback) {
        callback();
      }
      return response;
    },
    {
      onSuccess: () => {
        return queryClient.removeQueries(getScriptCacheKey(script.id));
      }
    }
  );

  return {
    isLoading,
    error,
    deleteScript: () => mutate()
  };
};

export const useStoreScript = (link: string, callback?: (id: string) => void) => {
  const { isLoading, error, mutate } = useMutation<unknown, Error, Script>((script: Script) => {
    return apiClient
      .post(link, script, SCRIPT_CONTENT_TYPE)
      .then(resp => resp.headers.get("Location"))
      .then(location => apiClient.get(location!))
      .then(resp => resp.json())
      .then((s: Script) => {
        if (callback) {
          callback(s?.id!);
        }
      });
  });

  return {
    isLoading,
    error,
    store: (script: Script) => mutate(script)
  };
};

export const useStoreListeners = (link: string) => {
  const queryClient = useQueryClient();
  const { isLoading, error, mutate } = useMutation<unknown, Error, Listeners>(
    listeners => {
      return apiClient.put(link, listeners, LISTENERS_TYPE);
    },
    {
      onSuccess: () => {
        return queryClient.invalidateQueries(getListenersCacheKey());
      }
    }
  );

  return {
    isLoading,
    error,
    store: (l: Listeners) => mutate(l)
  };
};

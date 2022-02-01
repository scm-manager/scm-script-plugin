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
import {
  ExecutionHistoryEntry,
  Listeners,
  Script,
  ScriptCollection,
  ScriptExecutionResult,
  ScriptLinks
} from "./types";
import { Link } from "@scm-manager/ui-types";
import { useMutation, useQuery, useQueryClient } from "react-query";

const SCRIPT_CONTENT_TYPE = "application/vnd.scmm-script+json;v=2";
const SCRIPT_EXECUTION_TYPE = "application/vnd.scmm-script-execution-result+json;v=2";
const LISTENERS_TYPE = "application/vnd.scmm-script-listener-collection+json;v=2";

const getScriptCacheKey = (id?: string) => ["script", id || "NEW"];
const getScriptHistoryCacheKey = (id: string) => ["script", id, "history"];
const scriptsCacheKey = ["scripts"];
const scriptLinksCacheKey = ["scripts", "links"];
const listenersCacheKey = ["scripts", "listeners"];
const eventTypesCacheKey = ["scripts", "eventTypes"];

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
  const { error, isLoading, data } = useQuery<ScriptCollection, Error>(scriptsCacheKey, () =>
    apiClient.get(link).then(resp => resp.json())
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useScriptLinks = (link: string) => {
  const { error, isLoading, data } = useQuery<ScriptLinks, Error>(scriptLinksCacheKey, () =>
    apiClient
      .get(link + "?fields=_links")
      .then(resp => resp.json())
      .then(json => json._links)
      .then(links => {
        return {
          list: link,
          eventTypes: (links["eventTypes"] as Link).href,
          create: (links["create"] as Link)?.href,
          execute: (links["execute"] as Link)?.href
        };
      })
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useListeners = (link: string) => {
  const { error, isLoading, data } = useQuery<Listeners, Error>(listenersCacheKey, () =>
    apiClient.get(link).then(resp => resp.json())
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useEventTypes = (link: string) => {
  const { error, isLoading, data } = useQuery<string[], Error>(eventTypesCacheKey, () =>
    apiClient
      .get(link)
      .then(resp => resp.json())
      .then(collection => collection.eventTypes)
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useScriptHistory = (script: Script) => {
  if (!script.id) {
    throw new Error("Missing script id");
  }
  if (!script._links.history) {
    throw new Error("Script history link missing");
  }

  const { error, isLoading, data } = useQuery<ExecutionHistoryEntry[], Error>(getScriptHistoryCacheKey(script.id), () =>
    apiClient
      .get((script._links.history as Link).href)
      .then(resp => resp.json())
      .then(history => {
        return history.entries || [];
      })
  );

  return {
    error,
    isLoading,
    data
  };
};

export const useUpdateScript = (script: Script) => {
  if (!script._links.update) {
    throw new Error("Script update link missing");
  }
  const queryClient = useQueryClient();
  const { isLoading, error, mutate } = useMutation<unknown, Error, Script>(
    s => {
      return apiClient.put((script._links.update as Link).href, s, SCRIPT_CONTENT_TYPE);
    },
    {
      onSuccess: async () => {
        await Promise.all([
          queryClient.invalidateQueries(scriptsCacheKey),
          queryClient.invalidateQueries(getScriptCacheKey(script.id))
        ]);
      }
    }
  );

  return {
    isLoading,
    error,
    update: (s: Script) => mutate(s)
  };
};

export const useRunScript = (link?: string, callback?: (result: ScriptExecutionResult) => void) => {
  if (!link) {
    throw new Error("Missing script execution link");
  }

  const { isLoading, error, mutate } = useMutation<unknown, Error, Script>(s => {
    const headers: Record<string, string> = {
      Accept: SCRIPT_EXECUTION_TYPE
    };
    return apiClient
      .postText(link + "?lang=" + s.type, s.content || "", headers)
      .then(resp => resp.json())
      .then(result => {
        if (callback) {
          callback(result);
        }
        return result;
      });
  });

  return {
    isLoading,
    error,
    run: (s: Script) => mutate(s)
  };
};

export const useDeleteScript = (script: Script, callback?: () => void) => {
  if (!script._links.delete) {
    throw new Error("Script deletion link missing");
  }
  const queryClient = useQueryClient();
  const { isLoading, error, mutate } = useMutation<unknown, Error, void>(
    () => {
      return apiClient.delete((script._links.delete as Link).href).then(response => {
        if (callback) {
          callback();
        }
        return response;
      });
    },
    {
      onSuccess: () => {
        queryClient.removeQueries(getScriptCacheKey(script.id));
        return queryClient.invalidateQueries(scriptsCacheKey);
      }
    }
  );

  return {
    isLoading,
    error,
    deleteScript: () => mutate()
  };
};

export const useStoreScript = (link?: string, callback?: (id: string) => void) => {
  if (!link) {
    throw new Error("Script update link missing");
  }
  const { isLoading, error, mutate } = useMutation<unknown, Error, Script>((script: Script) => {
    return apiClient
      .post(link, script, SCRIPT_CONTENT_TYPE)
      .then(resp => resp.headers.get("Location"))
      .then(location => {
        if (!location) {
          throw new Error("Could not fetch stored script");
        }
        return apiClient.get(location);
      })
      .then(resp => resp.json())
      .then((s: Script) => {
        if (callback) {
          if (!s.id) {
            throw new Error("Missing script id");
          }
          callback(s.id);
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
    listeners => apiClient.put(link, listeners, LISTENERS_TYPE),
    {
      onSuccess: () => queryClient.invalidateQueries(listenersCacheKey)
    }
  );

  return {
    isLoading,
    error,
    store: (l: Listeners) => mutate(l)
  };
};

// @flow
import { apiClient } from "@scm-manager/ui-components";
import type {
  Listeners,
  Script,
  ScriptExecutionResult,
  ScriptLinks
} from "./types";
import type { Links } from "@scm-manager/ui-types";

// modifying headers is not supported in the apiclient
export function run(
  link: string,
  language: string,
  content: string
): Promise<ScriptExecutionResult> {
  return fetch(link + "?lang=" + language, {
    credentials: "same-origin",
    headers: {
      Cache: "no-cache",
      Accept: "application/vnd.scmm-script-execution-result+json;v=2",
      "Content-Type": "text/plain"
    },
    method: "POST",
    body: content
  }).then(resp => resp.json());
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

export function storeListeners(
  link: string,
  listeners: Listeners
): Promise<void> {
  return apiClient.put(
    link,
    listeners,
    "application/vnd.scmm-script-listener-collection+json;v=2"
  );
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

  for (let rel in links) {
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

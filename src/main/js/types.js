// @flow
import type { Collection, Links } from "@scm-manager/ui-types";

export type Sample = {
  title: string,
  description: string,
  type: string,
  content: string
};

export type Script = {
  id?: string,
  title?: string,
  description?: string,
  type: string,
  content?: string,
  _links: Links
};

export type ScriptCollection = Collection & {
  _embedded: {
    scripts: Script[]
  }
};

export type ScriptLinks = {
  list: string,
  eventTypes: string,
  create?: string,
  execute?: string
};

export type ScriptExecutionResult = {
  success: boolean,
  output: string,
  started: string,
  ended: string
};

export type Listeners = {
  listeners: Listener[],
  storeListenerExecutionResults: boolean,
  _links: Links
};

export type Listener = {
  eventType: string,
  asynchronous: boolean
};

export type ExecutionHistoryEntry = {
  listener: Listener,
  result: ScriptExecutionResult
};

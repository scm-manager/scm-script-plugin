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
  create?: string,
  execute?: string
};

export type ScriptExecutionResult = {
  success: boolean,
  output: string,
  started: string,
  ended: string
};

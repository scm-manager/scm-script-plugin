// @flow
import type { Collection, Links } from "@scm-manager/ui-types";

export type Script = {
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

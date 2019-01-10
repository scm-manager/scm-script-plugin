// @flow
import type { Collection } from "@scm-manager/ui-types";

export type Script = {
  title?: string,
  description?: string,
  type: string,
  content?: string
};

export type ScriptCollection = Collection & {
  _embedded: {
    scripts: Script[]
  }
};

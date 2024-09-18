/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import { HalRepresentation, Links } from "@scm-manager/ui-types";

export type Sample = {
  title: string;
  description: string;
  type: string;
  content: string;
};

export type Script = {
  id?: string;
  title?: string;
  description?: string;
  type: string;
  content?: string;
  _links: Links;
};

export type ScriptCollection = HalRepresentation & {
  _embedded: {
    scripts: Script[];
  };
};

export type ScriptLinks = {
  list: string;
  eventTypes: string;
  create?: string;
  execute?: string;
};

export type ScriptExecutionResult = {
  success: boolean;
  output: string;
  started: string;
  ended: string;
};

export type Listener = {
  eventType: string;
  asynchronous: boolean;
};

export type Listeners = {
  listeners: Listener[];
  storeListenerExecutionResults: boolean;
  _links: Links;
};

export type ExecutionHistoryEntry = {
  listener: Listener;
  result: ScriptExecutionResult;
};

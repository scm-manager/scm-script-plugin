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

import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { Checkbox, ErrorNotification, Loading } from "@scm-manager/ui-components";
import { useEventTypes, useListeners, useStoreListeners } from "../api";
import { Listener, Listeners, Script, ScriptLinks } from "../types";
import ListenersTable from "../components/ListenersTable";
import { Link } from "@scm-manager/ui-types";
import { useDocumentTitle } from "@scm-manager/ui-core";

type Props = {
  script: Script;
  links: ScriptLinks;
};

type PageProps = {
  listeners: Listeners;
  eventTypes: string[]
};

export const ListenersRoot: FC<Props> = ({ script, links }) => {
  const { error: listenersError, isLoading: listenersLoading, data: listeners } = useListeners((script._links.listeners as Link).href);
  const { error: eventTypesError, isLoading: eventTypesLoading, data: eventTypes } = useEventTypes(links.eventTypes);
  const [t] = useTranslation("plugins");
  useDocumentTitle(
    t("scm-script-plugin.scriptTab.listenersTitle", { name: script.title }),
    t("scm-script-plugin.navigation.stored"),
    t("scm-script-plugin.rootPage.title")
  );

  const error = listenersError || eventTypesError;

  if (error) {
    return <ErrorNotification error={error} />;
  }
  if (listenersLoading || eventTypesLoading) {
    return <Loading />;
  }
  if (listeners && eventTypes) {
    return <ListenersPage listeners={listeners} eventTypes={eventTypes} />;
  }
  return null;
};

const ListenersPage: FC<PageProps> = ({ listeners, eventTypes }) => {
  const [t] = useTranslation("plugins");
  const { error: storeError, store } = useStoreListeners(
    (listeners._links.update as Link).href
  );
  const [asynchronous, setAsynchronous] = useState(true);
  const [eventType, setEventType] = useState<string | undefined>();

  const onToggleStoreListenerExecutionResultsChange = (storeListenerExecutionResults: boolean) => {
    return store({ ...listeners, storeListenerExecutionResults });
  };

  const onSubmit = () => {
    if (eventType) {
      listeners.listeners.push({
        eventType,
        asynchronous
      });
      store(listeners);
      setEventType("");
    }
  };

  const onDelete = (listener: Listener) => {
    const index = listeners.listeners.indexOf(listener);
    if (index >= 0) {
      listeners.listeners.splice(index, 1);
      store(listeners);
    }
  };

  return (
    <>
      <ListenersTable
        eventTypes={eventTypes}
        listeners={listeners}
        eventType={eventType}
        asynchronous={asynchronous}
        onChangeEventType={(value: string) => setEventType(value)}
        onChangeAsynchronous={(value: boolean) => setAsynchronous(value)}
        onSubmit={onSubmit}
        onDelete={onDelete}
      />
      <Checkbox
        name="storeListenerExecutionResults"
        label={t("scm-script-plugin.listeners.storeListenerExecutionResults")}
        checked={listeners.storeListenerExecutionResults}
        onChange={onToggleStoreListenerExecutionResultsChange}
      />
      {storeError ? <ErrorNotification error={storeError}/> : null}
    </>
  );
};

export default ListenersPage;

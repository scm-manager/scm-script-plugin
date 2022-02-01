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
import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { Checkbox, ErrorNotification, Loading } from "@scm-manager/ui-components";
import { useEventTypes, useListeners, useStoreListeners } from "../api";
import { Listener, Listeners, Script, ScriptLinks } from "../types";
import ListenersTable from "../components/ListenersTable";
import { Link } from "@scm-manager/ui-types";

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

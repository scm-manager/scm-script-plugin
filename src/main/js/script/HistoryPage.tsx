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

import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import { DateFromNow, ErrorNotification, Loading, Notification, useDocumentTitle } from "@scm-manager/ui-core";
import Output from "../components/Output";
import { useScriptHistory, useCurrentScript } from "../api";
import { Script } from "../types";

const Heading = styled.h2`
  font-weight: 700;
  font-size: 1.25rem !important;
`;

const HistoryPage: FC = () => {
  const [t] = useTranslation("plugins");
  const { data: script } = useCurrentScript();
  const { data: history, error, isLoading } = useScriptHistory(script as Script);
  useDocumentTitle(
    t("scm-script-plugin.scriptTab.historyTitle", { name: script.title }),
    t("scm-script-plugin.navigation.stored"),
    t("scm-script-plugin.rootPage.title"),
  );

  if (error) {
    return <ErrorNotification error={error} />;
  } else if (isLoading || history === undefined) {
    return <Loading />;
  }

  if (history.length === 0) {
    return <Notification type="info">{t("scm-script-plugin.listeners.noHistory")}</Notification>;
  }

  return (
    <>
      {history.map((entry, key) => (
        <div className="pb-4" key={key}>
          <Heading>
            <DateFromNow date={entry.result.started} />
          </Heading>
          <p>{entry.listener.eventType}</p>
          <Output result={entry.result} />
        </div>
      ))}
    </>
  );
};

export default HistoryPage;

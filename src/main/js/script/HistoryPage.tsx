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
import styled from "styled-components";
import { DateFromNow, ErrorNotification, Loading, Notification } from "@scm-manager/ui-components";
import { useScriptHistory } from "../api";
import { Script } from "../types";
import Output from "../components/Output";
import { useTranslation } from "react-i18next";

const Frame = styled.div`
  padding-bottom: 1rem;
`;

const Heading = styled.h2`
  font-weight: 700;
  font-size: 1.25rem !important;
`;

type Props = {
  script: Script;
};

const HistoryPage: FC<Props> = ({ script }) => {
  const [t] = useTranslation("plugins");
  const { data: history, error, isLoading } = useScriptHistory(script);

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
        <Frame key={key}>
          <Heading>
            <DateFromNow date={entry.result.started} />
          </Heading>
          <p>{entry.listener.eventType}</p>
          <Output result={entry.result} />
        </Frame>
      ))}
    </>
  );
};

export default HistoryPage;

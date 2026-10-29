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
import { ErrorNotification, Loading, Notification } from "@scm-manager/ui-core";
import { useDocumentTitle } from "@scm-manager/ui-core";
import { useScriptLinks, useScripts } from "../../api";
import ScriptTable from "./ScriptTable";
import { useIndexLink } from "@scm-manager/ui-api";

const Stored: FC = () => {
  const indexLink = useIndexLink("scripts");
  const { data: links, isLoading: linksLoading, error: linksError } = useScriptLinks(indexLink);
  const scriptsLink = links?.list;
  const { data: scriptsData, isLoading: scriptsLoading, error: scriptsError } = useScripts(scriptsLink);
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-script-plugin.navigation.stored"), t("scm-script-plugin.rootPage.title"));

  const error = linksError || scriptsError;
  const isLoading = linksLoading || scriptsLoading;

  if (error) {
    return <ErrorNotification error={error} />;
  } else if (isLoading) {
    return <Loading />;
  } else if (!scriptsData || scriptsData._embedded.scripts.length === 0) {
    return <Notification type="info">No stored scripts found</Notification>;
  } else {
    return <ScriptTable scripts={scriptsData._embedded.scripts} />;
  }
};

export default Stored;

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

import React, { FC, ReactNode } from "react";
import { useParams, Outlet } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useIndexLink } from "@scm-manager/ui-api";
import { ErrorNotification, Loading, Subtitle, Title } from "@scm-manager/ui-core";
import { useScript, useScriptLinks } from "../api";
import ScriptTabs from "./ScriptTabs";

const ScriptRoot: FC = () => {
  const [t] = useTranslation("plugins");

  const params = useParams<{ id: string }>();
  const indexLink = useIndexLink("scripts");

  const { data: links, isLoading: linksLoading, error: linksError } = useScriptLinks(indexLink);
  const { data: script, isLoading: scriptLoading, error: scriptError } = useScript(params.id);

  const error = linksError || scriptError;
  const isLoading = linksLoading || scriptLoading;

  let body: ReactNode;
  if (error) {
    body = <ErrorNotification error={error} />;
  } else if (isLoading) {
    body = <Loading />;
  } else if (script && links) {
    body = <Outlet />;
  }

  return (
    <>
      <Title title={t("scm-script-plugin.rootPage.title")} />
      <Subtitle subtitle={t("scm-script-plugin.rootPage.subtitle")} />
      <div className="content">
        <h3>{script?.title}</h3>
      </div>
      <ScriptTabs id={params.id} />
      {body}
    </>
  );
};

export default ScriptRoot;

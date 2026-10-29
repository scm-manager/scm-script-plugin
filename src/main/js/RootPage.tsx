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
import { Outlet } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useIndexLink } from "@scm-manager/ui-api";
import { ErrorNotification, Loading, Subtitle, Title } from "@scm-manager/ui-core";
import { useScriptLinks } from "./api";

const RootPage: FC = () => {
  const [t] = useTranslation("plugins");
  const indexLink = useIndexLink("scripts");
  const { isLoading, error } = useScriptLinks(indexLink);

  let body: ReactNode;
  if (error) {
    body = <ErrorNotification error={error} />;
  } else if (isLoading) {
    body = <Loading />;
  } else {
    body = <Outlet />;
  }

  return (
    <>
      <Title title={t("scm-script-plugin.rootPage.title")} />
      <Subtitle subtitle={t("scm-script-plugin.rootPage.subtitle")} />
      {body}
    </>
  );
};

export default RootPage;

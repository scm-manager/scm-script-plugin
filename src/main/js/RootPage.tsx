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
import { Route } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ErrorNotification, Loading, Subtitle, Title } from "@scm-manager/ui-components";
import ScriptRoot from "./script/ScriptRoot";
import MainRouting from "./scripts/MainRouting";
import { useScriptLinks } from "./api";

type Props = {
  link: string;
};

const RootPage: FC<Props> = ({ link }) => {
  const [t] = useTranslation("plugins");
  const { data: links, isLoading, error } = useScriptLinks(link);

  const createBody = () => {
    if (error) {
      return <ErrorNotification error={error} />;
    } else if (isLoading) {
      return <Loading />;
    } else if (links) {
      return (
        <>
          <Route path="/admin/scripts">
            <MainRouting links={links} />
          </Route>
          <Route path="/admin/script/:id">
            <ScriptRoot links={links} />
          </Route>
        </>
      );
    } else {
      return null;
    }
  };

  return (
    <>
      <Title title={t("scm-script-plugin.rootPage.title")} />
      <Subtitle subtitle={t("scm-script-plugin.rootPage.subtitle")} />
      {createBody()}
    </>
  );
};

export default RootPage;

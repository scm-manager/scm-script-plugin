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
import {RouteProps, useRouteMatch} from "react-router-dom";
import { useTranslation } from "react-i18next";
import { NavLink, SecondaryNavigationItem } from "@scm-manager/ui-components";

const ScriptNavigation: FC = () => {
  const match = useRouteMatch();
  const [t] = useTranslation("plugins");

  const matchesScript = (route: RouteProps) => {
    const regex = "/admin/script/.+";
    return !!route.location?.pathname.match(regex);
  };

  return (
    <SecondaryNavigationItem
      to={match.url + "/scripts/run"}
      icon="fas fa-file-code"
      label={t("scm-script-plugin.navLink")}
      title={t("scm-script-plugin.navLink")}
      activeWhenMatch={matchesScript}
      activeOnlyWhenExact={false}
    >
      <NavLink to={match.url + "/scripts/run"} label={t("scm-script-plugin.navigation.run")} />
      <NavLink
        to={match.url + "/scripts"}
        label={t("scm-script-plugin.navigation.stored")}
        activeWhenMatch={matchesScript}
        activeOnlyWhenExact={true}
      />
      <NavLink to={match.url + "/scripts/samples"} label={t("scm-script-plugin.navigation.samples")} />
    </SecondaryNavigationItem>
  );
};

export default ScriptNavigation;

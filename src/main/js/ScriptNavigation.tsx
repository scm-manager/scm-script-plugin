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
import { NavLink, SecondaryNavigationItem } from "@scm-manager/ui-components";
import { ADMIN_BASE_ROUTE, SCRIPT_BASE_ROUTE, SCRIPT_SUB_ROUTES } from "./routes";

const ScriptNavigation: FC = () => {
  const [t] = useTranslation("plugins");
  const base = `/${ADMIN_BASE_ROUTE}/${SCRIPT_BASE_ROUTE}/`;

  return (
    <SecondaryNavigationItem
      to={base + SCRIPT_SUB_ROUTES.RUN}
      icon="fas fa-file-code"
      label={t("scm-script-plugin.navLink")}
      title={t("scm-script-plugin.navLink")}
      activeWhenMatch={"/admin/scripts?(/.*)?$"}
    >
      <NavLink to={base + SCRIPT_SUB_ROUTES.RUN} label={t("scm-script-plugin.navigation.run")} />
      <NavLink
        to={base}
        label={t("scm-script-plugin.navigation.stored")}
        activeWhenMatch={"/admin/(script/.+|scripts/?)$"}
        matchWhenExact={true}
      />
      <NavLink to={base + SCRIPT_SUB_ROUTES.SAMPLES} label={t("scm-script-plugin.navigation.samples")} />
    </SecondaryNavigationItem>
  );
};

export default ScriptNavigation;

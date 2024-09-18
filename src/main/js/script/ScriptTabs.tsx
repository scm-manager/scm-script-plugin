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
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

type Props = {
  path: string;
};

export function isUrlSuffixMatching(baseURL: string, url: string, suffix: string) {
  let strippedUrl = url.substring(baseURL.length);
  if (strippedUrl.startsWith("/")) {
    strippedUrl = strippedUrl.substring(1);
  }
  const slash = strippedUrl.indexOf("/");
  if (slash >= 0) {
    strippedUrl = strippedUrl.substring(0, slash);
  }
  return strippedUrl === suffix;
}

const ScriptTabs: FC<Props> = ({ path }) => {
  const [t] = useTranslation("plugins");
  const location = useLocation();

  const navigationClass = (suffix: string) => {
    if (location && isUrlSuffixMatching(path, location.pathname, suffix)) {
      return "is-active";
    }
    return "";
  };

  return (
    <div className="tabs">
      <ul>
        <li className={navigationClass("")}>
          <Link to={path}>{t("scm-script-plugin.scriptTab.edit")}</Link>
        </li>
        <li className={navigationClass("listeners")}>
          <Link to={path + "/listeners"}>{t("scm-script-plugin.scriptTab.listeners")}</Link>
        </li>
        <li className={navigationClass("history")}>
          <Link to={path + "/history"}>{t("scm-script-plugin.scriptTab.history")}</Link>
        </li>
      </ul>
    </div>
  );
};

export default ScriptTabs;

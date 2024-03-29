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

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
import samples from "./samples";
import SamplePanel from "./SamplePanel";
import { useTranslation } from "react-i18next";
import { useDocumentTitle } from "@scm-manager/ui-core";

const SampleRoot: FC = () => {
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-script-plugin.navigation.samples"), t("scm-script-plugin.rootPage.title"));
  return (
    <>
      <div className="content">
        <h3>{t("scm-script-plugin.navigation.samples")}</h3>
        <hr />
      </div>
      {samples.map((sample, key) => (
        <SamplePanel sample={sample} key={key} />
      ))}
    </>
  );
};

export default SampleRoot;

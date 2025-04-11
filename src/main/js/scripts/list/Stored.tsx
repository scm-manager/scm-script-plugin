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
import { ErrorNotification, Loading, Notification } from "@scm-manager/ui-components";
import { useScripts } from "../../api";
import { ScriptLinks } from "../../types";
import ScriptTable from "./ScriptTable";
import { useDocumentTitle } from "@scm-manager/ui-core";
import { useTranslation } from "react-i18next";

type Props = {
  links: ScriptLinks;
};

const Stored: FC<Props> = ({ links }) => {
  const { data, error, isLoading } = useScripts(links.list);
  const [t] = useTranslation("plugins");
  useDocumentTitle(t("scm-script-plugin.navigation.stored"), t("scm-script-plugin.rootPage.title"));

  if (error) {
    return <ErrorNotification error={error} />;
  } else if (isLoading) {
    return <Loading />;
  } else if (!data || data._embedded.scripts.length === 0) {
    return <Notification type="info">No stored scripts found</Notification>;
  } else {
    return <ScriptTable scripts={data._embedded.scripts} />;
  }
};

export default Stored;

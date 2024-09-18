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
import { useParams } from "react-router-dom";
import { useScript } from "../api";
import { ScriptLinks } from "../types";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import ScriptMain from "./ScriptMain";

type Props = {
  links: ScriptLinks;
};
const ScriptRoot: FC<Props> = ({ links }) => {
  const params = useParams<{ id: string }>();
  const { data: script, isLoading, error } = useScript(params.id);

  if (error) {
    return <ErrorNotification error={error} />;
  } else if (isLoading) {
    return <Loading />;
  } else if (script) {
    return <ScriptMain links={links} script={script} />;
  }
  return null;
};

export default ScriptRoot;

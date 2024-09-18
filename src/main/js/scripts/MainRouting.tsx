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
import { Route, useRouteMatch } from "react-router-dom";
import Stored from "./list/Stored";
import { ScriptLinks } from "../types";
import Editor from "./run/Editor";
import SampleRoot from "./samples/SampleRoot";

type Props = {
  links: ScriptLinks;
};

const MainRouting: FC<Props> = ({ links }) => {
  const match = useRouteMatch();
  return (
    <>
      <Route path={match.url + "/run"}>
        <Editor links={links} />
      </Route>
      <Route path={match.url} exact={true}>
        <Stored links={links} />
      </Route>
      <Route path={match.url + "/samples"}>
        <SampleRoot />
      </Route>
    </>
  );
};

export default MainRouting;

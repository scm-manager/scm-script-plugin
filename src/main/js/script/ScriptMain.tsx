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
import { Script, ScriptLinks } from "../types";
import EditForm from "./EditForm";
import { ListenersRoot } from "./ListenersPage";
import HistoryPage from "./HistoryPage";
import ScriptTabs from "./ScriptTabs";

type Props = {
  script: Script;
  links: ScriptLinks;
};

const ScriptMain: FC<Props> = ({ script, links }) => {
  const match = useRouteMatch();

  return (
    <>
      <div className="content">
        <h3>{script.title}</h3>
      </div>
      <ScriptTabs path={match.url} />
      <Route path={match.url} exact={true}>
        <EditForm script={script} links={links} />
      </Route>
      <Route path={match.url + "/listeners"} exact={true}>
        <ListenersRoot script={script} links={links} />
      </Route>
      <Route path={match.url + "/history"} exact={true}>
        <HistoryPage script={script} />
      </Route>
    </>
  );
};

export default ScriptMain;

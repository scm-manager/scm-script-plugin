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

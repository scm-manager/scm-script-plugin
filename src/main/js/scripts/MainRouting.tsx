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
import React from "react";
import { Route, withRouter, RouteComponentProps } from "react-router-dom";
import Stored from "./list/Stored";
import { ScriptLinks } from "../types";
import Editor from "./run/Editor";
import SampleRoot from "./samples/SampleRoot";

type Props = RouteComponentProps & {
  links: ScriptLinks;
};

class MainRouting extends React.Component<Props> {
  render() {
    const { links, match } = this.props;
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
  }
}

export default withRouter(MainRouting);

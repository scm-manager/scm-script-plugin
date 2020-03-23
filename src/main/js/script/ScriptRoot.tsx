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
import { withRouter, RouteComponentProps } from "react-router-dom";
import { findById } from "../api";
import { Script, ScriptLinks } from "../types";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import ScriptMain from "./ScriptMain";

type Props = RouteComponentProps & {
  links: ScriptLinks;
};

type State = {
  loading: boolean;
  error?: Error;
  script?: Script;
};

class ScriptRoot extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount(): void {
    const { match } = this.props;
    findById(match.params.id)
      .then(script =>
        this.setState({
          loading: false,
          script
        })
      )
      .catch(error =>
        this.setState({
          loading: false,
          error
        })
      );
  }

  render() {
    const { links } = this.props;
    const { error, loading, script } = this.state;
    if (error) {
      return <ErrorNotification error={error} />;
    } else if (loading) {
      return <Loading />;
    } else if (script) {
      return <ScriptMain links={links} script={script} />;
    }
  }
}

export default withRouter(ScriptRoot);

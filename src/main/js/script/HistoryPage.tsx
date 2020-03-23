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
import styled from "styled-components";
import { DateFromNow, ErrorNotification, Loading } from "@scm-manager/ui-components";
import { findHistory } from "../api";
import { ExecutionHistoryEntry, Script } from "../types";
import Output from "../components/Output";

const Frame = styled.div`
  padding-bottom: 1rem;
`;

const Heading = styled.h2`
  font-weight: 700;
  font-size: 1.25rem !important;
`;

type Props = {
  script: Script;
};

type State = {
  loading: boolean;
  error?: Error;
  history?: ExecutionHistoryEntry[];
};

class HistoryPage extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount() {
    const { script } = this.props;

    findHistory(script._links.history.href)
      .then(history =>
        this.setState({
          history,
          loading: false
        })
      )
      .catch(error =>
        this.setState({
          error,
          loading: false
        })
      );
  }

  render() {
    const { history, loading, error } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    } else if (loading || history === undefined) {
      return <Loading />;
    }

    return (
      <>
        {history.map(entry => {
          return (
            <Frame>
              <Heading>
                <DateFromNow date={entry.result.started} />
              </Heading>
              <p>{entry.listener.eventType}</p>
              <Output result={entry.result} />
            </Frame>
          );
        })}
      </>
    );
  }
}

export default HistoryPage;

//@flow
import React from "react";
import styled from "styled-components";
import {
  DateFromNow,
  ErrorNotification,
  Loading
} from "@scm-manager/ui-components";
import { findHistory } from "../api";
import type { ExecutionHistoryEntry, Script } from "../types";
import Output from "../components/Output";

const Frame = styled.div`
  padding-bottom: 1rem;
`;

const Heading = styled.h2`
  font-weight: 700;
  font-size: 1.25rem !important;
`;

type Props = {
  script: Script
};

type State = {
  loading: boolean,
  error?: Error,
  history?: ExecutionHistoryEntry[]
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

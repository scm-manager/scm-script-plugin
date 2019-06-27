//@flow
import React from "react";
import injectSheets from "react-jss";
import {
  DateFromNow,
  ErrorNotification,
  Loading
} from "@scm-manager/ui-components";
import { findHistory } from "../api";
import type { ExecutionHistoryEntry, Script } from "../types";
import Output from "../components/Output";

const styles = {
  frame: {
    paddingBottom: "1rem"
  },
  heading: {
    fontWeight: 700,
    fontSize: "1.25rem !important"
  }
};

type Props = {
  script: Script,

  // context props
  classes: any
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
    const { classes } = this.props;
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
            <div className={classes.frame}>
              <h2 className={classes.heading}>
                <DateFromNow date={entry.result.started} />
              </h2>
              <p>{entry.listener.eventType}</p>
              <Output result={entry.result} />
            </div>
          );
        })}
      </>
    );
  }
}

export default injectSheets(styles)(HistoryPage);

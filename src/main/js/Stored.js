//@flow
import React from "react";
import type { Script } from "./types";
import { findAll } from "./api";
import ErrorNotification from "@scm-manager/ui-components/src/ErrorNotification";
import Loading from "@scm-manager/ui-components/src/Loading";
import ScriptTable from "./ScriptTable";
import { Notification } from "@scm-manager/ui-components";

type Props = {};

type State = {
  loading: boolean,
  error?: Error,
  scripts?: Script[]
};

class Stored extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount(): void {
    findAll()
      .then(scripts => {
        this.setState({
          loading: false,
          scripts
        });
      })
      .catch(error => {
        this.setState({
          loading: false,
          error
        });
      });
  }

  render() {
    const { loading, error, scripts } = this.state;
    if (error) {
      return <ErrorNotification error={error} />;
    } else if (loading) {
      return <Loading />;
    } else if (!scripts || scripts.length === 0) {
      return <Notification type="info">No stored scripts found</Notification>;
    } else {
      return <ScriptTable scripts={scripts} />;
    }
  }
}

export default Stored;

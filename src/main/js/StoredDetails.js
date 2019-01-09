//@flow
import React from "react";
import { findById } from "./api";
import type { Script } from "./types";
import ErrorNotification from "./Stored";
import EditForm from "./EditForm";
import Loading from "@scm-manager/ui-components/src/Loading";

type Props = {
  match: any
};

type State = {
  loading: boolean,
  error?: Error,
  script?: Script
};

class StoredDetails extends React.Component<Props, State> {
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
    const { error, loading, script } = this.state;
    if (error) {
      return <ErrorNotification error={error} />;
    } else if (loading) {
      return <Loading />;
    } else {
      return <EditForm script={script} />;
    }
  }
}

export default StoredDetails;

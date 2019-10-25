import React from "react";
import { Loading, Notification, ErrorNotification } from "@scm-manager/ui-components";
import { findAll } from "../../api";
import { ScriptCollection, ScriptLinks } from "../../types";
import ScriptTable from "./ScriptTable";

type Props = {
  links: ScriptLinks;
};

type State = {
  loading: boolean;
  error?: Error;
  collection?: ScriptCollection;
};

class Stored extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount(): void {
    const { links } = this.props;
    findAll(links.list)
      .then(collection => {
        this.setState({
          loading: false,
          collection
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
    const { loading, error, collection } = this.state;
    if (error) {
      return <ErrorNotification error={error} />;
    } else if (loading) {
      return <Loading />;
    } else if (!collection || collection._embedded.scripts.length === 0) {
      return <Notification type="info">No stored scripts found</Notification>;
    } else {
      return <ScriptTable scripts={collection._embedded.scripts} />;
    }
  }
}

export default Stored;

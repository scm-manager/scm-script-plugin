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

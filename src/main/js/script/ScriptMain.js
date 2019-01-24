//@flow
import React from "react";
import { Route, withRouter } from "react-router-dom";
import type { Script, ScriptLinks } from "../types";
import EditForm from "./EditForm";
import ScriptNavigation from "./ScriptNavigation";
import { remove } from "../api";
import ListenersPage from "./ListenersPage";

type Props = {
  script: Script,
  links: ScriptLinks,

  // context props
  match: any,
  history: any
};

type State = {
  loading: boolean,
  error?: Error
};

class ScriptMain extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  onDelete = () => {
    this.setState({
      loading: true
    });

    const { script, history } = this.props;

    remove(script._links.delete.href)
      // we do not modify state on success, because we redirect to another page
      .then(() => history.push("/scripts"))
      .catch(error =>
        this.setState({
          loading: false,
          error
        })
      );
  };

  render() {
    const { script, links, match } = this.props;

    return (
      <div className="columns">
        <div className="column is-three-quarters">
          <Route
            path={match.url}
            exact={true}
            render={() => <EditForm script={script} links={links} />}
          />
          <Route
            path={match.url + "/listeners"}
            exact={true}
            render={() => <ListenersPage script={script} links={links} />}
          />
        </div>
        <div className="column">
          <ScriptNavigation script={script} onDelete={this.onDelete} />
        </div>
      </div>
    );
  }
}

export default withRouter(ScriptMain);

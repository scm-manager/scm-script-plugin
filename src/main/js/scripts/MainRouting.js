//@flow
import React from "react";
import { Route, withRouter } from "react-router-dom";
import Stored from "./list/Stored";
import type { ScriptLinks } from "../types";
import Editor from "./run/Editor";
import SampleRoot from "./samples/SampleRoot";

type Props = {
  links: ScriptLinks,

  // context props
  match: any
};

class MainRouting extends React.Component<Props> {
  render() {
    const { links, match } = this.props;
    return (
      <>
        <Route
          path={match.url + "/run"}
          component={() => <Editor links={links} />}
        />
        <Route
          path={match.url}
          component={() => <Stored links={links} />}
          exact={true}
        />
        <Route path={match.url + "/samples"} component={SampleRoot} />
      </>
    );
  }
}

export default withRouter(MainRouting);

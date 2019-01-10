//@flow
import React from "react";
import { Route } from "react-router-dom";
import Stored from "./Stored";
import type { ScriptLinks } from "./types";
import Editor from "./Editor";

type Props = {
  links: ScriptLinks
};

class MainRouting extends React.Component<Props> {
  render() {
    const { links } = this.props;
    return (
      <>
        <Route path="/scripts/run" component={() => <Editor links={links} />} />
        <Route
          path="/scripts"
          component={() => <Stored links={links} />}
          exact={true}
        />
      </>
    );
  }
}

export default MainRouting;

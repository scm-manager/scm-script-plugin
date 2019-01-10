//@flow
import React from "react";
import { Route } from "react-router-dom";
import Stored from "./list/Stored";
import type { ScriptLinks } from "../types";
import Editor from "./run/Editor";
import SampleRoot from "./samples/SampleRoot";

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
        <Route path="/scripts/samples" component={SampleRoot} />
      </>
    );
  }
}

export default MainRouting;

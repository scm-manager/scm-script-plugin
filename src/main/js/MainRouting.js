//@flow
import React from "react";
import { Route } from "react-router-dom";
import Run from "./Run";

type Props = {};

class ScriptRouting extends React.Component<Props> {
  render() {
    return (
      <>
        <Route path="/scripts/run" component={Run} />
      </>
    );
  }
}

export default ScriptRouting;

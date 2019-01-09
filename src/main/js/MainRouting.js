//@flow
import React from "react";
import { Route } from "react-router-dom";
import Run from "./Run";
import Stored from "./Stored";
import StoredDetails from "./StoredDetails";

type Props = {};

class MainRouting extends React.Component<Props> {
  render() {
    return (
      <>
        <Route path="/scripts/run" component={Run} />
        <Route path="/scripts/stored" component={Stored} exact={true} />
        <Route path="/scripts/stored/:id" component={StoredDetails} />
      </>
    );
  }
}

export default MainRouting;

//@flow
import React from "react";
import type { Script } from "../types";
import EditForm from "./EditForm";
import ScriptNavigation from "./ScriptNavigation";

type Props = {
  script: Script
};

class ScriptMain extends React.Component<Props> {
  render() {
    const { script } = this.props;
    return (
      <div className="columns">
        <div className="column is-three-quarters">
          <EditForm script={script} />
        </div>
        <div className="column">
          <ScriptNavigation />
        </div>
      </div>
    );
  }
}

export default ScriptMain;

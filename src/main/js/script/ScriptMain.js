//@flow
import React from "react";
import type { Script, ScriptLinks } from "../types";
import EditForm from "./EditForm";
import ScriptNavigation from "./ScriptNavigation";

type Props = {
  script: Script,
  links: ScriptLinks
};

class ScriptMain extends React.Component<Props> {
  render() {
    const { script, links } = this.props;
    return (
      <div className="columns">
        <div className="column is-three-quarters">
          <EditForm script={script} links={links} />
        </div>
        <div className="column">
          <ScriptNavigation />
        </div>
      </div>
    );
  }
}

export default ScriptMain;

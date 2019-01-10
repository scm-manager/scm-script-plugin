//@flow
import React from "react";
import { translate } from "react-i18next";
import MainNavigation from "./MainNavigation";
import MainRouting from "./MainRouting";
import type { ScriptLinks } from "./types";

type Props = {
  links: ScriptLinks
};

class Main extends React.Component<Props, State> {
  render() {
    const { links } = this.props;

    return (
      <div className="columns">
        <div className="column is-three-quarters">
          <MainRouting links={links} />
        </div>
        <div className="column">
          <MainNavigation links={links} />
        </div>
      </div>
    );
  }
}

export default Main;

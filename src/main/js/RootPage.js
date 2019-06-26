//@flow
import React from "react";
import { Route } from "react-router-dom";
import { translate } from "react-i18next";
import {
  Loading,
  ErrorNotification,
  Title,
  Subtitle
} from "@scm-manager/ui-components";
import { findAllScriptLinks } from "./api";
import type { ScriptLinks } from "./types";
import ScriptRoot from "./script/ScriptRoot";
import MainRouting from "./scripts/MainRouting";

type Props = {
  link: string,

  // context props
  location: any,
  t: string => string
};

type State = {
  loading: boolean,
  error?: Error,
  links?: ScriptLinks
};

class RootPage extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount(): void {
    const { link } = this.props;
    findAllScriptLinks(link)
      .then(links => {
        this.setState({
          loading: false,
          links
        });
      })
      .catch(error => {
        this.setState({
          loading: false,
          error
        });
      });
  }

  createBody = () => {
    const { loading, error, links } = this.state;
    if (error) {
      return <ErrorNotification error={error} />;
    } else if (loading) {
      return <Loading />;
    } else if (links) {
      return (
        <>
          <Route
            path="/admin/scripts"
            component={() => <MainRouting links={links} />}
          />
          <Route
            path="/admin/script/:id"
            component={() => <ScriptRoot links={links} />}
          />
        </>
      );
    } else {
      return null;
    }
  };

  render() {
    const { t } = this.props;
    const body = this.createBody();

    return (
      <>
        <Title title={t("scm-script-plugin.rootPage.title")} />
        <Subtitle subtitle={t("scm-script-plugin.rootPage.subtitle")} />
        {body}
      </>
    );
  }
}

export default translate("plugins")(RootPage);

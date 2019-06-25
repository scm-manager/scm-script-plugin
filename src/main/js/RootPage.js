//@flow
import React from "react";
import { Link, Route } from "react-router-dom";
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

export function isUrlSuffixMatching(
  baseURL: string,
  url: string,
  suffix: string
) {
  let strippedUrl = url.substring(baseURL.length);
  if (strippedUrl.startsWith("/")) {
    strippedUrl = strippedUrl.substring(1);
  }
  const slash = strippedUrl.indexOf("/");
  if (slash >= 0) {
    strippedUrl = strippedUrl.substring(0, slash);
  }
  return strippedUrl === suffix;
}

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

  navigationClass(suffix: string) {
    const { baseURL, location } = this.props;
    if (location && isUrlSuffixMatching(baseURL, location.pathname, suffix)) {
      return "is-active";
    }
    return "";
  }

  render() {
    const { t } = this.props;
    const body = this.createBody();

    let tabs = (
      <div className="tabs">
        <ul>
          <li className={this.navigationClass("run")}>
            <Link to="/admin/scripts/run">
              {t("scm-script-plugin.navigation.run")}
            </Link>
          </li>
          <li className={this.navigationClass("stored")}>
            <Link to="/admin/scripts">
              {t("scm-script-plugin.navigation.stored")}
            </Link>
          </li>
          <li className={this.navigationClass("samples")}>
            <Link to="/admin/scripts/samples">
              {t("scm-script-plugin.navigation.samples")}
            </Link>
          </li>
        </ul>
      </div>
    );

    return (
      <>
        <Title title={t("scm-script-plugin.root-page.title")} />
        <Subtitle subtitle={t("scm-script-plugin.root-page.subtitle")} />
        {tabs}
        {body}
      </>
    );
  }
}

export default translate("plugins")(RootPage);

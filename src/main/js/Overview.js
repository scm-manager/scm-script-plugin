//@flow
import React from 'react';
import {Page} from '@scm-manager/ui-components';
import { translate } from "react-i18next";
import Editor from './Editor';

type Props = {
  // context props
  t: string => string
};

class Overview extends React.Component<Props> {

  render() {
    const { t } = this.props;
    return (
      <Page title={t("scm-script-plugin.overview.title")} subtitle={t("scm-script-plugin.overview.subtitle")}>
        <Editor />
      </Page>
    );
  }

}

export default translate("plugins")(Overview);

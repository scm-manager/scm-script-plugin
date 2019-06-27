//@flow
import React from "react";
import samples from "./samples";
import SamplePanel from "./SamplePanel";
import { translate } from "react-i18next";

type Props = {
  // context props
  t: string => string
};

class SampleRoot extends React.Component<Props> {
  render() {
    const { t } = this.props;
    return (
      <>
        <div className="content">
          <h3>{t("scm-script-plugin.navigation.samples")}</h3>
          <hr />
        </div>
        {samples.map(sample => (
          <SamplePanel sample={sample} />
        ))}
      </>
    );
  }
}

export default translate("plugins")(SampleRoot);

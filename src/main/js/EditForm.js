//@flow
import React from "react";
import type { Script } from "./types";
import {
  InputField,
  LabelWithHelpIcon,
  Textarea
} from "@scm-manager/ui-components";
import ContentEditor from "./ContentEditor";
import { translate } from "react-i18next";

type Props = {
  script: Script,

  // context props
  t: string => string
};

type State = Script & {};

class EditForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      ...props.script
    };
  }

  onValueChange = (value, name) => {
    this.setState({
      [name]: value
    });
  };

  render() {
    const { t } = this.props;
    const { title, description, content } = this.state;
    return (
      <form>
        <InputField
          name="title"
          label={t("scm-script-plugin.title")}
          helpText={t("scm-script-plugin.titleHelp")}
          value={title}
          onChange={this.onValueChange}
        />
        <Textarea
          name="description"
          label={t("scm-script-plugin.description")}
          helpText={t("scm-script-plugin.descriptionHelp")}
          value={description}
          onChange={this.onValueChange}
        />
        <div className="field">
          <LabelWithHelpIcon
            label={t("scm-script-plugin.content")}
            helpText={t("scm-script-plugin.contentHelp")}
          />
          <ContentEditor
            name="content"
            value={content}
            onChange={this.onValueChange}
          />
        </div>
      </form>
    );
  }
}

export default translate("plugins")(EditForm);

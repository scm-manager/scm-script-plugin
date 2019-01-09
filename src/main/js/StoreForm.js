//@flow
import React from "react";
import { translate } from "react-i18next";
import { InputField, SubmitButton, Textarea } from "@scm-manager/ui-components";
import Button from "@scm-manager/ui-components/src/buttons/Button";
import type { Script } from "./types";

type Props = {
  onSubmit: Script => void,
  onAbort: () => void,
  // context props
  t: string => string
};

type State = Script;

class StoreForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      title: "",
      description: "",
      type: "Groovy"
    };
  }

  onChange = (value, name) => {
    this.setState({
      [name]: value
    });
  };

  onSubmit = (e: Event) => {
    e.preventDefault();
    this.props.onSubmit(this.state);
  };

  render() {
    const { onAbort, t } = this.props;
    const { title, description } = this.state;

    return (
      <form onSubmit={this.onSubmit}>
        <InputField
          name="title"
          label={t("scm-script-plugin.store-form.title")}
          helpText={t("scm-script-plugin.store-form.titleHelp")}
          onChange={this.onChange}
          value={title}
        />
        <Textarea
          name="description"
          label={t("scm-script-plugin.store-form.description")}
          helpText={t("scm-script-plugin.store-form.descriptionHelp")}
          onChange={this.onChange}
          value={description}
        />
        <SubmitButton label={t("scm-script-plugin.store-form.submit")} />
        <Button
          label={t("scm-script-plugin.store-form.abort")}
          action={onAbort}
        />
      </form>
    );
  }
}

export default translate("plugins")(StoreForm);

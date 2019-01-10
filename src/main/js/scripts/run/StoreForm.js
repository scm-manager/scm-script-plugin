//@flow
import React from "react";
import { translate } from "react-i18next";
import { InputField, SubmitButton, Textarea } from "@scm-manager/ui-components";
import Button from "@scm-manager/ui-components/src/buttons/Button";
import type { Script } from "../../types";

type Props = {
  onSubmit: Script => void,
  onAbort: () => void,
  // context props
  t: string => string
};

type State = {
  script: Script,
  loading: boolean
};

class StoreForm extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      script: {
        title: "",
        description: "",
        type: "Groovy"
      },
      loading: false
    };
  }

  onChange = (value, name) => {
    this.setState(state => {
      return {
        script: {
          ...state.script,
          [name]: value
        }
      };
    });
  };

  onSubmit = (e: Event) => {
    e.preventDefault();
    this.setState({
      loading: true
    });
    this.props.onSubmit(this.state.script);
  };

  render() {
    const { onAbort, t } = this.props;
    const { script, loading } = this.state;

    return (
      <form onSubmit={this.onSubmit}>
        <InputField
          name="title"
          label={t("scm-script-plugin.title")}
          helpText={t("scm-script-plugin.titleHelp")}
          onChange={this.onChange}
          value={script.title}
        />
        <Textarea
          name="description"
          label={t("scm-script-plugin.description")}
          helpText={t("scm-script-plugin.descriptionHelp")}
          onChange={this.onChange}
          value={script.description}
        />
        <SubmitButton
          label={t("scm-script-plugin.store-form.submit")}
          loading={loading}
        />
        <Button
          label={t("scm-script-plugin.store-form.abort")}
          action={onAbort}
          disabled={loading}
        />
      </form>
    );
  }
}

export default translate("plugins")(StoreForm);

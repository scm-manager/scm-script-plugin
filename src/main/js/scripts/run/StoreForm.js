//@flow
import React from "react";
import { translate } from "react-i18next";
import {
  InputField,
  ButtonGroup,
  Button,
  SubmitButton,
  Textarea
} from "@scm-manager/ui-components";
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
        type: "Groovy",
        _links: {}
      },
      loading: false,
      titleValid: false
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

  isTitleValid = () => {
    return !!this.state.script.title;
  };

  isScriptValid = () => {
    return this.isTitleValid();
  };

  render() {
    const { t, onAbort } = this.props;
    const { script, loading } = this.state;

    return (
      <form onSubmit={this.onSubmit}>
        <InputField
          autofocus={true}
          name="title"
          label={t("scm-script-plugin.title")}
          helpText={t("scm-script-plugin.titleHelp")}
          onChange={this.onChange}
          value={script.title}
          validationError={!this.isTitleValid()}
          errorMessage={t("scm-script-plugin.titleValidationError")}
        />
        <Textarea
          name="description"
          label={t("scm-script-plugin.description")}
          helpText={t("scm-script-plugin.descriptionHelp")}
          onChange={this.onChange}
          value={script.description}
        />
        <ButtonGroup>
          <SubmitButton
            label={t("scm-script-plugin.store-form.submit")}
            loading={loading}
            disabled={!this.isScriptValid()}
          />
          <Button
            label={t("scm-script-plugin.store-form.abort")}
            action={onAbort}
            disabled={loading}
          />
        </ButtonGroup>
      </form>
    );
  }
}

export default translate("plugins")(StoreForm);

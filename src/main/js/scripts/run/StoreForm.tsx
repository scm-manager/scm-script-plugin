import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import { InputField, ButtonGroup, Button, SubmitButton, Textarea, Level } from "@scm-manager/ui-components";
import { Script } from "../../types";

type Props = WithTranslation & {
  onSubmit: (p: Script) => void;
  onAbort: () => void;
};

type State = {
  script: Script;
  loading: boolean;
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
        <Level
          right={
            <ButtonGroup>
              <SubmitButton
                label={t("scm-script-plugin.storeForm.submit")}
                loading={loading}
                disabled={!this.isScriptValid()}
              />
              <Button label={t("scm-script-plugin.storeForm.abort")} action={onAbort} disabled={loading} />
            </ButtonGroup>
          }
        />
      </form>
    );
  }
}

export default withTranslation("plugins")(StoreForm);

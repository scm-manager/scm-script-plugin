/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

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
import { Checkbox } from "@scm-manager/ui-components";
import { findAllEventTypes, findAllListeners, storeListeners } from "../api";
import { Listener, Listeners, Script, ScriptLinks } from "../types";
import ListenersTable from "../components/ListenersTable";

type Props = WithTranslation & {
  script: Script;
  links: ScriptLinks;
};

type State = {
  loading: boolean;
  eventTypes: string[];
  listeners: Listeners;
  error?: Error;

  eventType?: string;
  asynchronous: boolean;
};

class ListenersPage extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true,
      eventTypes: [],
      listeners: {
        listeners: [],
        storeListenerExecutionResults: false,
        _links: {}
      },
      asynchronous: true
    };
  }

  componentDidMount() {
    const { script, links } = this.props;

    Promise.all([findAllEventTypes(links.eventTypes), findAllListeners(script._links.listeners.href)])
      .then(([eventTypes, listeners]) =>
        this.setState({
          eventTypes,
          listeners,
          loading: false,
          error: undefined
        })
      )
      .catch(error =>
        this.setState({
          loading: false,
          error
        })
      );
  }

  updateListeners = () => {
    const { listeners } = this.state;

    storeListeners(listeners._links.update.href, listeners)
      .then(() =>
        this.setState({
          listeners
        })
      )
      .catch(error =>
        this.setState({
          error
        })
      );
  };

  onToggleStoreListenerExecutionResultsChange = (storeListenerExecutionResults: boolean) => {
    this.setState(state => {
      return {
        listeners: {
          ...state.listeners,
          storeListenerExecutionResults
        }
      };
    }, this.updateListeners);
  };

  onChange = (value: string, name: string) => {
    this.setState({
      [name]: value
    });
  };

  onSubmit = (e: Event) => {
    e.preventDefault();

    const { eventType, asynchronous, listeners } = this.state;

    if (eventType) {
      listeners.listeners.push({
        eventType,
        asynchronous
      });

      storeListeners(listeners._links.update.href, listeners)
        .then(() =>
          this.setState({
            eventType: "",
            listeners
          })
        )
        .catch(error =>
          this.setState({
            error
          })
        );
    }
  };

  onDelete = (listener: Listener) => {
    const { listeners } = this.state;
    const index = listeners.listeners.indexOf(listener);
    if (index >= 0) {
      listeners.listeners.splice(index, 1);
      this.updateListeners();
    }
  };

  render() {
    const { t } = this.props;
    const { eventTypes, listeners, eventType, asynchronous } = this.state;

    return (
      <>
        <ListenersTable
          eventTypes={eventTypes}
          listeners={listeners}
          eventType={eventType}
          asynchronous={asynchronous}
          onChange={this.onChange}
          onSubmit={this.onSubmit}
          onDelete={this.onDelete}
        />
        <Checkbox
          name="storeListenerExecutionResults"
          label={t("scm-script-plugin.listeners.storeListenerExecutionResults")}
          checked={listeners.storeListenerExecutionResults}
          onChange={this.onToggleStoreListenerExecutionResultsChange}
        />
      </>
    );
  }
}

export default withTranslation("plugins")(ListenersPage);

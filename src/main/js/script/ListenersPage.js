//@flow
import React from "react";
import type { Listeners, Script, ScriptLinks } from "../types";
import { translate } from "react-i18next";
import { Checkbox, Select, SubmitButton } from "@scm-manager/ui-components";
import { findAllEventTypes, findAllListeners, storeListeners } from "../api";

type Props = {
  script: Script,
  links: ScriptLinks,

  // context props
  t: string => string
};

type State = {
  loading: boolean,
  eventTypes: string[],
  listeners: Listeners,
  error?: Error,

  eventType?: string,
  asynchronous: boolean
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

    Promise.all([
      findAllEventTypes(links.eventTypes),
      findAllListeners(script._links.listeners.href)
    ])
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

  checkedIcon = checked => {
    if (checked) {
      return <i className="fas fa-check" />;
    }
    return null;
  };

  renderListenerRow = (listener, key) => {
    return (
      <tr key={key}>
        <td>{listener.eventType}</td>
        <td>{this.checkedIcon(listener.asynchronous)}</td>
        <td>
          <a className="fas fa-trash" onClick={() => this.onDelete(listener)} />
        </td>
      </tr>
    );
  };

  onChange = (value: string, name: string) => {
    this.setState({
      [name]: value
    });
  };

  onDelete = listener => {
    const { listeners } = this.state;
    const index = listeners.listeners.indexOf(listener);
    if (index >= 0) {
      listeners.listeners.splice(index, 1);
      this.updateListeners();
    }
  };

  onToggleStoreListenerExecutionResultsChange = (
    storeListenerExecutionResults: boolean
  ) => {
    this.setState(state => {
      return {
        listeners: {
          ...state.listeners,
          storeListenerExecutionResults
        }
      };
    }, this.updateListeners);
  };

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

  onSubmit = (e: Event) => {
    e.preventDefault();

    const { eventType, asynchronous, listeners } = this.state;

    if (eventType) {
      listeners.listeners.push({ eventType, asynchronous });

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

  render() {
    const { script, t } = this.props;
    const { eventTypes, listeners, eventType, asynchronous } = this.state;

    const options = eventTypes.map(eventType => {
      return {
        value: eventType,
        label: eventType
      };
    });

    options.unshift({
      value: "",
      label: ""
    });

    return (
      <div>
        <div className="content">
          <h3>{script.title}</h3>
        </div>
        <table className="card-table table is-hoverable is-fullwidth">
          <thead>
            <tr>
              <th>{t("scm-script-plugin.listeners.eventType")}</th>
              <th>{t("scm-script-plugin.listeners.asynchronous")}</th>
              <th>{t("scm-script-plugin.listeners.remove")}</th>
            </tr>
          </thead>
          <tbody>{listeners.listeners.map(this.renderListenerRow)}</tbody>
        </table>
        <hr />
        <Checkbox
          name={"storeListenerExecutionResults"}
          label={t("scm-script-plugin.listeners.storeListenerExecutionResults")}
          checked={listeners.storeListenerExecutionResults}
          onChange={this.onToggleStoreListenerExecutionResultsChange}
        />
        <hr />
        <form onSubmit={this.onSubmit}>
          <Select
            name={"eventType"}
            label={t("scm-script-plugin.listeners.eventType")}
            options={options}
            value={eventType}
            onChange={this.onChange}
          />
          <Checkbox
            name={"asynchronous"}
            label={t("scm-script-plugin.listeners.asynchronous")}
            checked={asynchronous}
            onChange={this.onChange}
          />
          <SubmitButton label={t("scm-script-plugin.listeners.submit")} />
        </form>
      </div>
    );
  }
}

export default translate("plugins")(ListenersPage);

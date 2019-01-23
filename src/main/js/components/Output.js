//@flow
import React from "react";
import injectSheets from "react-jss";
import classNames from "classnames";
import type { ScriptExecutionResult } from "../types";
import moment from "moment";

type Props = {
  result?: ScriptExecutionResult,

  // context props
  classes: any
};

const styles = {
  figure: {
    marginTop: "1em",
    position: "relative"
  },
  caption: {
    outline: 0,
    paddingBottom: 0,
    paddingTop: 0,
    position: "absolute",
    right: ".25rem",
    top: ".25rem",
    fontSize: ".75rem"
  }
};

class Output extends React.Component<Props> {
  createCaption(result: ScriptExecutionResult) {
    const duration = moment(result.ended).diff(moment(result.started));
    const prefix = result.success ? "success" : "failed";
    return `${prefix} in ${duration}ms`;
  }

  createCaptionClass(result: ScriptExecutionResult) {
    return result.success ? "has-text-success" : "has-text-danger";
  }

  render() {
    const { result, classes } = this.props;
    if (!result) {
      return null;
    }
    return (
      <figure className={classes.figure}>
        <pre>
          <code>{result.output}</code>
        </pre>
        <figcaption
          className={classNames(
            classes.caption,
            this.createCaptionClass(result)
          )}
        >
          {this.createCaption(result)}
        </figcaption>
      </figure>
    );
  }
}

export default injectSheets(styles)(Output);

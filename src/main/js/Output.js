//@flow
import React from "react";
import injectSheets from "react-jss";

type Props = {
  output: string,

  // context props
  classes: any
};

const styles = {
  spacing: {
    marginTop: "1em"
  }
};

class Output extends React.Component<Props> {
  render() {
    const { output, classes } = this.props;
    return (
      <pre className={classes.spacing}>
        <code>{output}</code>
      </pre>
    );
  }
}

export default injectSheets(styles)(Output);

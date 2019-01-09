//@flow
import React from "react";
import type { Script } from "./types";

type Props = {
  script: Script
};

class EditForm extends React.Component<Props> {
  render() {
    const { script } = this.props;
    return (
      <div className="content">
        <h1>{script.title}</h1>
        <p>{script.description}</p>
      </div>
    );
  }
}

export default EditForm;

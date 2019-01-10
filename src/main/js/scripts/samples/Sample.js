//@flow
import React from "react";
import type { Script } from "../../types";
import ContentViewer from "../../components/ContentViewer";

type Props = {
  sample: Script
};

class Sample extends React.Component<Props> {
  render() {
    const { sample } = this.props;
    return (
      <div className="content">
        <h4>{sample.title}</h4>
        <p>{sample.description}</p>
        <ContentViewer value={sample.content} />
      </div>
    );
  }
}

export default Sample;

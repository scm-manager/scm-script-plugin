//@flow
import React from "react";
import type { Sample } from "../../types";
import ContentViewer from "../../components/ContentViewer";

type Props = {
  sample: Sample
};

class SamplePanel extends React.Component<Props> {
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

export default SamplePanel;

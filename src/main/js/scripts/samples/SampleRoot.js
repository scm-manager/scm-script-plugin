//@flow
import React from "react";
import samples from "./samples";
import Sample from "./Sample";

type Props = {};

class SampleRoot extends React.Component<Props> {
  render() {
    return (
      <div>
        {samples.map(sample => (
          <Sample sample={sample} />
        ))}
      </div>
    );
  }
}

export default SampleRoot;

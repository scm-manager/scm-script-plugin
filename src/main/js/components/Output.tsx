import React from "react";
import styled from "styled-components";
import { ScriptExecutionResult } from "../types";

const Figure = styled.figure`
  margin-top: 1em;
  position: relative;
`;

const Figcaption = styled.figcaption`
  outline: 0;
  padding-bottom: 0;
  padding-top: 0;
  position: absolute;
  right: 0.25rem;
  top: 0.25rem;
  font-size: 0.75rem;
`;

type Props = {
  result?: ScriptExecutionResult;
};

class Output extends React.Component<Props> {
  createCaption(result: ScriptExecutionResult) {
    const duration = Math.abs(new Date(result.ended) - new Date(result.started));
    const prefix = result.success ? "success" : "failed";
    return `${prefix} in ${duration}ms`;
  }

  createCaptionClass(result: ScriptExecutionResult) {
    return result.success ? "has-text-success" : "has-text-danger";
  }

  render() {
    const { result } = this.props;
    if (!result) {
      return null;
    }
    return (
      <Figure>
        <pre>
          <code>{result.output}</code>
        </pre>
        <Figcaption className={this.createCaptionClass(result)}>{this.createCaption(result)}</Figcaption>
      </Figure>
    );
  }
}

export default Output;

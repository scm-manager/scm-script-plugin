// @flow

import {predicate} from './index';

describe("test predicate", () => {

  const exec = (links) => {
    return predicate({links});
  };

  it("should return true", () => {
    const result = exec({
      scripts: [{
        name: "run",
        href: "http://..."
      }]
    });
    expect(result).toBe(true);
  });

  it("should return false, without script links", () => {
    const result = exec({});
    expect(result).toBe(false);
  });

  it("should return false, with empty script links", () => {
    const result = exec({
      scripts: []
    });
    expect(result).toBe(false);
  });

  it("should return false, without links", () => {
    const result = exec({
      scripts: []
    });
    expect(result).toBe(false);
  });

});

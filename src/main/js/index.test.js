// @flow

import { predicate } from "./index";

describe("test predicate", () => {
  const exec = links => {
    return predicate({ links });
  };

  it("should return true", () => {
    const result = exec({
      scripts: {
        href: "http://..."
      }
    });
    expect(result).toBe(true);
  });

  it("should return false, without script links", () => {
    const result = exec({});
    expect(result).toBe(false);
  });
});

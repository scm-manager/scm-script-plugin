// @flow

import { createScriptLinks } from "./api";

describe("test transformToScriptLinks", () => {
  it("should transform the link array", () => {
    const scriptLinks = createScriptLinks("http://list", {
      execute: {
        href: "http://execute"
      },
      create: {
        href: "http://create"
      }
    });

    expect(scriptLinks).toEqual({
      list: "http://list",
      execute: "http://execute",
      create: "http://create"
    });
  });
});

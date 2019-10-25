import { createScriptLinks } from "./api";

describe("test transformToScriptLinks", () => {
  it("should transform the link array", () => {
    const scriptLinks = createScriptLinks("http://list", {
      eventTypes: {
        href: "http://eventTypes"
      },
      execute: {
        href: "http://execute"
      },
      create: {
        href: "http://create"
      }
    });

    expect(scriptLinks).toEqual({
      list: "http://list",
      eventTypes: "http://eventTypes",
      execute: "http://execute",
      create: "http://create"
    });
  });
});

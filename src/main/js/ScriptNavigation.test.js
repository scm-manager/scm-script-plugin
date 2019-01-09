// @flow

import {findNavigationLink, hasLinkItem} from './ScriptNavigation';

describe("test hasLinkItem", () => {

  it("should return true", () => {
    const links = [{
      name: "self",
      href: "http://..."
    }];

    expect(hasLinkItem(links, "self")).toBe(true);
  });

  it("should return true, even if theire are multiple links", () => {
    const links = [{
      name: "list",
      href: "http://..."
    },{
      name: "self",
      href: "http://..."
    }];

    expect(hasLinkItem(links, "self")).toBe(true);
  });

  it("should return true", () => {
    const links = [{
      name: "self",
      href: "http://..."
    }];

    expect(hasLinkItem(links, "something")).toBe(false);
  });

});

describe("test findNavigationLink", () => {

  it("should use run, if it is available", () => {
    const links = [{
      name: "list",
      href: "http://..."
    },{
      name: "run",
      href: "http://..."
    }];

    expect(findNavigationLink(links)).toBe("/scripts/run");
  });

  it("should use list, if run is not available", () => {
    const links = [{
      name: "list",
      href: "http://..."
    }];

    expect(findNavigationLink(links)).toBe("/scripts/stored");
  });

});

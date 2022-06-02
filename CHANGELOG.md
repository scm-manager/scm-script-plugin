# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 2.3.4 - 2022-06-02
### Fixed
- Enlarge color difference between options in Confirm Alert ([#47](https://github.com/scm-manager/scm-script-plugin/pull/47))

## 2.3.3 - 2022-02-17
### Fixed
- Fix background in high contrast mode ([#38](https://github.com/scm-manager/scm-script-plugin/pull/38))

### Changed
- Use react-query to prevent stale states and fix some minor issues ([#39](https://github.com/scm-manager/scm-script-plugin/pull/39))

## 2.3.2 - 2021-12-21
### Fixed
- Remove obsolete <a> tags ([#35](https://github.com/scm-manager/scm-script-plugin/pull/35))

## 2.3.1 - 2021-11-04
### Fixed
- Remove obsolete <a> tags ([#35](https://github.com/scm-manager/scm-script-plugin/pull/35))

## 2.3.0 - 2021-07-30
### Changed
- Exceptions in scripts run as triggers are rethrown to interrupt triggers ([#29](https://github.com/scm-manager/scm-script-plugin/pull/29))

## 2.2.1 - 2021-04-19
### Fixed
- Fix page reload when switching focus ([#19](https://github.com/scm-manager/scm-script-plugin/pull/19))

## 2.2.0 - 2020-10-27
### Changed
- Use scm-code-editor-plugin for syntax highlighting ([#14](https://github.com/scm-manager/scm-script-plugin/pull/14))

## 2.1.3 - 2020-09-02
### Fixed
- Remove useless dependency since it was added mistakenly

## 2.1.1 - 2020-08-24
### Fixed
- Add missing dependency to scm-el-plugin to enable expression language for scripts

## 2.1.0 - 2020-08-05
### Fixed
- Script execution on java > 11 ([#12](https://github.com/scm-manager/scm-script-plugin/pull/12))
- Loading of plugin classes in a script ([#13](https://github.com/scm-manager/scm-script-plugin/pull/13))

### Added
- Added `ScmStartedEvent` which is fired after all init scripts are executed ([#11](https://github.com/scm-manager/scm-script-plugin/pull/11))
- Documentation in English and German ([#8](https://github.com/scm-manager/scm-script-plugin/pull/8))

## 2.0.0 - 2020-06-04
### Changed
- Rebuild for api changes from core

## 2.0.0-rc3 - 2020-05-15
### Changed
- Changeover to MIT license ([#6](https://github.com/scm-manager/scm-script-plugin/pull/6))
- Adapt to new SCM-Manager event API ([#7](https://github.com/scm-manager/scm-script-plugin/pull/7))

## 2.0.0-rc2 - 2020-03-13
### Added
- Add swagger rest annotations to generate openAPI specs for the scm-openapi-plugin ([#4](https://github.com/scm-manager/scm-script-plugin/pull/4))
- Make navigation item collapsable ([#5](https://github.com/scm-manager/scm-script-plugin/pull/5))

[2.0.0]: https://github.com/scm-manager/scm-script-plugin/releases/tag/2.0.0
[2.0.0-rc3]: https://github.com/scm-manager/scm-script-plugin/releases/tag/2.0.0-rc3
[2.0.0-rc2]: https://github.com/scm-manager/scm-script-plugin/releases/tag/2.0.0-rc2

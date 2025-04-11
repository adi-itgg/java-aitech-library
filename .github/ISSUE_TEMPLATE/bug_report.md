name: 🐞 Bug Report
description: Report a bug to help us improve
title: "[BUG] "
labels: bug
assignees: ''

body:
- type: markdown
  attributes:
  value: |
  Thanks for taking the time to report a bug! Please fill out the form below so we can look into it quickly. 🙏

- type: input
  id: environment
  attributes:
  label: Environment
  description: Include details such as OS, Java version, and library version.
  placeholder: Example: Windows 10, Java 17, Version 1.0.0
  validations:
  required: true

- type: textarea
  id: describe_bug
  attributes:
  label: Bug Description
  description: A clear and concise description of what the bug is.
  placeholder: What happened? What did you expect to happen?
  validations:
  required: true

- type: textarea
  id: steps
  attributes:
  label: Steps to Reproduce
  description: Please provide steps to reproduce the issue.
  placeholder: |
  1. Run ...
  2. Call function ...
  3. Observe ...
  validations:
  required: true

- type: textarea
  id: logs
  attributes:
  label: Logs or Screenshots
  description: If applicable, add logs or a screenshot to help explain the issue.
  placeholder: Paste error logs or screenshots here...
  validations:
  required: false

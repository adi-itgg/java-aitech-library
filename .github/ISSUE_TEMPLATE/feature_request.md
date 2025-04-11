name: ✨ Feature Request
description: Suggest an idea or enhancement
title: "[FEATURE] "
labels: enhancement
assignees: ''

body:
- type: markdown
  attributes:
  value: |
  Have an idea to improve the project? We'd love to hear it! 🚀

- type: input
  id: context
  attributes:
  label: Problem Statement
  description: What problem are you trying to solve?
  placeholder: For example: It's hard to handle X because ...
  validations:
  required: true

- type: textarea
  id: solution
  attributes:
  label: Proposed Solution
  description: Describe the feature or improvement you would like to see.
  placeholder: I would like the library to ...
  validations:
  required: true

- type: textarea
  id: alternatives
  attributes:
  label: Alternatives Considered
  description: Have you tried other solutions or workarounds?
  placeholder: I tried working around this by ...
  validations:
  required: false

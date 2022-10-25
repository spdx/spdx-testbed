# Contributing

Thank you for your interest in `spdx-testbed`. The project is open-source software, and bug reports, suggestions, and
most especially patches and additions are welcome.

## Issues

We try to track all bug reports, suggestions, agreed-on improvements and questions as issues on
the [project page](https://github.com/TNG/spdx-testbed/issues). In case of larger changes, you may also wish to contact
the SPDX working group technical team through its mailing
list, [spdx-tech@lists.spdx.org](mailto:spdx-tech@lists.spdx.org).

If you would like to ask a question or suggest an improvement, please check for open issues first. If no matching issue
exists, feel free to open a new one.

If you would like to work on an issue, please assign the issue to yourself (or write a comment indicating your intention
if it was created by someone else) so we avoid having multiple people working on the same issue.

## Development process

We use the standard GitHub fork & PR workflow. Further information is available
at https://docs.github.com/en/get-started/quickstart/fork-a-repo
and https://docs.github.com/en/get-started/quickstart/github-flow.

The process for making changes is as follows:

1. Find an existing issue describing the change you'd like to address, or create a new one (see above).

2. To make sure work on the same issue is not already in progress,
   check [open pull requests](https://github.com/TNG/spdx-testbed/pulls) before spending a substantial amount of time on
   the issue.

3. Fork the repo or update your fork (see above), then create a branch from `main`.

4. Make the desired changes and commit them to the branch. Commits should be reasonably small logical units, and each
   commit should pass CI. Ideally, each commit message should be prefixed with `[issue-xyz]`, so finding all commits
   associated with a specific issue is easy.

   **Licensing**: Please sign off in each of your commits that you license your contributions under the terms
   of [the Developer Certificate of Origin](https://developercertificate.org/). Git has utilities for signing off on
   commits: `git commit -s` or `--signoff` signs a current commit, and `git rebase --signoff <revision-range>`
   retroactively signs a range of past commits.

5. Verify that the tests pass:
   ```
   ./gradlew test
   ```

6. Create a pull request on GitHub against the `spdx-testbed` repo.

7. A collaborator will review the changes and merge them once all comments are resolved. Merges should be done
   using `rebase` if possible to avoid unnecessary merge commits and keep the git history clean.

## Adding new test cases

The testbed was designed to make adding and integrating new test cases as easy as possible. To achieve this, annotations
on the test case classes are used to "tag" a test. Possible tags should reside
in [the testClassification package](testbed/src/main/java/org/spdx/testbed/util/testClassification) and are:

- the name of the test ([TestName](testbed/src/main/java/org/spdx/testbed/util/testClassification/TestName.java))
- the category of the test (so far,
  only [GenerationTest](testbed/src/main/java/org/spdx/testbed/util/testClassification/GenerationTest.java))
- the formats the test applies to (not yet implemented)
- the SPDX versions the test applies to (not yet implemented)

These annotations are scanned at runtime in order to select the cases matching the given CLI input. The gory details can
be found in [TestCaseSelector](testbed/src/main/java/org/spdx/testbed/util/TestCaseSelector.java)
and [TestCaseFinder](testbed/src/main/java/org/spdx/testbed/util/TestCaseFinder.java).

Each case should:

- extend [TestCase](testbed/src/main/java/org/spdx/testbed/TestCase.java)
- have a no args constructor (used in the reflection logic mentioned above)
- be annotated with [TestName](testbed/src/main/java/org/spdx/testbed/util/testClassification/TestName.java)
- be annotated with further annotations as appropriate

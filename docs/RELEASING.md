# Releasing

Each release corresponds to a GitHub milestone (for example `v1.3.0`).

1. **Prepare.** All issues in the milestone are closed or moved to the next one. The CI builds on `vX.Y-DEV` are green. `docs/` is up to date (run `./update-docs.sh` if any triplifier annotations changed).
2. **Release candidate (optional).** Create a GitHub release with tag `vX.Y.Z-RC1` on `vX.Y-DEV`, marked as pre-release. Write the notes for the community: what to test, notable changes, and install instructions for the CLI jar, the server jar and the Maven dependency.
3. **Close the milestone.** `draft-release.yml` creates a draft release listing the milestone's issues.
4. **Edit the draft.** Set the tag to `vX.Y.Z` on `vX.Y-DEV` and review the notes.
5. **Publish.** Publishing the release triggers:
    - `publish_to_mvn-central.yml`: deploys to Maven Central with `-Drevision=X.Y.Z` (the tag without the leading `v`), signing with the project GPG key (`-DperformRelease=true` activates the `release-and-sign-artifacts` profile);
    - `docker-image.yml`: builds and pushes the Docker image and attaches the jars to the release.
6. **Check.** The artifacts are on Maven Central, the jars are attached to the release, the Docker image is on Docker Hub, and Read the Docs shows the release version.
7. **Start the next development cycle** (see commit `6290130f` for an example):
    - create the branch `vX.(Y+1)-DEV` and set it as the default branch on GitHub;
    - update the branch filters in the workflows (`build_on_maven_java21*.yml`, `codeql-analysis.yml`), the badges in `README.md` and `docs/README.md`, and `edit_uri` in `mkdocs.yml`;
    - bump `<revision>` in the root `pom.xml` to `X.(Y+1).0-SNAPSHOT`;
    - activate the new branch as a version on Read the Docs.

Secrets used by the release workflows: `OSSRH_USERNAME`, `OSSRH_TOKEN`, `OSSRH_GPG_SECRET_KEY`, `OSSRH_GPG_SECRET_KEY_PASSWORD`, `DOCKER_USERNAME`, `DOCKER_PASSWORD`. The repository variable `DOCKER_REPO` is also required.

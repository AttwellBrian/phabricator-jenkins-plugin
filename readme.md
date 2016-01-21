# Phabricator-Jenkins Plugin [![Build Status](https://travis-ci.org/uber/phabricator-jenkins-plugin.svg?branch=master)](https://travis-ci.org/uber/phabricator-jenkins-plugin) [![Coverage Status](https://coveralls.io/repos/uber/phabricator-jenkins-plugin/badge.svg?branch=master&service=github)](https://coveralls.io/github/uber/phabricator-jenkins-plugin?branch=master)

This plugin provides [Phabricator][] integration with [Jenkins][]. It allows Jenkins to
report build status and coverage information over Harbormaster (or via comments
if Harbormaster is not enabled).

[Phabricator]: http://phabricator.org/
[Jenkins]: https://jenkins-ci.org/

Configuration
=============

Before the plugin can be used, a few configuration steps on your
Phabricator and Jenkins instances need to be completed.

Phabricator Configuration
-------------------------

In this section, you'll create a bot user in Phabricator and generate a Conduit API token. If you already have a bot user and a Conduit API token, skip to the "Jenkins Setup Section".

1. Create a bot user in Phabricator.
2. Generate a Conduit API token for your Phabricator installation.
  1. Navigate to `https://phabricator.example.com/settings/panel/apitokens/` with your base Phabricator URL in place of "phabricator.example".
  2. Click the **Generate API Token** button. ![Conduit Token](/docs/conduit-token.png)
  3. Click the **Generate Token** button.
  4. Copy the token.

Jenkins Setup
-------------

1. Navigate to `https://ci.example.com/configure` with your base Jenkins URL in place of "ci.example".
2. Navigate to the **Phabricator** section and click the **Add** button. ![Add Credentials](/docs/add-credentials.png)
3. From the **Kind** dropdown, select **Phabricator Conduit Key**.
4. Enter the base URL for your Phabricator instance in the **Phabricator URL** field. For example `https://phabricator.example.com`.
5. Enter a description in the **Description** field for readbility.![Configure Credentials](/docs/configure-credentials.png)
6. Paste the Conduit API token (created in the Phabrticator Setup section) in the "Conduit Token" field.
7. Save the configuration. 

Usage
=====

Now that Jenkins and Phabricator can connect, configure your Jenkins job and Harbormaster.

Jenkins Job
-----------

1. Navigate to the Jenkins job you want to integrate with Phabricator.
2. Click the **Configure** button.
3. Click the **Add Parameter** button and select **String Parameter**.
4. Enter **DIFF_ID** in the **Name** field of the parameter.
5. Repeat step 3.
6. Enter **PHID** in the **Name** field of the parameter. ![Configure job parameters](/docs/configure-job-parameters.png)
7. If you want to apply the differential to your workspace before each test run, navigate to the **Build Environment** section and select the **Apply Phabricator Differential** checkbox. This resets to the base commit the differential was generated from. If you'd rather apply the patch to master, select the **Apply patch to master** checkbox.
![Enable build environment](/docs/configure-job-environment.png)
8. To report the build status to Phabricator after the test completes:
  1. Navigate to the **Post-build Actions** section.
  2. Click the **Add post-build action** button and select **Post to Phabricator**.
  3. Make sure the **Comment on Success** and **Comment with console link on Failure** checkboxes are selected.
  4. Optionally, if you have [Uberalls]: https://github.com/uber/uberalls enabled, enter a path to scan for Cobertura reports.
![Add post-build action](/docs/configure-job-post-build.png)

Harbormaster
------------

Once the plugin is configured, you will want to enable harbormaster via herald
rules to trigger jenkins builds on differentials.

First, create a new Harbormaster build plan with a single step, "Make an HTTP
POST request":

Set the URI to
`https://ci.example.com/buildByToken/buildWithParameters?job=test-example&DIFF_ID=${buildable.diff}&PHID=${target.phid}`,
replacing `https://ci.example.com` with the URI of your Jenkins instance, and
`test-example` with the name of your job. If your Jenkins instance is exposed to
the internet, make sure to install the [Build Token Root Plugin][] and fill in
the `token` parameter.

Set the "When Complete" dropdown to "Wait For Message"

![Harbormaster plan](/docs/harbormaster-plan.png)

[Build Token Root Plugin]: https://wiki.jenkins-ci.org/display/JENKINS/Build+Token+Root+Plugin

Additional Comments
-------------------

To allow projects to post back additional text instead of just pass/fail, the
plugin supports a build comment file, which defaults to
`.phabricator-comment`. Put text in here, and Jenkins will add it to the build
status comment.

Herald
------

Next, create a Global herald rule:

![Herald rule](/docs/herald-rule.png)

Fill in your repository name and build plan.

Test it out
-----------

Try `arc diff`-ing on your repo. If everything goes well, you should see Jenkins
commenting on your diff:

![Example](/docs/uberalls-integration.png)

Development
-----------

Set up your maven file according to
https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial


Testing
-------

Start up Jenkins with the plugin installed
```bash
mvn hpi:run
```

Open your browser to your [local instance](http://localhost:8080/jenkins/)

Pull Requests
-------------

Please open all pull requests / issues against
https://github.com/uber/phabricator-jenkins-plugin

License
-------

MIT Licensed

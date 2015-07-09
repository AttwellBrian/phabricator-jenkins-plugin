// Copyright (c) 2015 Uber Technologies, Inc.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.uber.jenkins.phabricator.utils;

import com.uber.jenkins.phabricator.CodeCoverageMetrics;
import com.uber.jenkins.phabricator.LauncherFactory;
import com.uber.jenkins.phabricator.conduit.ArcanistClient;
import com.uber.jenkins.phabricator.uberalls.UberallsClient;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.plugins.cobertura.Ratio;
import hudson.plugins.cobertura.targets.CoverageMetric;
import hudson.plugins.cobertura.targets.CoverageResult;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.mockito.Mockito.*;

public class TestUtils {

    public static final String TEST_BASE_URL = "http://uberalls.example.com";
    public static final String TEST_REPOSITORY = "test-repository";
    public static final String TEST_BRANCH = "test-branch";
    public static final String TEST_SHA = "test-sha";

    public static Logger getDefaultLogger() {
        return new Logger(new PrintStream(new ByteArrayOutputStream()));
    }

    public static UberallsClient getUberallsClient(String baseURL, Logger logger, String repository,
                                             String branch) {
        return spy(new UberallsClient(baseURL, logger, repository, branch));
    }

    public static UberallsClient getDefaultUberallsClient() {
        return getUberallsClient(TEST_BASE_URL, getDefaultLogger(), TEST_REPOSITORY, TEST_BRANCH);
    }

    public static EnvVars getDefaultEnvVars() {
        return new EnvVars();
    }

    public static LauncherFactory createLauncherFactory(JenkinsRule j) throws Exception {
        return new LauncherFactory(
                j.createLocalLauncher(),
                getDefaultEnvVars(),
                System.err,
                new FilePath(j.getWebAppRoot())
        );
    }

    public static CodeCoverageMetrics getCodeCoverageMetrics(String sha1,
                                                             float packagesCoveragePercent,
                                                             float filesCoveragePercent,
                                                             float classesCoveragePercent,
                                                             float methodCoveragePercent,
                                                             float lineCoveragePercent,
                                                             float conditionalCoveragePercent) {
        return spy(new CodeCoverageMetrics(sha1, packagesCoveragePercent, filesCoveragePercent,
                classesCoveragePercent, methodCoveragePercent, lineCoveragePercent,
                conditionalCoveragePercent));
    }

    public static CodeCoverageMetrics getDefaultCodeCoverageMetrics() {
        return getCodeCoverageMetrics(TEST_SHA, 100.0f, 100.0f, 100.0f, 100.0f, 100.0f, 100.0f);
    }

    public static CoverageResult getCoverageResult(Float packageCoverage, Float filesCoverage,
                                                   Float classesCoverage, Float methodCoverage,
                                                   Float linesCoverage) {
        CoverageResult coverageResult = mock(CoverageResult.class);
        setCoverage(coverageResult, CoverageMetric.PACKAGES, packageCoverage);
        setCoverage(coverageResult, CoverageMetric.FILES, filesCoverage);
        setCoverage(coverageResult, CoverageMetric.CLASSES, classesCoverage);
        setCoverage(coverageResult, CoverageMetric.METHOD, methodCoverage);
        setCoverage(coverageResult, CoverageMetric.LINE, linesCoverage);
        return coverageResult;
    }

    public static CoverageResult getDefaultCoverageResult() {
        return getCoverageResult(100.0f, 100.0f, 100.0f, 100.0f, 100.0f);
    }

    private static void setCoverage(CoverageResult coverageResult, CoverageMetric coverageMetric,
                                    Float value) {
        if (value != null) {
            Ratio ratio = Ratio.create(value * 100.0f, 100.0f);
            when(coverageResult.getCoverage(coverageMetric)).thenReturn(ratio);
        }
    }
}

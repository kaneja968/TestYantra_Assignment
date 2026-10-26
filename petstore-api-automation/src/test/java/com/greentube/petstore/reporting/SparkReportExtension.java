package com.greentube.petstore.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.greentube.petstore.config.ApiConfig;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SparkReportExtension implements BeforeEachCallback, TestWatcher, AfterAllCallback {
    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(SparkReportExtension.class);
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        ExtentTest test = report(context).createTest(context.getDisplayName());
        context.getTags().forEach(test::assignCategory);
        CURRENT_TEST.set(test);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        currentTest(context).pass("Test passed");
        CURRENT_TEST.remove();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        currentTest(context).fail(cause);
        CURRENT_TEST.remove();
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        currentTest(context).skip(cause);
        CURRENT_TEST.remove();
    }

    @Override
    public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
        currentTest(context).skip(reason.orElse("Test disabled"));
        CURRENT_TEST.remove();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        CURRENT_TEST.remove();
    }

    private ExtentTest currentTest(ExtensionContext context) {
        ExtentTest test = CURRENT_TEST.get();
        if (test == null) {
            test = report(context).createTest(context.getDisplayName());
            CURRENT_TEST.set(test);
        }
        return test;
    }

    private ReportResource report(ExtensionContext context) {
        return context.getRoot().getStore(NAMESPACE)
                .getOrComputeIfAbsent(ReportResource.class, ignored -> new ReportResource(), ReportResource.class);
    }

    private static final class ReportResource implements ExtensionContext.Store.CloseableResource {
        private final ExtentReports extent;

        private ReportResource() {
            Path reportDirectory = Path.of("target", "spark-report").toAbsolutePath();
            try {
                Files.createDirectories(reportDirectory);
            } catch (IOException exception) {
                throw new IllegalStateException("Could not create Spark report directory: " + reportDirectory, exception);
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(reportDirectory.resolve("Spark.html").toString());
            spark.config().setDocumentTitle("Petstore API Automation");
            spark.config().setReportName("API Test Results");
            spark.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Base URL", ApiConfig.baseUrl());
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("Operating system", System.getProperty("os.name"));
        }

        private synchronized ExtentTest createTest(String name) {
            return extent.createTest(name);
        }

        @Override
        public void close() {
            extent.flush();
        }
    }
}
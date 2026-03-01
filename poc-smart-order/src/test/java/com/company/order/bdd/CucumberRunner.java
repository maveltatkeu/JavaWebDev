package com.company.order.bdd;
import org.junit.platform.suite.api.*;

/**
 * Runner JUnit 5 pour les tests Cucumber BDD.
 *
 * Pointe vers :
 * - Les classes Step Definitions (glue)
 * - Les fichiers .feature (features)
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "com.company.order.bdd")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, html:target/cucumber-reports.html, json:target/cucumber.json")
public class CucumberRunner {}

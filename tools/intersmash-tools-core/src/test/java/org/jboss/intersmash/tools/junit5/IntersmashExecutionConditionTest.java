package org.jboss.intersmash.tools.junit5;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.intersmash.tools.annotations.Intersmash;
import org.jboss.intersmash.tools.annotations.Service;
import org.jboss.intersmash.tools.application.Application;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import cz.xtf.core.config.XTFConfig;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

@ExtendWith(SystemStubsExtension.class)
public class IntersmashExecutionConditionTest {

	@SystemStub
	private SystemProperties systemProperties;

	private static final IntersmashExecutionCondition INTERSMASH_EXECUTION_CONDITION = new IntersmashExecutionCondition();
	private static final List<String> ALL_SUPPORTED = Stream.of(
			Intersmash.Target.Kubernetes.name(), Intersmash.Target.OpenShift.name()).collect(Collectors.toList());

	@Intersmash(value = {
			@Service(Application.class)
	}, target = Intersmash.Target.OpenShift)
	class OpenShiftTargetTestClass {

	}

	@Intersmash(value = {
			@Service(Application.class)
	})
	class DefaultTargetTestClass {

	}

	@Intersmash(value = {
			@Service(Application.class)
	}, target = Intersmash.Target.Kubernetes)
	class KubernetesTargetTestClass {

	}

	@BeforeEach
	void before() {
		systemProperties.set("intersmash.junit5.execution.targets", "");
	}

	@Test
	void evaluateExecutionCondition_targetingDefaultTestIsEnabledWhenJustOpenShiftIsSupported() {
		// Arrange
		systemProperties.set("intersmash.junit5.execution.targets", Intersmash.Target.OpenShift.name());
		XTFConfig.loadConfig();
		ExtensionContext extensionContext = new EmptyExtensionContext(DefaultTargetTestClass.class);
		// Act
		ConditionEvaluationResult conditionEvaluationResult = INTERSMASH_EXECUTION_CONDITION
				.evaluateExecutionCondition(extensionContext);
		// Assert
		Assertions.assertFalse(conditionEvaluationResult.isDisabled(),
				"The test - which is targeting the default environment - should be enabled");
	}

	@Test
	void evaluateExecutionCondition_targetingOpenShiftTestIsDisabledWhenJustKubernetesIsSupported() {
		// Arrange
		systemProperties.set("intersmash.junit5.execution.targets", Intersmash.Target.Kubernetes.name());
		XTFConfig.loadConfig();
		ExtensionContext extensionContext = new EmptyExtensionContext(OpenShiftTargetTestClass.class);
		// Act
		ConditionEvaluationResult conditionEvaluationResult = INTERSMASH_EXECUTION_CONDITION
				.evaluateExecutionCondition(extensionContext);
		// Assert
		Assertions.assertTrue(conditionEvaluationResult.isDisabled(),
				"The test - which is targeting the OpenShift environment - should be disabled");
	}

	@Test
	void evaluateExecutionCondition_targetingOpenshiftTestIsEnabledWhenJustOpenShiftIsSupported() {
		// Arrange
		systemProperties.set("intersmash.junit5.execution.targets", Intersmash.Target.OpenShift.name());
		XTFConfig.loadConfig();
		ExtensionContext extensionContext = new EmptyExtensionContext(OpenShiftTargetTestClass.class);
		// Act
		ConditionEvaluationResult conditionEvaluationResult = INTERSMASH_EXECUTION_CONDITION
				.evaluateExecutionCondition(extensionContext);
		// Assert
		Assertions.assertFalse(conditionEvaluationResult.isDisabled(),
				"The test - which is targeting the OpenShift environment - should be enabled");
	}

	@Test
	void evaluateExecutionCondition_targetingOpenshiftTestIsEnabledWhenOpenShiftIsSupportedToo() {
		// Arrange
		systemProperties.set("intersmash.junit5.execution.targets", ALL_SUPPORTED.stream().collect(Collectors.joining(",")));
		XTFConfig.loadConfig();
		ExtensionContext extensionContext = new EmptyExtensionContext(OpenShiftTargetTestClass.class);
		// Act
		ConditionEvaluationResult conditionEvaluationResult = INTERSMASH_EXECUTION_CONDITION
				.evaluateExecutionCondition(extensionContext);
		// Assert
		Assertions.assertFalse(conditionEvaluationResult.isDisabled(),
				"The test - which is targeting the OpenShift environment - should be enabled");
	}

	@Test
	void evaluateExecutionCondition_targetingKubernetesTestIsDisabledWhenJustOpenShiftIsSupported() {
		// Arrange
		systemProperties.set("intersmash.junit5.execution.targets", Intersmash.Target.OpenShift.name());
		XTFConfig.loadConfig();
		ExtensionContext extensionContext = new EmptyExtensionContext(KubernetesTargetTestClass.class);
		// Act
		ConditionEvaluationResult conditionEvaluationResult = INTERSMASH_EXECUTION_CONDITION
				.evaluateExecutionCondition(extensionContext);
		// Assert
		Assertions.assertTrue(conditionEvaluationResult.isDisabled(),
				"The test - which is targeting the Kubernetes environment - should be disabled");
	}

	@Test
	void evaluateExecutionCondition_targetingKubernetesTestIsEnabledWhenJustKubernetesIsSupported() {
		// Arrange
		systemProperties.set("intersmash.junit5.execution.targets", Intersmash.Target.Kubernetes.name());
		XTFConfig.loadConfig();
		ExtensionContext extensionContext = new EmptyExtensionContext(KubernetesTargetTestClass.class);
		// Act
		ConditionEvaluationResult conditionEvaluationResult = INTERSMASH_EXECUTION_CONDITION
				.evaluateExecutionCondition(extensionContext);
		// Assert
		Assertions.assertFalse(conditionEvaluationResult.isDisabled(),
				"The test - which is targeting the Kubernetes environment - should be enabled");
	}

	@Test
	void evaluateExecutionCondition_targetingKubernetesTestIsEnabledWhenKubernetesIsSupportedToo() {
		// Arrange
		systemProperties.set("intersmash.junit5.execution.targets", ALL_SUPPORTED.stream().collect(Collectors.joining(",")));
		XTFConfig.loadConfig();
		ExtensionContext extensionContext = new EmptyExtensionContext(KubernetesTargetTestClass.class);
		// Act
		ConditionEvaluationResult conditionEvaluationResult = INTERSMASH_EXECUTION_CONDITION
				.evaluateExecutionCondition(extensionContext);
		// Assert
		Assertions.assertFalse(conditionEvaluationResult.isDisabled(),
				"The test - which is targeting the Kubernetes environment - should be enabled");
	}
}

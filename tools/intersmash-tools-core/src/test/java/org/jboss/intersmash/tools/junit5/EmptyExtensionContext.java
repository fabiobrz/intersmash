package org.jboss.intersmash.tools.junit5;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;

public class EmptyExtensionContext implements ExtensionContext {
	private final Class<?> testClazz;

	public EmptyExtensionContext(Class<?> testClazz) {
		this.testClazz = testClazz;
	}

	@Override
	public Optional<ExtensionContext> getParent() {
		return Optional.empty();
	}

	@Override
	public ExtensionContext getRoot() {
		return null;
	}

	@Override
	public String getUniqueId() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public Set<String> getTags() {
		return null;
	}

	@Override
	public Optional<AnnotatedElement> getElement() {
		return Optional.empty();
	}

	@Override
	public Optional<Class<?>> getTestClass() {
		return Optional.of(testClazz);
	}

	@Override
	public Optional<TestInstance.Lifecycle> getTestInstanceLifecycle() {
		return Optional.empty();
	}

	@Override
	public Optional<Object> getTestInstance() {
		return Optional.empty();
	}

	@Override
	public Optional<TestInstances> getTestInstances() {
		return Optional.empty();
	}

	@Override
	public Optional<Method> getTestMethod() {
		return Optional.empty();
	}

	@Override
	public Optional<Throwable> getExecutionException() {
		return Optional.empty();
	}

	@Override
	public Optional<String> getConfigurationParameter(String s) {
		return Optional.empty();
	}

	@Override
	public <T> Optional<T> getConfigurationParameter(String s, Function<String, T> function) {
		return Optional.empty();
	}

	@Override
	public void publishReportEntry(Map<String, String> map) {

	}

	@Override
	public Store getStore(Namespace namespace) {
		return null;
	}
}

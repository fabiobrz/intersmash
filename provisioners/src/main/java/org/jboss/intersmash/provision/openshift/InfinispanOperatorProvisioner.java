/**
 * Copyright (C) 2023 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
package org.jboss.intersmash.provision.openshift;
========
package org.jboss.intersmash.tools.provision.operator;
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
import org.infinispan.v1.Infinispan;
import org.infinispan.v1.infinispanspec.Expose;
import org.infinispan.v2alpha1.Cache;
import org.jboss.intersmash.IntersmashConfig;
import org.jboss.intersmash.application.openshift.InfinispanOperatorApplication;
import org.jboss.intersmash.provision.openshift.operator.infinispan.cache.CacheList;
import org.jboss.intersmash.provision.openshift.operator.infinispan.infinispan.InfinispanList;
import org.jboss.intersmash.provision.openshift.operator.infinispan.infinispan.spec.InfinispanConditionBuilder;
========
import org.jboss.intersmash.tools.IntersmashConfig;
import org.jboss.intersmash.tools.application.operator.InfinispanOperatorApplication;
import org.jboss.intersmash.tools.provision.Provisioner;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.cache.Cache;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.cache.CacheList;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.Infinispan;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.InfinispanList;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.spec.ExposeSpecBuilder;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.spec.InfinispanConditionBuilder;
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
import org.slf4j.event.Level;

import cz.xtf.core.waiting.SimpleWaiter;
import cz.xtf.core.waiting.failfast.FailFastCheck;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.dsl.internal.HasMetadataOperationsImpl;
import io.fabric8.openshift.api.model.Route;

public interface InfinispanOperatorProvisioner extends
		OlmOperatorProvisioner<InfinispanOperatorApplication>, Provisioner<InfinispanOperatorApplication> {

	// this is the packagemanifest for the operator;
	// you can get it with command:
	// oc get packagemanifest <bundle> -o template --template='{{ .metadata.name }}'
	static String operatorId() {
		return IntersmashConfig.infinispanOperatorPackageManifest();
	}

	// this is the name of the CustomResourceDefinition(s)
	// you can get it with command:
	// oc get crd <group> -o template --template='{{ .metadata.name }}'
	default String infinispanCustomResourceDefinitionName() {
		return "infinispans.infinispan.org";
	}

	// this is the name of the CustomResourceDefinition(s)
	// you can get it with command:
	// oc get crd <group> -o template --template='{{ .metadata.name }}'
	default String cacheCustomResourceDefinitionName() {
		return "caches.infinispan.org";
	}

	default void deploy() {
		FailFastCheck ffCheck = () -> false;
		subscribe();
		// create Infinispan CR
		final int replicas = getApplication().getInfinispan().getSpec().getReplicas();
		infinispansClient().createOrReplace(getApplication().getInfinispan());
		if (replicas > 0) {
			new SimpleWaiter(() -> getInfinispanPods().size() == replicas).level(Level.DEBUG).waitFor();
		}
		// create Cache CR(s)
		if (getApplication().getCaches().size() > 0) {
			getApplication().getCaches().stream().forEach((i) -> cachesClient().resource(i).create());
		}
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
========

		// This might be a litle bit naive, but we need more use cases to see how will this behave and what other
		// use-cases we have to cover wait for infinispan pods - look for "clusterName" in infinispan pod
		if (replicas > 0) {
			BooleanSupplier bs = () -> retrievePods().stream()
					.filter(p -> p.getMetadata().getLabels().get("clusterName") != null && p.getMetadata().getLabels()
							.get("clusterName").equals(getApplication().getInfinispan().getMetadata().getName()))
					.collect(Collectors.toList()).size() == replicas;
			new SimpleWaiter(bs, TimeUnit.MINUTES, 2,
					"Waiting for " + replicas + " pods with label \"clusterName\"="
							+ getApplication().getInfinispan().getMetadata().getName())
					.waitFor();
		}
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
		// wait for all resources to be ready
		waitForResourceReadiness();
	}

<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
	@Override
	public void undeploy() {
		// delete Cache CR(s)
========
	default void undeploy() {
		FailFastCheck ffCheck = () -> false;
		// delete custom resources
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
		caches().forEach(keycloakUser -> keycloakUser.withPropagationPolicy(DeletionPropagation.FOREGROUND).delete());
		// delete Infinispan CR
		infinispan().withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
		// wait for 0 pods, and here it waits for no pods to exist with the `clusterName=<INFINISPAN_APP_NAME>` label,
		// since all CRs have been deleted
		OpenShiftWaiters.get(OpenShiftProvisioner.openShift, ffCheck)
				.areExactlyNPodsReady(0, "clusterName", getApplication().getInfinispan().getMetadata().getName())
				.level(Level.DEBUG)
========
		// wait for 0 pods
		BooleanSupplier bs = () -> retrievePods().stream()
				.filter(p -> p.getMetadata().getLabels().get("clusterName") != null && p.getMetadata().getLabels()
						.get("clusterName").equals(getApplication().getInfinispan().getMetadata().getName()))
				.collect(Collectors.toList()).size() == 0;
		new SimpleWaiter(bs, TimeUnit.MINUTES, 2,
				"Waiting for 0 pods with label \"clusterName\"=" + getApplication().getInfinispan().getMetadata().getName())
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
				.waitFor();
		unsubscribe();
	}

<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
	@Override
	public void scale(int replicas, boolean wait) {
========
	StatefulSet retrieveNamedStatefulSet(final String statefulSetName);

	default void scale(int replicas, boolean wait) {
		StatefulSet statefulSet = retrieveNamedStatefulSet(getApplication().getName());
		if (Objects.isNull(statefulSet)) {
			throw new IllegalStateException(String.format(
					"Impossible to scale non existent StatefulSet with name=\"%s\" to replicas=%d",
					getApplication().getName(),
					replicas));
		}
		String controllerRevisionHash = statefulSet.getStatus().getUpdateRevision();
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
		Infinispan tmpInfinispan = infinispan().get();
		tmpInfinispan.getSpec().setReplicas(replicas);
		infinispan().replace(tmpInfinispan);
		if (wait) {
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
			// waits for the correct number of Pods representing the Infinispan CR replicas to be ready
			new SimpleWaiter(() -> getInfinispanPods().size() == replicas).level(Level.DEBUG).waitFor();
			if (replicas > 0) {
				waitForResourceReadiness();
			}
========
			BooleanSupplier bs = () -> retrievePods().stream()
					.filter(p -> p.getMetadata().getLabels().get("controller-revision-hash") != null
							&& p.getMetadata().getLabels().get("controller-revision-hash").equals(controllerRevisionHash))
					.collect(Collectors.toList()).size() == replicas;
			new SimpleWaiter(bs, TimeUnit.MINUTES, 2,
					"Waiting for pods with label \"controller-revision-hash\"=" + controllerRevisionHash + " to be scaled")
					.waitFor();
		}
		if (replicas > 0) {
			//	see https://github.com/kubernetes/apimachinery/blob/v0.20.4/pkg/apis/meta/v1/types.go#L1289
			new SimpleWaiter(
					() -> infinispan().get().getStatus().getConditions().stream()
							.anyMatch(c -> c.getType()
									.equals(InfinispanConditionBuilder.ConditionType.ConditionWellFormed.getValue())
									&& c.getStatus().equals("True")))
					.reason("Wait for infinispan resource to be ready").level(Level.DEBUG).waitFor();
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
		}
	}

	default List<Pod> getPods() {
		StatefulSet statefulSet = retrieveNamedStatefulSet(getApplication().getName());
		return Objects.nonNull(statefulSet)
				? retrievePods().stream()
						.filter(p -> p.getMetadata().getLabels().get("controller-revision-hash") != null
								&& p.getMetadata().getLabels().get("controller-revision-hash")
										.equals(statefulSet.getStatus().getUpdateRevision()))
						.collect(Collectors.toList())
				: Lists.emptyList();
	}

<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
	public List<Pod> getInfinispanPods() {
		return getInfinispanPods(getApplication().getName());
	}

	public static List<Pod> getInfinispanPods(final String clusterName) {
		return OpenShiftProvisioner.openShift.inNamespace(OpenShiftConfig.namespace()).pods().list().getItems().stream().filter(
				// the following criteria is implemented based on similar requirements taken from the
				// infinispan-operator project, see
				// https://github.com/infinispan/infinispan-operator/blob/main/test/e2e/utils/kubernetes.go#L599-L604
				p -> p.getMetadata().getLabels().entrySet().stream()
						.anyMatch(tl -> "app".equals(tl.getKey()) && "infinispan-pod".equals(tl.getValue())
								&& p.getMetadata().getLabels().entrySet().stream().anyMatch(
										cnl -> "clusterName".equals(cnl.getKey()) && clusterName.equals(cnl.getValue()))))
				.collect(Collectors.toList());
	}

	@Override
	protected String getOperatorCatalogSource() {
========
	default String getOperatorCatalogSource() {
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
		return IntersmashConfig.infinispanOperatorCatalogSource();
	}

	default String getOperatorIndexImage() {
		return IntersmashConfig.infinispanOperatorIndexImage();
	}

	default String getOperatorChannel() {
		return IntersmashConfig.infinispanOperatorChannel();
	}

	/**
	 * The result is affected by the CR definition and specifically the method will return the {@code service} URL in
	 * case the CR {@code .spec.expose.type} is set to {@code NodePort} or {@code LoadBalancer} while it will return the
	 * route URL (i.e. for external access) when {@code .spec.expose.type} is set to {@code Route}
	 * @return The URL for the provisioned Infinispan service
	 */
	default URL getURL() {
		final Service defaultInternalService = retrieveNamedService(getApplication().getName());
		String internalUrl = "http://" + defaultInternalService.getSpec().getClusterIP() + ":11222";
		String externalUrl = null;
		if (getApplication().getInfinispan().getSpec().getExpose() != null) {
			final Expose.Type exposedType = getApplication().getInfinispan().getSpec().getExpose().getType();
			switch (exposedType) {
				case NodePort:
					// see see https://github.com/infinispan/infinispan-operator/blob/2.0.x/pkg/apis/infinispan/v1/infinispan_types.go#L107
					externalUrl = "http://"
							+ retrieveNamedService(getApplication().getName() + "-external").getSpec()
									.getClusterIP()
							+ getApplication().getInfinispan().getSpec().getExpose().getNodePort();
					break;
				case LoadBalancer:
					//	see https://github.com/infinispan/infinispan-operator/blob/2.0.x/pkg/apis/infinispan/v1/infinispan_types.go#L111
					externalUrl = "http://"
							+ retrieveNamedService(getApplication().getName() + "-external").getSpec()
									.getExternalIPs().get(0)
							+ getApplication().getInfinispan().getSpec().getExpose().getNodePort();
					break;
				case Route:
					//	https://github.com/infinispan/infinispan-operator/blob/2.0.x/pkg/apis/infinispan/v1/infinispan_types.go#L116
					Route route = retrieveNamedRoute(getApplication().getName() + "-external");
					externalUrl = "https://" + route.getSpec().getHost();
					break;
				default:
					throw new UnsupportedOperationException(String.format("Unsupported .spec.expose.type: %s", exposedType));
			}
		}
		try {
			return new URL(externalUrl == null ? internalUrl : externalUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format("Infinispan operator Internal URL \"%s\" is malformed.", internalUrl), e);
		}
	}

<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisioner.java
========
	Route retrieveNamedRoute(final String routeName);

	Service retrieveNamedService(final String serviceName);

	HasMetadataOperationsImpl<Infinispan, InfinispanList> infinispanCustomResourcesClient(CustomResourceDefinitionContext crdc);

	HasMetadataOperationsImpl<Cache, CacheList> cacheCustomResourcesClient(CustomResourceDefinitionContext crdc);

	NonNamespaceOperation<Infinispan, InfinispanList, Resource<Infinispan>> infinispansClient();

	NonNamespaceOperation<Cache, CacheList, Resource<Cache>> cachesClient();

>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/operator/InfinispanOperatorProvisioner.java
	/**
	 * Get a client capable of working with {@link InfinispanOperatorProvisioner#infinispanCustomResourceDefinitionName()} custom resource.
	 *
	 * @return client for operations with {@link InfinispanOperatorProvisioner#infinispanCustomResourceDefinitionName()} custom resource
	 */

	default MixedOperation<Infinispan, InfinispanList, Resource<Infinispan>> buildInfinispansClient() {
		CustomResourceDefinition crd = retrieveCustomResourceDefinitions()
				.withName(infinispanCustomResourceDefinitionName()).get();
		CustomResourceDefinitionContext crdc = CustomResourceDefinitionContext.fromCrd(crd);
		if (!retrieveCustomResourceDefinitions().list().getItems().contains(infinispanCustomResourceDefinitionName())) {
			throw new RuntimeException(String.format("[%s] custom resource is not provided by [%s] operator.",
					infinispanCustomResourceDefinitionName(), operatorId()));
		}
		return infinispanCustomResourcesClient(crdc);
	}

	/**
	 * Get a reference to infinispan object. Use get() to get the actual object, or null in case it does not
	 * exist on tested cluster.
	 * @return A concrete {@link Resource} instance representing the {@link Infinispan} resource definition
	 */
	default Resource<Infinispan> infinispan() {
		return infinispansClient().withName(getApplication().getInfinispan().getMetadata().getName());
	}

	// caches.infinispan.org

	/**
	 * Get a client capable of working with {@link InfinispanOperatorProvisioner#cacheCustomResourceDefinitionName()} custom resource.
	 *
	 * @return client for operations with {@link InfinispanOperatorProvisioner#cacheCustomResourceDefinitionName()} custom resource
	 */
	default MixedOperation<Cache, CacheList, Resource<Cache>> buildCachesClient() {
		CustomResourceDefinition crd = retrieveCustomResourceDefinitions()
				.withName(cacheCustomResourceDefinitionName()).get();
		CustomResourceDefinitionContext crdc = CustomResourceDefinitionContext.fromCrd(crd);
		if (!retrieveCustomResourceDefinitions().list().getItems().contains(cacheCustomResourceDefinitionName())) {
			throw new RuntimeException(String.format("[%s] custom resource is not provided by [%s] operator.",
					cacheCustomResourceDefinitionName(), operatorId()));
		}
		return cacheCustomResourcesClient(crdc);
	}

	/**
	 * Get a reference to cache object. Use get() to get the actual object, or null in case it does not
	 * exist on tested cluster.
	 *
	 * @param name name of the cache custom resource
	 * @return A concrete {@link Resource} instance representing the {@link Cache} resource definition
	 */
	default Resource<Cache> cache(String name) {
		return cachesClient().withName(name);
	}

	/**
	 * Get all caches maintained by the current operator instance.
	 * <p>
	 * Be aware that this method returns just a references to the addresses, they might not actually exist on the cluster.
	 * Use get() to get the actual object, or null in case it does not exist on tested cluster.
	 * @return A list of {@link Resource} instances representing the {@link Cache} resource definitions
	 */
	default List<Resource<Cache>> caches() {
		InfinispanOperatorApplication infinispanOperatorApplication = getApplication();
		return infinispanOperatorApplication.getCaches().stream()
				.map(cache -> cache.getMetadata().getName())
				.map(this::cache)
				.collect(Collectors.toList());
	}

	private void waitForResourceReadiness() {
		// it must be well-formed
		// see https://github.com/kubernetes/apimachinery/blob/v0.20.4/pkg/apis/meta/v1/types.go#L1289
		new SimpleWaiter(
				() -> infinispan().get().getStatus().getConditions().stream()
						.anyMatch(
								c -> c.getType().equals(InfinispanConditionBuilder.ConditionType.ConditionWellFormed.getValue())
										&& c.getStatus().equals("True")))
				.reason("Wait for infinispan resource to be ready").level(Level.DEBUG)
				.waitFor();
		// and with the expected number of Cache CR(s)
		if (getApplication().getCaches().size() > 0)
			new SimpleWaiter(() -> cachesClient().list().getItems().size() == caches().size())
					.reason("Wait for caches to be ready.").level(Level.DEBUG).waitFor(); // no isReady() for cache
	}
}

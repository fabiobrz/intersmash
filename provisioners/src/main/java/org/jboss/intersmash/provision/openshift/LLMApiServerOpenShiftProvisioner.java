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
package org.jboss.intersmash.provision.openshift;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jboss.intersmash.application.openshift.LLMApiServerOpenShiftApplication;
import org.slf4j.event.Level;

import cz.xtf.core.config.WaitingConfig;
import cz.xtf.core.event.helpers.EventHelper;
import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.waiting.failfast.FailFastCheck;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ConfigMapVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KeyToPathBuilder;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.QuantityBuilder;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePortBuilder;
import io.fabric8.openshift.api.model.RouteTargetReferenceBuilder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Deploys a LLM Api Server application based on {@link LLMApiServerOpenShiftApplication}
 */
@Slf4j
public class LLMApiServerOpenShiftProvisioner implements OpenShiftProvisioner<LLMApiServerOpenShiftApplication> {

	public static final String LLM_API_CACHE_PVC_NAME = "pvc-llm-api";
	public static final String LLM_API_CACHE_PVC_ACCESS_MODES = "ReadWriteMany";
	public static final String LLM_API_CACHE_PVC_REQUEST_STORAGE_ID = "storage";
	public static final String LLM_API_CACHE_PVC_STORAGE_CLASS_NAME = "standard-csi";
	public static final String LLM_API_CACHE_VOLUME_NAME = "llm-api-cache";
	public static final String LLM_API_CONFIGURATION_CONFIG_MAP_NAME = "ray-config";
	public static final String LLM_API_CACHE_VOLUME_MOUNT_PATH = "/home/llm-api";
	public static final String LLM_API_CONFIG_VOLUME_NAME = "llm-api-config";
	public static final String LLM_API_SERVICE_NAME = "llm-api-svc";
	public static final String LLM_API_SERVICE_PORT_NAME = "llm-api-server";
	public static final int LLM_API_SERVICE_PORT = 80;
	public static final String LLM_API_SESSION_AFFINITY = "ClientIP";
	private final LLMApiServerOpenShiftApplication application;
	private FailFastCheck ffCheck = () -> false;

	public LLMApiServerOpenShiftProvisioner(@NonNull LLMApiServerOpenShiftApplication application) {
		this.application = application;
	}

	@Override
	public LLMApiServerOpenShiftApplication getApplication() {
		return application;
	}

	/**
	 * Deploy the LLM API Server application service
	 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md
	 */
	@Override
	public void deploy() {
		deployImage();
	}

	@Override
	public void undeploy() {
		// remove route
		openShift.deleteRoute(openShift.routes().inNamespace(openShift.getNamespace()).withName(LLM_API_SERVICE_NAME).get());
		// remove service
		openShift
				.deleteService(openShift.services().inNamespace(openShift.getNamespace()).withName(LLM_API_SERVICE_NAME).get());
		// remove deployment
		openShift.apps().deployments().inNamespace(openShift.getNamespace()).withName(getApplication().getName()).delete();
		// additional cleanup
		OpenShiftUtils.deleteResourcesWithLabel(openShift, APP_LABEL_KEY, application.getName());
	}

	@Override
	public void scale(int replicas, boolean wait) {
		openShift.scale(application.getName(), replicas);
		if (wait) {
			waitForReplicas(replicas);
		}
	}

	public void waitForReplicas(int replicas) {
		OpenShiftWaiters.get(openShift, ffCheck).areExactlyNPodsReady(replicas, "app", getApplication().getName())
				.level(Level.DEBUG)
				.waitFor();
		WaitersUtil.serviceEndpointsAreReady(openShift, getApplication().getName(), replicas, 80)
				.level(Level.DEBUG)
				.waitFor();
		if (replicas > 0) {
			WaitersUtil.routeIsUp(getUrl(application.getName(), false))
					.level(Level.DEBUG)
					.waitFor();
		}
	}

	/**
	 * Creates a persistent volume claim to store the LLM API Server cache.
	 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md#step-3-create-persistent-volume
	 */
	private void createCachePersistentVolume() {
		openShift.inNamespace(openShift.getNamespace()).persistentVolumeClaims().resource(
				new PersistentVolumeClaimBuilder()
						.withNewMetadata()
						.withName(LLM_API_CACHE_PVC_NAME)
						.and()
						.withNewSpec()
						.withAccessModes(LLM_API_CACHE_PVC_ACCESS_MODES)
						.withResources(
								new ResourceRequirementsBuilder()
										.withRequests(Map.of(LLM_API_CACHE_PVC_REQUEST_STORAGE_ID,
												getApplication().getCacheSizeRequest()))
										.build())
						.withStorageClassName(LLM_API_CACHE_PVC_STORAGE_CLASS_NAME)
						.endSpec()
						.build())
				.create();
	}

	/**
	 * Creates a ConfigMap resource to store the LLM API Server configuration.
	 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md#step-4-prepare-config-file-and-create-configmap
	 */
	private void createConfigurationPersistentVolume() throws IOException {
		try (FileInputStream fis = new FileInputStream(getApplication().getConfigFilePath().toFile())) {
			openShift.createConfigMap(new ConfigMapBuilder()
					.withNewMetadata()
					.withName(LLM_API_CONFIGURATION_CONFIG_MAP_NAME)
					.endMetadata()
					.addToData(getApplication().getConfigFilePath().toFile().getName(),
							new String(fis.readAllBytes(), StandardCharsets.UTF_8))
					.build());
		}
	}

	/**
	 * Creates the LLM API Server {@link Deployment} resource, and adds the service and external route
	 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md#step-5-llm-api-deployment
	 */
	private void deployImage() {
		ffCheck = FailFastUtils.getFailFastCheck(EventHelper.timeOfLastEventBMOrTestNamespaceOrEpoch(),
				application.getName());

		final List<Volume> volumes = new ArrayList<>();
		final List<VolumeMount> volumeMounts = new ArrayList<>();
		final Path configurationFilePath = getApplication().getConfigFilePath();

		// initialize a Volume resource for the LLM API Server cache
		volumes.add(new VolumeBuilder()
				.withName(LLM_API_CACHE_VOLUME_NAME)
				.withPersistentVolumeClaim(
						new PersistentVolumeClaimVolumeSourceBuilder()
								.withClaimName(LLM_API_CACHE_PVC_NAME)
								.withReadOnly(false)
								.build())
				.build());
		// and define a mount point for it
		volumeMounts.add(
				new VolumeMountBuilder()
						.withName(LLM_API_CACHE_VOLUME_NAME)
						.withMountPath(LLM_API_CACHE_VOLUME_MOUNT_PATH)
						.build());
		// Given a configuration file...
		if (configurationFilePath == null) {
			throw new IllegalStateException("No configuration file defined");
		}
		if (!configurationFilePath.toFile().exists()) {
			throw new IllegalStateException(
					String.format("Configuration file not found: %s", configurationFilePath.toFile().getAbsolutePath()));
		}
		// create a Volume resource to store the ConfigMap holding the server configuration
		volumes.add(
				new VolumeBuilder()
						.withName(LLM_API_CONFIG_VOLUME_NAME)
						.withConfigMap(
								new ConfigMapVolumeSourceBuilder()
										.withName(LLM_API_CONFIGURATION_CONFIG_MAP_NAME)
										.withDefaultMode(420)
										.withItems(new KeyToPathBuilder()
												.withKey(configurationFilePath.toFile().getName())
												.withPath(configurationFilePath.toFile().getName())
												.build())
										.build())
						.build());
		// and define a mount point for it
		volumeMounts.add(
				new VolumeMountBuilder()
						.withName(LLM_API_CONFIG_VOLUME_NAME)
						.withMountPath("/llm_api_server/" + configurationFilePath.toFile().getName())
						.withSubPath(configurationFilePath.toFile().getName())
						.build());

		//  create the container
		Container container = new ContainerBuilder()
				.withName(getApplication().getName())
				.withImage("quay.io/redhat_emp1/llm-api-server:latest")
				.withEnv(
						new EnvVarBuilder()
								.withName("HOME")
								.withValue(LLM_API_CACHE_VOLUME_MOUNT_PATH)
								.build())
				.withPorts(new ContainerPortBuilder().withProtocol("TCP")
						.withContainerPort(getApplication().getServicePort()).build())
				.withVolumeMounts(volumeMounts)
				.build();
		Integer nvidiaComGpuResourceLimit = getApplication().getNvidiaComGpuResourceLimit();
		if (nvidiaComGpuResourceLimit > 0) {
			container.setResources(
					new ResourceRequirementsBuilder()
							.withLimits(Map.of("nvidia.com/gpu",
									new QuantityBuilder().withAmount(nvidiaComGpuResourceLimit.toString()).build()))
							.build());
		}
		// and finally the deployment configuration
		Deployment deployment = new DeploymentBuilder()
				.withMetadata(new ObjectMetaBuilder()
						.withLabels(getAppLabels())
						.withName(application.getName())
						.withNamespace(openShift.getNamespace()).build())
				.withNewSpec()
				.withReplicas(getApplication().getReplicas())
				.withSelector(new LabelSelectorBuilder().withMatchLabels(getAppLabels()).build())
				.withTemplate(new PodTemplateSpecBuilder()
						.withNewMetadata()
						.withLabels(getAppLabels())
						.endMetadata()
						.withNewSpec()
						.withContainers(container)
						.withVolumes(volumes)
						.and().build())
				.and().build();

		openShift.apps().deployments().inNamespace(openShift.getNamespace()).resource(deployment).create();
		openShift.apps().deployments().resource(deployment).waitUntilReady(WaitingConfig.timeout(), TimeUnit.MILLISECONDS);

		createService();
		createRoute();

		// 1 by default
		waitForReplicas(1);
	}

	/**
	 * Creates a Service resource to expose the LLM API Server service within the cluster
	 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md#step-6-create-a-service
	 */
	private void createService() {
		openShift.services().inNamespace(openShift.getNamespace()).resource(
				new ServiceBuilder()
						.withNewMetadata()
						.withName(LLM_API_SERVICE_NAME)
						.withLabels(getAppLabels())
						.endMetadata()
						.withNewSpec()
						.withSelector(getAppLabels())
						.withPorts(new ServicePortBuilder()
								.withName(LLM_API_SERVICE_PORT_NAME)
								.withPort(LLM_API_SERVICE_PORT)
								.withTargetPort(new IntOrString(getApplication().getServicePort()))
								.build())
						.withSessionAffinity(LLM_API_SESSION_AFFINITY)
						.endSpec()
						.build())
				.create();
	}

	private Map<String, String> getAppLabels() {
		return Map.of("app", getApplication().getName());
	}

	/**
	 * Creates a Route resource to expose the LLM API Server service outside the cluster
	 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md#step-6-create-a-service
	 */
	private void createRoute() {
		openShift.routes().inNamespace(openShift.getNamespace()).resource(
				new RouteBuilder()
						.withNewMetadata()
						.withName(LLM_API_SERVICE_NAME)
						.endMetadata()
						.withNewSpec()
						.withTo(new RouteTargetReferenceBuilder()
								.withKind(Service.class.getSimpleName())
								.withName(LLM_API_SERVICE_NAME)
								.build())
						.withPort(new RoutePortBuilder().withTargetPort(new IntOrString(80)).build())
						.endSpec()
						.build())
				.create();
	}

	@Override
	public List<Pod> getPods() {
		return openShift.getPods(getApplication().getName());
	}

	@Override
	public void preDeploy() {
		OpenShiftProvisioner.super.preDeploy();
		createCachePersistentVolume();
		try {
			createConfigurationPersistentVolume();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void postUndeploy() {
		removeConfigurationPersistentVolume();
		removeCachePersistentVolume();
		OpenShiftProvisioner.super.postUndeploy();
	}

	private static void removeCachePersistentVolume() {
		// remove the PVC holding the data
		openShift.deletePersistentVolumeClaim(openShift.persistentVolumeClaims().inNamespace(openShift.getNamespace())
				.withName(LLM_API_CACHE_PVC_NAME).get());
	}

	private void removeConfigurationPersistentVolume() {
		// remove the ConfigMap holding the LLM API Server configuration
		openShift.deleteConfigMap(openShift.configMaps().inNamespace(openShift.getNamespace())
				.withName(getApplication().getConfigFilePath().toFile().getName()).get());
	}
}

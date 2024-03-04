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
package org.jboss.intersmash.application.openshift;

import java.nio.file.Path;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.QuantityBuilder;

/**
 * Represents a base contract for the LLM Server API application service on OpenShift
 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md
 */
public interface LLMApiServerOpenShiftApplication extends OpenShiftApplication {

	/**
	 * Define the size of the persistent volume which is requested to hold the LLM API Server cache
	 * @return {@link Quantity} instance representing the size of the persistent volume which is requested to hold the LLM API Server cache
	 */
	default Quantity getCacheSizeRequest() {
		return new QuantityBuilder().withAmount("30").withFormat("Gi").build();
	}

	/**
	 * Define the port that the LLM Server API service will run on
	 * @return Integer representing the port that the LLM Server API service will run on
	 */
	default Integer getServicePort() {
		return 7000;
	}

	/**
	 * Define the path of a valid configuration file for the LLM Server API.
	 * @return A {@link Path} instance representing the path of a valid configuration file for the LLM Server API.
	 */
	Path getConfigFilePath();

	/**
	 * Define the number of LLM API Server pods that will run the workload.
	 * @return A positive integer holding the number of LLM API Server pods that will run the workload
	 */
	default Integer getReplicas() {
		return 1;
	}

	/**
	 * Define the value for the {@code nvidia.com/gpu} item of the
	 * {@code .template.spec.containers.resources.limits} in the server container configuration.
	 * @return A positive integer holding the limit for the {@code nvidia.com/gpu} resource item.
	 *
	 * See https://github.com/bekkermans/llm-api-server/blob/main/docs/OpenShift.md#step-5-llm-api-deployment
	 */
	default Integer getNvidiaComGpuResourceLimit() {
		return 1;
	}
}

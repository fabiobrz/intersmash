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
package org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.status;

import java.util.List;

import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.spec.InfinispanCondition;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.spec.InfinispanSecurity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * InfinispanStatus defines the observed state of Infinispan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class InfinispanStatus {

	/**
	 * Conditions that define the Infinispan resource status
	 */
	private List<InfinispanCondition> conditions;

	/**
	 * Names the Infinispan resource stateful set
	 */
	private String statefulSetName;

	/**
	 * Specify the security configuration
	 */
	private InfinispanSecurity security;

	/**
	 * Specify the number of replicas needed at restart
	 */
	private int replicasWantedAtRestart;
}
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
package org.jboss.intersmash.testsuite.provision.k8s;

import java.io.IOException;

import org.jboss.intersmash.application.k8s.KubernetesApplication;
import org.jboss.intersmash.application.operator.HyperfoilOperatorApplication;
import org.jboss.intersmash.junit5.IntersmashExtension;
import org.jboss.intersmash.k8s.KubernetesConfig;
import org.jboss.intersmash.k8s.client.Kuberneteses;
import org.jboss.intersmash.provision.k8s.HyperfoilKubernetesOperatorProvisioner;
import org.jboss.intersmash.provision.olm.OperatorGroup;
import org.jboss.intersmash.provision.operator.hyperfoil.client.v05.invoker.ApiException;
import org.jboss.intersmash.testsuite.junit5.categories.KubernetesTest;
import org.jboss.intersmash.testsuite.k8s.NamespaceCreationCapable;
import org.jboss.intersmash.testsuite.provision.operator.HyperfoilOperatorProvisionerTests;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.hyperfoil.v1alpha2.Hyperfoil;
import io.hyperfoil.v1alpha2.HyperfoilBuilder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@KubernetesTest
public class HyperfoilKubernetesOperatorProvisionerTest implements NamespaceCreationCapable {
	private static final HyperfoilKubernetesOperatorProvisioner PROVISIONER = initializeProvisioner();

	private static class MyKubernetesHyperfoilOperatorApplication
			implements HyperfoilOperatorApplication, KubernetesApplication {
		@Override
		public Hyperfoil getHyperfoil() {
			return new HyperfoilBuilder(
					getName(),
					// see https://github.com/Hyperfoil/hyperfoil-operator/issues/18, "latest" (default) would fail.
					"0.24.2").build();
		}

		@Override
		public String getName() {
			return HyperfoilOperatorProvisionerTests.APPLICATION_SERVICE_NAME;
		}
	}

	private static HyperfoilKubernetesOperatorProvisioner initializeProvisioner() {
		return new HyperfoilKubernetesOperatorProvisioner(new MyKubernetesHyperfoilOperatorApplication());
	}

	@BeforeAll
	public static void createOperatorGroup() throws IOException {
		PROVISIONER.configure();
		IntersmashExtension.operatorCleanup(true, false);
		// create operator group - this should be done by InteropExtension
		Kuberneteses.adminBinary().execute("apply", "-f",
				new OperatorGroup(KubernetesConfig.namespace()).save().getAbsolutePath());
		// clean any leftovers
		PROVISIONER.unsubscribe();
	}

	@AfterAll
	public static void removeOperatorGroup() {
		Kuberneteses.adminBinary().execute("delete", "operatorgroup", "--all");
		// there might be leftovers in case of failures
		Kuberneteses.admin().pods().withLabel("role", "controller").delete();
		Kuberneteses.admin().pods().withLabel("role", "agent").delete();
		PROVISIONER.dismiss();
	}

	/**
	 * Test deployment of HyperFoil via its operator (includes subscription logic)
	 */
	@Test
	@Order(1)
	public void deploy() {
		HyperfoilOperatorProvisionerTests.verifyDeploy(PROVISIONER);
	}

	/**
	 * Test running a Benchmark on Hyperfoil
	 */
	@Test
	@Order(2)
	public void benchmark() throws ApiException, InterruptedException, IOException {
		HyperfoilOperatorProvisionerTests.verifyBenchmark(PROVISIONER);
	}

	/**
	 * Test undeployment of HyperFoil Operator and resources, including subscription removal
	 */
	@Test
	@Order(3)
	public void undeploy() {
		HyperfoilOperatorProvisionerTests.verifyUndeploy(PROVISIONER);
	}
}
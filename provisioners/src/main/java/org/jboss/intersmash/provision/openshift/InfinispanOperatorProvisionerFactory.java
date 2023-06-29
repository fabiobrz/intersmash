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

<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/provision/openshift/InfinispanOperatorProvisionerFactory.java
import org.jboss.intersmash.application.Application;
import org.jboss.intersmash.application.openshift.InfinispanOperatorApplication;
import org.jboss.intersmash.provision.ProvisionerFactory;
=======
import org.jboss.intersmash.tools.application.Application;
import org.jboss.intersmash.tools.application.operator.InfinispanOperatorApplication;
import org.jboss.intersmash.tools.provision.ProvisionerFactory;
>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/provision/openshift/InfinispanOperatorProvisionerFactory.java

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfinispanOperatorProvisionerFactory implements ProvisionerFactory<InfinispanOpenShiftOperatorProvisioner> {

	@Override
	public InfinispanOpenShiftOperatorProvisioner getProvisioner(Application application) {
		if (InfinispanOperatorApplication.class.isAssignableFrom(application.getClass()))
			return new InfinispanOpenShiftOperatorProvisioner((InfinispanOperatorApplication) application);
		return null;
	}
}

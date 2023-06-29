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
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/application/openshift/WildflyOperatorApplication.java
package org.jboss.intersmash.application.openshift;

import org.jboss.intersmash.provision.openshift.WildflyOperatorProvisioner;
========
package org.jboss.intersmash.tools.application.operator;

>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/application/operator/WildflyOperatorApplication.java
import org.wildfly.v1alpha1.WildFlyServer;

/**
 * End user Application interface which presents WILDFLY operator application on OpenShift Container Platform.
 *
 * The application will be deployed by:
 * <ul>
 *     <li>{@link org.jboss.intersmash.tools.provision.operator.WildflyOperatorProvisioner}</li>
 * </ul>
 */
public interface WildflyOperatorApplication extends OperatorApplication {
	String NAME = "wildfly-operator";

	WildFlyServer getWildflyServer();
}

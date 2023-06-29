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
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/application/openshift/ActiveMQOperatorApplication.java
package org.jboss.intersmash.application.openshift;

import java.util.List;

import org.jboss.intersmash.provision.openshift.ActiveMQOperatorProvisioner;

========
package org.jboss.intersmash.tools.application.operator;

import java.util.List;

>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/application/operator/ActiveMQOperatorApplication.java
import io.amq.broker.v1beta1.ActiveMQArtemis;
import io.amq.broker.v1beta1.ActiveMQArtemisAddress;

/**
 * End user Application interface which presents ActiveMQ operator application on OpenShift Container Platform.
 *
 * The application will be deployed by:
 * <ul>
 *     <li>{@link org.jboss.intersmash.tools.provision.operator.ActiveMQOperatorProvisioner}</li>
 * </ul>
 */
public interface ActiveMQOperatorApplication extends OperatorApplication {

	ActiveMQArtemis getActiveMQArtemis();

	List<ActiveMQArtemisAddress> getActiveMQArtemisAddresses();

	//	ActiveMQArtemisScaledowns getActiveMQArtemisScaledowns(); // TODO add on demand
}

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
<<<<<<<< HEAD:provisioners/src/main/java/org/jboss/intersmash/application/openshift/InfinispanOperatorApplication.java
package org.jboss.intersmash.application.openshift;

import java.util.List;

import org.infinispan.v1.Infinispan;
import org.infinispan.v2alpha1.Cache;
import org.jboss.intersmash.provision.openshift.InfinispanOperatorProvisioner;
========
package org.jboss.intersmash.tools.application.operator;

import java.util.List;

import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.cache.Cache;
import org.jboss.intersmash.tools.provision.openshift.operator.infinispan.infinispan.Infinispan;
>>>>>>>> a372bbb ([k8s-support] - Complete draft of k8s provisioning tooling, with Hyperfoil test enabled. Missing parts: docs (limitations and operators based + prerequisited), CI):tools/intersmash-tools-provisioners/src/main/java/org/jboss/intersmash/tools/application/operator/InfinispanOperatorApplication.java

/**
 * End user Application interface which presents Infinispan operator application on OpenShift Container Platform.
 * Only relevant model APIs are currently exposed, more would be added on demand (e.g.: backups, restores, etc.)
 *
 * The application will be deployed by:
 * <ul>
 *     <li>{@link org.jboss.intersmash.tools.provision.operator.InfinispanOperatorProvisioner}</li>
 * </ul>
 */
public interface InfinispanOperatorApplication extends OperatorApplication {

	Infinispan getInfinispan();

	List<Cache> getCaches();
}

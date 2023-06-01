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
package org.jboss.intersmash.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.intersmash.tools.junit5.IntersmashExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Identifies test classes which lifecycle will be managed by {@link IntersmashExtension}.
 * Contains elements that describe the services composing the interoperability scenario
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(IntersmashExtension.class)
public @interface Intersmash {
	/**
	 * Services composing the interoperability scenario
	 * @return A list of {@link Service} instances that describe the interoperability scenario
	 */
	Service[] value();

	/**
	 * The target environment where the interoperability scenario will be deployed
	 * @return {@link Target} value identifying the interoperability scenario target environment
	 */
	Target target() default Target.OpenShift;

	enum Target {
		/**
		 * Provision on OpenShift
		 */
		OpenShift,
		/**
		 * Provision on vanilla Kubernetes
		 */
		Kubernetes
	}
}

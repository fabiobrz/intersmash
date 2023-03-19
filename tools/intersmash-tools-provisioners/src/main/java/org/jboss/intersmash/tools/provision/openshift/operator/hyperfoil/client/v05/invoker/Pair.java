/*
 * Hyperfoil Controller API
 * Hyperfoil Controller API
 *
 * The version of the OpenAPI document: 0.5
 * Contact: rvansa@redhat.com
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package org.jboss.intersmash.tools.provision.openshift.operator.hyperfoil.client.v05.invoker;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-07-18T14:39:47.341166292+02:00[Europe/Rome]")
public class Pair {
	private String name = "";
	private String value = "";

	public Pair(String name, String value) {
		setName(name);
		setValue(value);
	}

	private void setName(String name) {
		if (!isValidString(name)) {
			return;
		}

		this.name = name;
	}

	private void setValue(String value) {
		if (!isValidString(value)) {
			return;
		}

		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	private boolean isValidString(String arg) {
		if (arg == null) {
			return false;
		}

		return true;
	}
}
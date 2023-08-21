/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2023, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jaxws;

import java.net.URL;
import javax.xml.namespace.QName;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.assertj.core.api.Assertions;
import org.jboss.intersmash.tools.annotations.Intersmash;
import org.jboss.intersmash.tools.annotations.Service;
import org.jboss.intersmash.tools.annotations.ServiceUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.jboss.jaxws.Endpoint;

import cz.xtf.core.http.Https;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Intersmash({
		@Service(SoapWildflyBootableOpenShiftJarApplication.class)
})
public class SoapWildflyBootableOpenShiftJarTest {
	@ServiceUrl(SoapWildflyBootableOpenShiftJarApplication.class)
	private String appOpenShiftUrl;

	private final String CONTEXT_ROOT = "/ROOT-jaxws";

	@Test
	public void testNameChange() throws Exception{
		appOpenShiftUrl = "http://localhost:8080";	// rls test
		URL baseURL = new URL(appOpenShiftUrl + CONTEXT_ROOT + "/EndpointServiceSERVICE");
		QName serviceName = new QName("http://org.jboss.ws/cxf/container", "EndpointServiceSERVICE");
		URL wsdlURL = new URL(baseURL + "?wsdl");
		jakarta.xml.ws.Service service = jakarta.xml.ws.Service.create(wsdlURL, serviceName);
		Endpoint proxy = service.getPort(Endpoint.class);
		String  greeting = proxy.ping();
		Assertions.assertThat(greeting).contains("pong");
	}
}
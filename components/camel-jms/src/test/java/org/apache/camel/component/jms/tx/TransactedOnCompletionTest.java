/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jms.tx;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class TransactedOnCompletionTest extends CamelTestSupport {

    @Produce
    protected ProducerTemplate template;

    @Test
    void testOnCompletion() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:onCompletion").expectedBodiesReceived("onCompletion");

        template.sendBody("direct:start", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        ActiveMQConnectionFactory connectionFactory
                = new ActiveMQConnectionFactory("vm://broker?broker.persistent=false&broker.useJmx=false");
        CamelContext camelContext = super.createCamelContext();
        JmsComponent component = new JmsComponent();
        component.setConnectionFactory(connectionFactory);
        camelContext.addComponent("jms", component);
        return camelContext;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .onCompletion()
                        .setBody(simple("onCompletion"))
                        .to("mock:onCompletion")
                        .end()
                        .to("jms:queue:test.queue?transacted=true");

                from("jms:queue:test.queue?transacted=true")
                        .to("mock:result");
            }
        };
    }
}

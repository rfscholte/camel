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
package org.apache.camel.component.netty.http;

import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;

import static org.apache.camel.test.junit5.TestSupport.assertIsInstanceOf;
import static org.apache.camel.test.junit5.TestSupport.isPlatform;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class NettyHttpSuspendResume503Test extends BaseNettyTest {

    private String serverUri = "netty-http:http://localhost:" + getPort() + "/cool?disconnect=true";

    @Test
    public void testNettySuspendResume() throws Exception {
        // these tests does not run well on Windows
        assumeFalse(isPlatform("windows"));

        context.getShutdownStrategy().setTimeout(50);

        String reply = template.requestBody(serverUri, "World", String.class);
        assertEquals("Bye World", reply);

        // now suspend netty
        NettyHttpConsumer consumer = (NettyHttpConsumer) context.getRoute("foo").getConsumer();
        assertNotNull(consumer);

        // suspend
        consumer.suspend();

        try {
            template.requestBody(serverUri, "Moon", String.class);
            fail("Should throw exception");
        } catch (Exception e) {
            NettyHttpOperationFailedException cause = assertIsInstanceOf(NettyHttpOperationFailedException.class, e.getCause());
            assertEquals(503, cause.getStatusCode());
        }

        // resume
        consumer.resume();

        Thread.sleep(2000);

        // and send request which should be processed
        reply = template.requestBody(serverUri, "Moon", String.class);
        assertEquals("Bye Moon", reply);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(serverUri).routeId("foo")
                        .transform(body().prepend("Bye "));
            }
        };
    }

}

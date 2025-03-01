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
package org.apache.camel.component.aws2.eks;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.component.extension.ComponentVerifierExtension;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EKS2ComponentVerifierExtensionTest extends CamelTestSupport {

    // *************************************************
    // Tests (parameters)
    // *************************************************
    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }

    @Test
    public void testParameters() {
        Component component = context().getComponent("aws2-eks");

        ComponentVerifierExtension verifier
                = component.getExtension(ComponentVerifierExtension.class).orElseThrow(IllegalStateException::new);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("secretKey", "l");
        parameters.put("accessKey", "k");
        parameters.put("region", "l");
        parameters.put("label", "test");
        parameters.put("operation", EKS2Operations.listClusters);

        ComponentVerifierExtension.Result result = verifier.verify(ComponentVerifierExtension.Scope.PARAMETERS, parameters);

        assertEquals(ComponentVerifierExtension.Result.Status.OK, result.getStatus());
    }

    @Test
    public void testConnectivity() {
        Component component = context().getComponent("aws2-eks");
        ComponentVerifierExtension verifier
                = component.getExtension(ComponentVerifierExtension.class).orElseThrow(IllegalStateException::new);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("secretKey", "l");
        parameters.put("accessKey", "k");
        parameters.put("region", "US_EAST_1");
        parameters.put("label", "test");
        parameters.put("operation", EKS2Operations.listClusters);

        ComponentVerifierExtension.Result result = verifier.verify(ComponentVerifierExtension.Scope.CONNECTIVITY, parameters);

        assertEquals(ComponentVerifierExtension.Result.Status.ERROR, result.getStatus());
    }

    @Test
    public void testConnectivityAndRegion() {
        Component component = context().getComponent("aws2-eks");
        ComponentVerifierExtension verifier
                = component.getExtension(ComponentVerifierExtension.class).orElseThrow(IllegalStateException::new);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("secretKey", "l");
        parameters.put("accessKey", "k");
        parameters.put("region", "l");
        parameters.put("label", "test");
        parameters.put("operation", EKS2Operations.listClusters);

        ComponentVerifierExtension.Result result = verifier.verify(ComponentVerifierExtension.Scope.CONNECTIVITY, parameters);

        assertEquals(ComponentVerifierExtension.Result.Status.ERROR, result.getStatus());
    }

}

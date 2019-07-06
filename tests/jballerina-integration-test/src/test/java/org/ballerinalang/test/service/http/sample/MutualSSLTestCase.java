/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.test.service.http.sample;

import org.ballerinalang.test.context.BMainInstance;
import org.ballerinalang.test.context.LogLeecher;
import org.ballerinalang.test.service.http.HttpBaseTest;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Testing Mutual SSL.
 */
@Test(groups = "http-test")
public class MutualSSLTestCase extends HttpBaseTest {

    private BMainInstance ballerinaClient;

    @Test(description = "Test mutual ssl")
    public void testMutualSSL() throws Exception {
        String serverMessage = "successful";
        String serverResponse = "hello world";

        LogLeecher serverLeecher = new LogLeecher(serverMessage);
        serverInstance.addLogLeecher(serverLeecher);

        String balFile = new File("src" + File.separator + "test" + File.separator + "resources"
                + File.separator + "mutualSSL" + File.separator + "mutual_ssl_client.bal").getAbsolutePath();

        ballerinaClient = new BMainInstance(balServer);
        LogLeecher clientLeecher = new LogLeecher(serverResponse);
        ballerinaClient.runMain(balFile, new LogLeecher[]{clientLeecher});
        serverLeecher.waitForText(20000);
        clientLeecher.waitForText(20000);
    }
}

// Copyright (c) 2021 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/http as http2;
import project.http as http;

http:Client httpEndpoint = check new ("https://api.chucknorris.io/");

service /hello on new http2:Listener(9090) {
    resource function get sayHello(string name) returns string|error {
        do {
            io:println("Iterating a string array :");
            string[] fruits = ["apple", "banana", "cherry"];

            string r = "";
            foreach var v in fruits {
                json getResponse = check httpEndpoint->get("/jokes/search?query={" + v + "}");
                r += getResponse.toJsonString();
            }

            foreach var item in 0 ..< 3 {
                json clientResponse = check httpEndpoint->post("/", req);
            }

            foreach var item in 0 ... 3 {
                json clientResponse = check httpEndpoint->post("/", req);
            }

            return r;
        } on fail var e {
            io:println(e);

        }
    }
}

/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.semtype.definition;

import io.ballerina.semtype.Atom;
import io.ballerina.semtype.ComplexSemType;
import io.ballerina.semtype.Definition;
import io.ballerina.semtype.Env;
import io.ballerina.semtype.RecAtom;
import io.ballerina.semtype.SemType;

import java.util.List;

/**
 * Represent mapping type desc.
 *
 * @since 2.0.0
 */
public class MappingDefinition implements Definition {

    private RecAtom roRec = null;
    private RecAtom rwRec = null;
    private SemType semType = null;

    @Override
    public SemType getSemType(Env env) {
        SemType s = this.semType;
        if (s == null) {
            RecAtom ro = env.recMappingAtom();
            RecAtom rw = env.recMappingAtom();
            this.roRec = ro;
            this.rwRec = rw;
            return createSemType(env, ro, rw);
        } else {
            return s;
        }
    }

    public ComplexSemType define(Env env, List<Field> fields, SemType rest) {

        throw new IllegalStateException();
    }

    private SemType createSemType(Env env, Atom ro, Atom rw) {
        throw new AssertionError();
    }
}

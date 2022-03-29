/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.langlib.string;

import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BString;

import static io.ballerina.runtime.api.constants.RuntimeConstants.STRING_LANG_LIB;
import static io.ballerina.runtime.internal.util.exceptions.BallerinaErrorReasons.LENGTH_SHOULD_BE_LESS_THAN_2147483647;
import static io.ballerina.runtime.internal.util.exceptions.BallerinaErrorReasons.getModulePrefixedReason;

/**
 * Extern function ballerina.model.strings:PadZero.
 *
 * @since 2201.1.0
 */

public class PadZero {

    public static BString padZero(BString str, long len, BString padChar) {
        int strLength = str.length();
        if (len <= strLength) {
            return str;
        }

        if (len > Integer.MAX_VALUE) {
            throw ErrorCreator.createError(getModulePrefixedReason(STRING_LANG_LIB,
                    LENGTH_SHOULD_BE_LESS_THAN_2147483647));
        }

        int targetLen = (int) len;
        String pad = padChar.toString().repeat(targetLen - strLength);
        StringBuilder result = new StringBuilder();
        if (str.toString().charAt(0) == '+' || str.toString().charAt(0) == '-') {
            result.append(str.toString().substring(0, 1)).append(pad).append(str.toString().substring(1));
        } else {
            result.append(pad).append(str);
        }
        return StringUtils.fromString(result.toString());
    }
}

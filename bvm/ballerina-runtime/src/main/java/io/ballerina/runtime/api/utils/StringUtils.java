/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.runtime.api.utils;

import io.ballerina.runtime.api.TypeTags;
import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BLink;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.runtime.internal.BalStringUtils;
import io.ballerina.runtime.internal.JsonGenerator;
import io.ballerina.runtime.internal.TypeChecker;
import io.ballerina.runtime.internal.regexp.RegExpFactory;
import io.ballerina.runtime.internal.util.exceptions.BLangExceptionHelper;
import io.ballerina.runtime.internal.util.exceptions.BallerinaException;
import io.ballerina.runtime.internal.util.exceptions.RuntimeErrors;
import io.ballerina.runtime.internal.values.ArrayValue;
import io.ballerina.runtime.internal.values.ArrayValueImpl;
import io.ballerina.runtime.internal.values.BmpStringValue;
import io.ballerina.runtime.internal.values.DecimalValue;
import io.ballerina.runtime.internal.values.MapValueImpl;
import io.ballerina.runtime.internal.values.NonBmpStringValue;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.ballerina.runtime.api.constants.RuntimeConstants.STRING_LANG_LIB;
import static io.ballerina.runtime.internal.util.StringUtils.getExpressionStringVal;
import static io.ballerina.runtime.internal.util.StringUtils.getStringVal;
import static io.ballerina.runtime.internal.util.exceptions.BallerinaErrorReasons.INDEX_OUT_OF_RANGE_ERROR_IDENTIFIER;
import static io.ballerina.runtime.internal.util.exceptions.BallerinaErrorReasons.getModulePrefixedReason;

/**
 * Common utility methods used for String manipulation.
 * 
 * @since 0.95.3
 */
public class StringUtils {

    /**
     * Convert input stream to String.
     *
     * @param in Input stream to be converted to string
     * @return Converted string
     */
    public static BString getStringFromInputStream(InputStream in) {
        String result;
        try (BufferedInputStream bis = new BufferedInputStream(in);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int data;
            while ((data = bis.read()) != -1) {
                bos.write(data);
            }
            result = bos.toString();
        } catch (IOException ioe) {
            throw new BallerinaException("Error occurred when reading input stream", ioe);
        }
        return StringUtils.fromString(result);
    }

    public static BString getStringFromInputStream(InputStream inputStream, String charset) {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new InputStreamReader(inputStream, Charset.forName(charset))) {
            int character;
            while ((character = reader.read()) != -1) {
                textBuilder.append((char) character);
            }
        } catch (IOException e) {
            throw new BallerinaException("Error occurred when reading input stream with the charset" + charset, e);
        }
        return StringUtils.fromString(textBuilder.toString());
    }

    public static BString getStringAt(BString s, long index) {
        if (index < 0 || index >= s.length()) {
            throw ErrorCreator.createError(getModulePrefixedReason(STRING_LANG_LIB,
                    INDEX_OUT_OF_RANGE_ERROR_IDENTIFIER),
                    BLangExceptionHelper.getErrorDetails(RuntimeErrors.STRING_INDEX_OUT_OF_RANGE, index, s.length()));
        }

        return StringUtils.fromString(String.valueOf(Character.toChars(s.getCodePoint((int) index))));
    }

    public static BString fromString(String s) {
        if (s == null) {
            return null;
        }
        List<Integer> highSurrogates = null;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (highSurrogates == null) {
                    highSurrogates = new ArrayList<>();
                }
                highSurrogates.add(i - highSurrogates.size());
            }
        }
        if (highSurrogates == null) {
            return new BmpStringValue(s);
        }

        int[] highSurrogatesArr = new int[highSurrogates.size()];

        for (int i = 0; i < highSurrogates.size(); i++) {
            Integer highSurrogate = highSurrogates.get(i);
            highSurrogatesArr[i] = highSurrogate;
        }
        return new NonBmpStringValue(s, highSurrogatesArr);
    }

    public static BArray fromStringArray(String[] s) {
        BString[] bStringArray = new BString[s.length];
        for (int i = 0; i < s.length; i++) {
            bStringArray[i] = StringUtils.fromString(s[i]);
        }
        return new ArrayValueImpl(bStringArray, false);
    }

    public static BArray fromStringSet(Set<String> set) {
        BString[] bStringArray = new BString[set.size()];
        int i = 0;
        for (String s : set) {
            bStringArray[i] = StringUtils.fromString(s);
            i++;
        }
        return new ArrayValueImpl(bStringArray, false);
    }

    /**
     * Returns the human-readable string value of Ballerina values.
     *
     * @param value The value on which the function is invoked
     * @return      String value of the provided value
     */
    public static String getStringValue(Object value) {
        return getStringVal(value, null);
    }

    // TODO: remove this with https://github.com/ballerina-platform/ballerina-lang/issues/40175
    /**
     * Returns the human-readable string value of Ballerina values.
     *
     * @param value     The value on which the function is invoked
     * @param parent    The link to the parent node
     * @return          String value of the value
     * @deprecated      use {@link #getStringValue(Object)} instead.
     */
    @Deprecated
    public static String getStringValue(Object value, BLink parent) {
        return getStringVal(value, parent);
    }

    /**
     * Returns the string value of Ballerina values in expression style.
     *
     * @param value The value on which the function is invoked
     * @return String value of the value in expression style
     */
    public static String getExpressionStringValue(Object value) {
        return getExpressionStringVal(value, null);
    }

    // TODO: remove this with https://github.com/ballerina-platform/ballerina-lang/issues/40175
    /**
     * Returns the string value of Ballerina values in expression style.
     *
     * @param value The value on which the function is invoked
     * @param parent The link to the parent node
     * @return String value of the value in expression style
     * @deprecated      use {@link #getExpressionStringValue(Object)} instead.
     */
    @Deprecated
    public static String getExpressionStringValue(Object value, BLink parent) {
        return getExpressionStringVal(value, parent);
    }

    /**
     * Returns the Ballerina value represented by Ballerina expression syntax.
     *
     * @param value The value on which the function is invoked
     * @return Ballerina value represented by Ballerina expression syntax
     * @throws BError for any parsing error
     */
    public static Object parseExpressionStringValue(String value, BLink parent) throws BallerinaException {
        String exprValue = value.trim();
        int endIndex = exprValue.length() - 1;
        if (exprValue.equals("()")) {
            return null;
        }
        if (exprValue.startsWith("\"") && exprValue.endsWith("\"")) {
            return StringUtils.fromString(exprValue.substring(1, endIndex));
        }
        if (exprValue.matches("[+-]?[0-9][0-9]*")) {
            return Long.parseLong(exprValue);
        }
        if (exprValue.equals("float:Infinity") || exprValue.equals("float:NaN")) {
            return Double.parseDouble(exprValue.substring(6));
        }
        if (exprValue.matches("[+-]?[0-9]+([.][0-9]+)?([Ee][+-]?[0-9]+)?")) {
            return Double.parseDouble(exprValue);
        }
        if (exprValue.matches("[+-]?[0-9]+(.[0-9]+)?([Ee][+-]?[0-9]+)?[d]")) {
            return new DecimalValue(exprValue.substring(0, endIndex));
        }
        if (exprValue.equals("true") || exprValue.equals("false")) {
            return Boolean.parseBoolean(exprValue);
        }
        if (exprValue.startsWith("[") && exprValue.endsWith("]")) {
            return BalStringUtils.parseArrayExpressionStringValue(exprValue, parent);
        }
        if (exprValue.startsWith("{") && exprValue.endsWith("}")) {
            return BalStringUtils.parseMapExpressionStringValue(exprValue, parent);
        }
        if (exprValue.startsWith("table key")) {
            return BalStringUtils.parseTableExpressionStringValue(exprValue, parent);
        }
        if (exprValue.startsWith("xml")) {
            String xml = exprValue.substring(exprValue.indexOf('`') + 1,
                    exprValue.lastIndexOf('`')).trim();
            return BalStringUtils.parseXmlExpressionStringValue(xml);
        }
        if (exprValue.startsWith("re")) {
            String regexp = exprValue.substring(exprValue.indexOf('`') + 1,
                    exprValue.lastIndexOf('`')).trim();
            return RegExpFactory.parse(regexp);
        }
        if (exprValue.startsWith("...")) {
            return BalStringUtils.parseCycleDetectedExpressionStringValue(exprValue, parent);
        }
        throw new BallerinaException("invalid expression style string value");
    }

    /**
     * Returns the json string value of Ballerina values.
     *
     * @param value The value on which the function is invoked
     * @return Json String value of the value
     */
    public static String getJsonString(Object value) {
        Object jsonValue = JsonUtils.convertToJson(value);

        Type type = TypeUtils.getReferredType(TypeChecker.getType(jsonValue));
        switch (type.getTag()) {
            case TypeTags.NULL_TAG:
                return "null";
            case TypeTags.STRING_TAG:
                return stringToJson((BString) jsonValue);
            case TypeTags.MAP_TAG:
                MapValueImpl mapValue = (MapValueImpl) jsonValue;
                return mapValue.getJSONString();
            case TypeTags.ARRAY_TAG:
                ArrayValue arrayValue = (ArrayValue) jsonValue;
                return arrayValue.getJSONString();
            default:
                return String.valueOf(jsonValue);
        }
    }

    private static String stringToJson(BString value) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        JsonGenerator gen = new JsonGenerator(byteOut);
        try {
            gen.writeString(value.getValue());
            gen.flush();
        } catch (IOException e) {
            throw new BallerinaException("Error in converting string value to a json string: " + e.getMessage(), e);
        }
        return byteOut.toString();
    }

    private StringUtils() {
    }
}

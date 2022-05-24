/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.compiler.syntax.tree;

/**
 * Define various kinds of syntax tree nodes, tokens and minutiae.
 *
 * @since 2.0.0
 */
public enum SyntaxKind {

    // Keywords

    PUBLIC_KEYWORD(50, "public"),
    PRIVATE_KEYWORD(51, "private"),
    REMOTE_KEYWORD(52, "remote"),
    ABSTRACT_KEYWORD(53, "abstract"),
    CLIENT_KEYWORD(54, "client"),
    IMPORT_KEYWORD(100, "import"),
    FUNCTION_KEYWORD(101, "function"),
    CONST_KEYWORD(102, "const"),
    LISTENER_KEYWORD(103, "listener"),
    SERVICE_KEYWORD(104, "service"),
    XMLNS_KEYWORD(105, "xmlns"),
    ANNOTATION_KEYWORD(106, "annotation"),
    TYPE_KEYWORD(107, "type"),
    RECORD_KEYWORD(108, "record"),
    OBJECT_KEYWORD(109, "object"),
    VERSION_KEYWORD(110, "version"),
    AS_KEYWORD(111, "as"),
    ON_KEYWORD(112, "on"),
    RESOURCE_KEYWORD(113, "resource"),
    FINAL_KEYWORD(114, "final"),
    SOURCE_KEYWORD(115, "source"),
    WORKER_KEYWORD(117, "worker"),
    PARAMETER_KEYWORD(118, "parameter"),
    FIELD_KEYWORD(119, "field"),
    ISOLATED_KEYWORD(120, "isolated"),

    RETURNS_KEYWORD(200, "returns"),
    RETURN_KEYWORD(201, "return"),
    EXTERNAL_KEYWORD(202, "external"),
    TRUE_KEYWORD(203, "true"),
    FALSE_KEYWORD(204, "false"),
    IF_KEYWORD(205, "if"),
    ELSE_KEYWORD(206, "else"),
    WHILE_KEYWORD(207, "while"),
    CHECK_KEYWORD(208, "check"),
    CHECKPANIC_KEYWORD(209, "checkpanic"),
    PANIC_KEYWORD(210, "panic"),
    CONTINUE_KEYWORD(211, "continue"),
    BREAK_KEYWORD(212, "break"),
    TYPEOF_KEYWORD(213, "typeof"),
    IS_KEYWORD(214, "is"),
    NULL_KEYWORD(215, "null"),
    LOCK_KEYWORD(216, "lock"),
    FORK_KEYWORD(217, "fork"),
    TRAP_KEYWORD(218, "trap"),
    IN_KEYWORD(219, "in"),
    FOREACH_KEYWORD(220, "foreach"),
    TABLE_KEYWORD(221, "table"),
    KEY_KEYWORD(222, "key"),
    LET_KEYWORD(223, "let"),
    NEW_KEYWORD(224, "new"),
    FROM_KEYWORD(225, "from"),
    WHERE_KEYWORD(226, "where"),
    SELECT_KEYWORD(227, "select"),
    START_KEYWORD(228, "start"),
    FLUSH_KEYWORD(229, "flush"),
    CONFIGURABLE_KEYWORD(230, "configurable"),
    WAIT_KEYWORD(231, "wait"),
    DO_KEYWORD(232, "do"),
    TRANSACTION_KEYWORD(233, "transaction"),
    TRANSACTIONAL_KEYWORD(234, "transactional"),
    COMMIT_KEYWORD(235, "commit"),
    ROLLBACK_KEYWORD(236, "rollback"),
    RETRY_KEYWORD(237, "retry"),
    ENUM_KEYWORD(238, "enum"),
    BASE16_KEYWORD(239, "base16"),
    BASE64_KEYWORD(240, "base64"),
    MATCH_KEYWORD(241, "match"),
    CONFLICT_KEYWORD(242, "conflict"),
    LIMIT_KEYWORD(243, "limit"),
    JOIN_KEYWORD(244, "join"),
    OUTER_KEYWORD(245, "outer"),
    EQUALS_KEYWORD(246, "equals"),
    CLASS_KEYWORD(247, "class"),
    ORDER_KEYWORD(248, "order"),
    BY_KEYWORD(249, "by"),
    ASCENDING_KEYWORD(250, "ascending"),
    DESCENDING_KEYWORD(251, "descending"),
    UNDERSCORE_KEYWORD(252, "_"),
    NOT_IS_KEYWORD(253, "!is"),

    // Type keywords
    INT_KEYWORD(300, "int"),
    BYTE_KEYWORD(301, "byte"),
    FLOAT_KEYWORD(302, "float"),
    DECIMAL_KEYWORD(303, "decimal"),
    STRING_KEYWORD(304, "string"),
    BOOLEAN_KEYWORD(305, "boolean"),
    XML_KEYWORD(306, "xml"),
    JSON_KEYWORD(307, "json"),
    HANDLE_KEYWORD(308, "handle"),
    ANY_KEYWORD(309, "any"),
    ANYDATA_KEYWORD(310, "anydata"),
    NEVER_KEYWORD(311, "never"),
    VAR_KEYWORD(312, "var"),
    MAP_KEYWORD(313, "map"),
    FUTURE_KEYWORD(314, "future"),
    TYPEDESC_KEYWORD(315, "typedesc"),
    ERROR_KEYWORD(316, "error"),
    STREAM_KEYWORD(317, "stream"),
    READONLY_KEYWORD(318, "readonly"),
    DISTINCT_KEYWORD(319, "distinct"),
    FAIL_KEYWORD(320, "fail"),

    // Separators
    OPEN_BRACE_TOKEN(500, "{"), // Any kind above this is considered as a keyword
    CLOSE_BRACE_TOKEN(501, "}"),
    OPEN_PAREN_TOKEN(502, "("),
    CLOSE_PAREN_TOKEN(503, ")"),
    OPEN_BRACKET_TOKEN(504, "["),
    CLOSE_BRACKET_TOKEN(505, "]"),
    SEMICOLON_TOKEN(506, ";"),
    DOT_TOKEN(507, "."),
    COLON_TOKEN(508, ":"),
    COMMA_TOKEN(509, ","),
    ELLIPSIS_TOKEN(510, "..."),
    OPEN_BRACE_PIPE_TOKEN(511, "{|"),
    CLOSE_BRACE_PIPE_TOKEN(512, "|}"),
    AT_TOKEN(513, "@"),
    HASH_TOKEN(514, "#"),
    BACKTICK_TOKEN(515, "`"),
    DOUBLE_QUOTE_TOKEN(516, "\""),
    SINGLE_QUOTE_TOKEN(517, "'"),
    DOUBLE_BACKTICK_TOKEN(518, "``"),
    TRIPLE_BACKTICK_TOKEN(519, "```"),

    // Operators
    EQUAL_TOKEN(550, "="),
    DOUBLE_EQUAL_TOKEN(551, "=="),
    TRIPPLE_EQUAL_TOKEN(552, "==="),
    PLUS_TOKEN(553, "+"),
    MINUS_TOKEN(554, "-"),
    SLASH_TOKEN(555, "/"),
    PERCENT_TOKEN(556, "%"),
    ASTERISK_TOKEN(557, "*"),
    LT_TOKEN(558, "<"),
    LT_EQUAL_TOKEN(559, "<="),
    GT_TOKEN(560, ">"),
    RIGHT_DOUBLE_ARROW_TOKEN(561, "=>"),
    QUESTION_MARK_TOKEN(562, "?"),
    PIPE_TOKEN(563, "|"),
    GT_EQUAL_TOKEN(564, ">="),
    EXCLAMATION_MARK_TOKEN(565, "!"),
    NOT_EQUAL_TOKEN(566, "!="),
    NOT_DOUBLE_EQUAL_TOKEN(567, "!=="),
    BITWISE_AND_TOKEN(568, "&"),
    BITWISE_XOR_TOKEN(569, "^"),
    LOGICAL_AND_TOKEN(570, "&&"),
    LOGICAL_OR_TOKEN(571, "||"),
    NEGATION_TOKEN(572, "~"),
    RIGHT_ARROW_TOKEN(573, "->"),
    INTERPOLATION_START_TOKEN(574, "${"),
    XML_PI_START_TOKEN(575, "<?"),
    XML_PI_END_TOKEN(576, "?>"),
    XML_COMMENT_START_TOKEN(577, "<!--"),
    XML_COMMENT_END_TOKEN(578, "-->"),
    SYNC_SEND_TOKEN(579, "->>"),
    LEFT_ARROW_TOKEN(580, "<-"),
    DOUBLE_DOT_LT_TOKEN(580, "..<"),
    DOUBLE_LT_TOKEN(581, "<<"),
    ANNOT_CHAINING_TOKEN(582, ".@"),
    OPTIONAL_CHAINING_TOKEN(583, "?."),
    ELVIS_TOKEN(584, "?:"),
    DOT_LT_TOKEN(585, ".<"),
    SLASH_LT_TOKEN(586, "/<"),
    DOUBLE_SLASH_DOUBLE_ASTERISK_LT_TOKEN(587, "/**/<"),
    SLASH_ASTERISK_TOKEN(588, "/*"),
    DOUBLE_GT_TOKEN(589, ">>"),
    TRIPPLE_GT_TOKEN(590, ">>>"),
    XML_CDATA_START_TOKEN(591, "<![CDATA["),
    XML_CDATA_END_TOKEN(592, "]]>"),
    RESOURCE_METHOD_CALL_TOKEN(593, "->/"),

    // Documentation reference types
    TYPE_DOC_REFERENCE_TOKEN(900, "type"),
    SERVICE_DOC_REFERENCE_TOKEN(901, "service"),
    VARIABLE_DOC_REFERENCE_TOKEN(902, "variable"),
    VAR_DOC_REFERENCE_TOKEN(903, "var"),
    ANNOTATION_DOC_REFERENCE_TOKEN(904, "annotation"),
    MODULE_DOC_REFERENCE_TOKEN(905, "module"),
    FUNCTION_DOC_REFERENCE_TOKEN(906, "function"),
    PARAMETER_DOC_REFERENCE_TOKEN(907, "parameter"),
    CONST_DOC_REFERENCE_TOKEN(908, "const"),

    // Literal tokens
    IDENTIFIER_TOKEN(1000),
    STRING_LITERAL_TOKEN(1001),
    DECIMAL_INTEGER_LITERAL_TOKEN(1002),
    HEX_INTEGER_LITERAL_TOKEN(1003),
    DECIMAL_FLOATING_POINT_LITERAL_TOKEN(1004),
    HEX_FLOATING_POINT_LITERAL_TOKEN(1005),
    XML_TEXT_CONTENT(1006),
    TEMPLATE_STRING(1007),

    // Documentation
    DOCUMENTATION_DESCRIPTION(1100),
    PARAMETER_NAME(1101),
    CODE_CONTENT(1102),
    DEPRECATION_LITERAL(1103),
    DOCUMENTATION_STRING(1104),

    // Other
    INVALID_TOKEN(1191),

    //-----------------------------------------------non-terminal-kinds-----------------------------------------------

    // Minutiae kinds
    WHITESPACE_MINUTIAE(1500),
    END_OF_LINE_MINUTIAE(1501),
    COMMENT_MINUTIAE(1502),
    INVALID_NODE_MINUTIAE(1503),

    // Invalid nodes
    INVALID_TOKEN_MINUTIAE_NODE(1601),

    // module-level declarations
    IMPORT_DECLARATION(2000),
    FUNCTION_DEFINITION(2001),
    TYPE_DEFINITION(2002),
    SERVICE_DECLARATION(2003),
    MODULE_VAR_DECL(2004),
    LISTENER_DECLARATION(2005),
    CONST_DECLARATION(2006),
    ANNOTATION_DECLARATION(2007),
    MODULE_XML_NAMESPACE_DECLARATION(2008),
    ENUM_DECLARATION(2009),
    CLASS_DEFINITION(2010),

    // Statements
    BLOCK_STATEMENT(1200),
    LOCAL_VAR_DECL(1201),
    ASSIGNMENT_STATEMENT(1202),
    IF_ELSE_STATEMENT(1203),
    ELSE_BLOCK(1204),
    WHILE_STATEMENT(1205),
    CALL_STATEMENT(1206),
    PANIC_STATEMENT(1207),
    RETURN_STATEMENT(1208),
    CONTINUE_STATEMENT(1209),
    BREAK_STATEMENT(1210),
    COMPOUND_ASSIGNMENT_STATEMENT(1211),
    LOCAL_TYPE_DEFINITION_STATEMENT(1212),
    ACTION_STATEMENT(1213),
    LOCK_STATEMENT(1214),
    NAMED_WORKER_DECLARATION(1215),
    FORK_STATEMENT(1216),
    FOREACH_STATEMENT(1217),
    TRANSACTION_STATEMENT(1218),
    ROLLBACK_STATEMENT(1219),
    RETRY_STATEMENT(1220),
    XML_NAMESPACE_DECLARATION(1221),
    MATCH_STATEMENT(1222),
    INVALID_EXPRESSION_STATEMENT(1223),
    DO_STATEMENT(1224),
    FAIL_STATEMENT(1225),

    // Expressions
    BINARY_EXPRESSION(1300),
    BRACED_EXPRESSION(1301),
    FUNCTION_CALL(1302),
    QUALIFIED_NAME_REFERENCE(1303),
    INDEXED_EXPRESSION(1304),
    FIELD_ACCESS(1305),
    METHOD_CALL(1306),
    CHECK_EXPRESSION(1307),
    MAPPING_CONSTRUCTOR(1308),
    TYPEOF_EXPRESSION(1309),
    UNARY_EXPRESSION(1310),
    TYPE_TEST_EXPRESSION(1311),
    SIMPLE_NAME_REFERENCE(1313),
    TRAP_EXPRESSION(1314),
    LIST_CONSTRUCTOR(1315),
    TYPE_CAST_EXPRESSION(1316),
    TABLE_CONSTRUCTOR(1317),
    LET_EXPRESSION(1318),
    XML_TEMPLATE_EXPRESSION(1319),
    RAW_TEMPLATE_EXPRESSION(1320),
    STRING_TEMPLATE_EXPRESSION(1321),
    IMPLICIT_NEW_EXPRESSION(1322),
    EXPLICIT_NEW_EXPRESSION(1323),
    PARENTHESIZED_ARG_LIST(1324),
    EXPLICIT_ANONYMOUS_FUNCTION_EXPRESSION(1325),
    IMPLICIT_ANONYMOUS_FUNCTION_EXPRESSION(1326),
    QUERY_EXPRESSION(1327),
    ANNOT_ACCESS(1328),
    OPTIONAL_FIELD_ACCESS(1329),
    CONDITIONAL_EXPRESSION(1330),
    TRANSACTIONAL_EXPRESSION(1331),
    OBJECT_CONSTRUCTOR(1332),
    XML_FILTER_EXPRESSION(1333),
    XML_STEP_EXPRESSION(1334),
    XML_NAME_PATTERN_CHAIN(1335),
    XML_ATOMIC_NAME_PATTERN(1336),
    STRING_LITERAL(1337),
    NUMERIC_LITERAL(1338),
    BOOLEAN_LITERAL(1339),
    NIL_LITERAL(1340),
    NULL_LITERAL(1341),
    BYTE_ARRAY_LITERAL(1342),
    ASTERISK_LITERAL(1343),
    REQUIRED_EXPRESSION(1344),
    ERROR_CONSTRUCTOR(1345),

    // Type descriptors
    TYPE_DESC(2000),
    RECORD_TYPE_DESC(2001),
    OBJECT_TYPE_DESC(2002),
    NIL_TYPE_DESC(2003),
    OPTIONAL_TYPE_DESC(2004),
    ARRAY_TYPE_DESC(2005),
    INT_TYPE_DESC(2006),
    BYTE_TYPE_DESC(2007),
    FLOAT_TYPE_DESC(2008),
    DECIMAL_TYPE_DESC(2009),
    STRING_TYPE_DESC(2010),
    BOOLEAN_TYPE_DESC(2011),
    XML_TYPE_DESC(2012),
    JSON_TYPE_DESC(2013),
    HANDLE_TYPE_DESC(2014),
    ANY_TYPE_DESC(2015),
    ANYDATA_TYPE_DESC(2016),
    NEVER_TYPE_DESC(2017),
    VAR_TYPE_DESC(2018),
    SERVICE_TYPE_DESC(2019),
    MAP_TYPE_DESC(2020),
    UNION_TYPE_DESC(2021),
    ERROR_TYPE_DESC(2022),
    STREAM_TYPE_DESC(2023),
    TABLE_TYPE_DESC(2024),
    FUNCTION_TYPE_DESC(2025),
    TUPLE_TYPE_DESC(2026),
    PARENTHESISED_TYPE_DESC(2027),
    READONLY_TYPE_DESC(2028),
    DISTINCT_TYPE_DESC(2029),
    INTERSECTION_TYPE_DESC(2030),
    SINGLETON_TYPE_DESC(2031),
    TYPE_REFERENCE_TYPE_DESC(2032),
    TYPEDESC_TYPE_DESC(2033),
    FUTURE_TYPE_DESC(2034),


    // Actions
    REMOTE_METHOD_CALL_ACTION(2500),
    BRACED_ACTION(2501),
    CHECK_ACTION(2502),
    START_ACTION(2503),
    TRAP_ACTION(2504),
    FLUSH_ACTION(2505),
    ASYNC_SEND_ACTION(2506),
    SYNC_SEND_ACTION(2507),
    RECEIVE_ACTION(2508),
    WAIT_ACTION(2509),
    QUERY_ACTION(2510),
    COMMIT_ACTION(2511),
    RESOURCE_METHOD_CALL_ACTION(2512),

    // Other
    RETURN_TYPE_DESCRIPTOR(3000),
    REQUIRED_PARAM(3001),
    DEFAULTABLE_PARAM(3002),
    REST_PARAM(3003),
    EXTERNAL_FUNCTION_BODY(3004),
    RECORD_FIELD(3005),
    RECORD_FIELD_WITH_DEFAULT_VALUE(3006),
    TYPE_REFERENCE(3007),
    RECORD_REST_TYPE(3008),
    POSITIONAL_ARG(3009),
    NAMED_ARG(3010),
    REST_ARG(3011),
    OBJECT_FIELD(3012),
    IMPORT_ORG_NAME(3013),
    MODULE_NAME(3014),
    SUB_MODULE_NAME(3015),
    IMPORT_VERSION(3016),
    ORDER_BY_CLAUSE(3017),
    IMPORT_PREFIX(3018),
    SPECIFIC_FIELD(3019),
    COMPUTED_NAME_FIELD(3020),
    SPREAD_FIELD(3021),
    ORDER_KEY(3022),
    RESOURCE_ACCESSOR_DEFINITION(3023),
    ANNOTATION(3024),
    METADATA(3025),
    ARRAY_DIMENSION(3026),
    ANNOTATION_ATTACH_POINT(3028),
    FUNCTION_BODY_BLOCK(3029),
    NAMED_WORKER_DECLARATOR(3030),
    EXPRESSION_FUNCTION_BODY(3031),
    TYPE_CAST_PARAM(3032),
    KEY_SPECIFIER(3033),
    EXPLICIT_TYPE_PARAMS(3034),
    LET_VAR_DECL(3035),
    STREAM_TYPE_PARAMS(3036),
    FUNCTION_SIGNATURE(3037),
    INFER_PARAM_LIST(3038),
    TYPE_PARAMETER(3039),
    KEY_TYPE_CONSTRAINT(3040),
    QUERY_CONSTRUCT_TYPE(3041),
    FROM_CLAUSE(3042),
    WHERE_CLAUSE(3043),
    LET_CLAUSE(3044),
    QUERY_PIPELINE(3045),
    SELECT_CLAUSE(3046),
    METHOD_DECLARATION(3047),
    TYPED_BINDING_PATTERN(3048),
    BINDING_PATTERN(3049),
    CAPTURE_BINDING_PATTERN(3050),
    REST_BINDING_PATTERN(3051),
    LIST_BINDING_PATTERN(3052),
    RECEIVE_FIELDS(3053),
    REST_TYPE(3054),
    WAIT_FIELDS_LIST(3055),
    WAIT_FIELD(3056),
    ENUM_MEMBER(3057),
    BRACKETED_LIST(3058),
    LIST_BP_OR_LIST_CONSTRUCTOR(3059),
    MAPPING_BINDING_PATTERN(3060),
    FIELD_BINDING_PATTERN(3061),
    MAPPING_BP_OR_MAPPING_CONSTRUCTOR(3062),
    WILDCARD_BINDING_PATTERN(3063),
    MATCH_CLAUSE(3064),
    MATCH_GUARD(3065),
    OBJECT_METHOD_DEFINITION(3066),
    ON_CONFLICT_CLAUSE(3067),
    LIMIT_CLAUSE(3068),
    JOIN_CLAUSE(3069),
    ON_CLAUSE(3070),
    LIST_MATCH_PATTERN(3071),
    REST_MATCH_PATTERN(3072),
    MAPPING_MATCH_PATTERN(3073),
    FIELD_MATCH_PATTERN(3074),
    ERROR_MATCH_PATTERN(3075),
    NAMED_ARG_MATCH_PATTERN(3076),
    ERROR_BINDING_PATTERN(3077),
    NAMED_ARG_BINDING_PATTERN(3078),
    TUPLE_TYPE_DESC_OR_LIST_CONST(3079),
    ON_FAIL_CLAUSE(3080),
    RESOURCE_ACCESSOR_DECLARATION(3081),
    RESOURCE_PATH_SEGMENT_PARAM(3082),
    RESOURCE_PATH_REST_PARAM(3083),
    INCLUDED_RECORD_PARAM(3084),
    ARRAY_TYPE_DESC_OR_MEMBER_ACCESS(3085),
    INFERRED_TYPEDESC_DEFAULT(3086),
    SPREAD_MEMBER(3087),
    COMPUTED_RESOURCE_ACCESS_SEGMENT(3088),
    RESOURCE_ACCESS_REST_SEGMENT(3089),

    // XML
    XML_ELEMENT(4000),
    XML_EMPTY_ELEMENT(4001),
    XML_TEXT(4002),
    XML_COMMENT(4003),
    XML_PI(4004),
    XML_ELEMENT_START_TAG(4005),
    XML_ELEMENT_END_TAG(4006),
    XML_SIMPLE_NAME(4007),
    XML_QUALIFIED_NAME(4008),
    XML_ATTRIBUTE(4009),
    XML_ATTRIBUTE_VALUE(4010),
    INTERPOLATION(4011),
    XML_CDATA(4012),

    // Documentation
    MARKDOWN_DOCUMENTATION(4500),
    MARKDOWN_DOCUMENTATION_LINE(4501),
    MARKDOWN_REFERENCE_DOCUMENTATION_LINE(4502),
    MARKDOWN_PARAMETER_DOCUMENTATION_LINE(4503),
    MARKDOWN_RETURN_PARAMETER_DOCUMENTATION_LINE(4504),
    MARKDOWN_DEPRECATION_DOCUMENTATION_LINE(4505),
    MARKDOWN_CODE_LINE(4506),
    BALLERINA_NAME_REFERENCE(4507),
    MARKDOWN_CODE_BLOCK(4508),
    INLINE_CODE_REFERENCE(4509),

    INVALID(4),
    MODULE_PART(3),
    EOF_TOKEN(2),
    LIST(1),
    NONE(0);

    final int tag;
    final String strValue;

    SyntaxKind(int tag, String strValue) {
        this.tag = tag;
        this.strValue = strValue;
    }

    SyntaxKind(int tag) {
        this.tag = tag;
        this.strValue = "";
    }

    public String stringValue() {
        return strValue;
    }
}

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
package io.ballerinalang.compiler.syntax.tree;

import io.ballerinalang.compiler.internal.parser.tree.STNode;

import java.util.Objects;

/**
 * This is a generated syntax tree node.
 *
 * @since 2.0.0
 */
public class ServiceBodyNode extends NonTerminalNode {

    public ServiceBodyNode(STNode internalNode, int position, NonTerminalNode parent) {
        super(internalNode, position, parent);
    }

    public Token openBraceToken() {
        return childInBucket(0);
    }

    public NodeList<Node> members() {
        return new NodeList<>(childInBucket(1));
    }

    public Token closeBraceToken() {
        return childInBucket(2);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(NodeTransformer<T> visitor) {
        return visitor.transform(this);
    }

    @Override
    protected String[] childNames() {
        return new String[]{
                "openBraceToken",
                "members",
                "closeBraceToken"};
    }

    public ServiceBodyNode modify(
            Token openBraceToken,
            NodeList<Node> members,
            Token closeBraceToken) {
        if (checkForReferenceEquality(
                openBraceToken,
                members.underlyingListNode(),
                closeBraceToken)) {
            return this;
        }

        return NodeFactory.createServiceBodyNode(
                openBraceToken,
                members,
                closeBraceToken);
    }

    public ServiceBodyNodeModifier modify() {
        return new ServiceBodyNodeModifier(this);
    }

    /**
     * This is a generated tree node modifier utility.
     *
     * @since 2.0.0
     */
    public static class ServiceBodyNodeModifier {
        private final ServiceBodyNode oldNode;
        private Token openBraceToken;
        private NodeList<Node> members;
        private Token closeBraceToken;

        public ServiceBodyNodeModifier(ServiceBodyNode oldNode) {
            this.oldNode = oldNode;
            this.openBraceToken = oldNode.openBraceToken();
            this.members = oldNode.members();
            this.closeBraceToken = oldNode.closeBraceToken();
        }

        public ServiceBodyNodeModifier withOpenBraceToken(
                Token openBraceToken) {
            Objects.requireNonNull(openBraceToken, "openBraceToken must not be null");
            this.openBraceToken = openBraceToken;
            return this;
        }

        public ServiceBodyNodeModifier withMembers(
                NodeList<Node> members) {
            Objects.requireNonNull(members, "members must not be null");
            this.members = members;
            return this;
        }

        public ServiceBodyNodeModifier withCloseBraceToken(
                Token closeBraceToken) {
            Objects.requireNonNull(closeBraceToken, "closeBraceToken must not be null");
            this.closeBraceToken = closeBraceToken;
            return this;
        }

        public ServiceBodyNode apply() {
            return oldNode.modify(
                    openBraceToken,
                    members,
                    closeBraceToken);
        }
    }
}

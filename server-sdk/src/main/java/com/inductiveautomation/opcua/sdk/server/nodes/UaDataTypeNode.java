/*
 * Copyright 2014 Inductive Automation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inductiveautomation.opcua.sdk.server.nodes;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.inductiveautomation.opcua.sdk.core.AttributeIds;
import com.inductiveautomation.opcua.sdk.core.Reference;
import com.inductiveautomation.opcua.sdk.core.nodes.DataTypeNode;
import com.inductiveautomation.opcua.stack.core.Identifiers;
import com.inductiveautomation.opcua.stack.core.types.builtin.LocalizedText;
import com.inductiveautomation.opcua.stack.core.types.builtin.NodeId;
import com.inductiveautomation.opcua.stack.core.types.builtin.QualifiedName;
import com.inductiveautomation.opcua.stack.core.types.builtin.unsigned.UInteger;
import com.inductiveautomation.opcua.stack.core.types.enumerated.NodeClass;

public class UaDataTypeNode extends UaNode implements DataTypeNode {

    private final ListMultimap<NodeId, Reference> referenceMap =
            Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    private final AtomicBoolean isAbstract;

    public UaDataTypeNode(NodeId nodeId,
                          NodeClass nodeClass,
                          QualifiedName browseName,
                          LocalizedText displayName,
                          Optional<LocalizedText> description,
                          Optional<UInteger> writeMask,
                          Optional<UInteger> userWriteMask,
                          boolean isAbstract,
                          List<Reference> references) {

        super(nodeId, nodeClass, browseName, displayName, description, writeMask, userWriteMask);

        Preconditions.checkArgument(nodeClass == NodeClass.DataType);

        this.isAbstract = new AtomicBoolean(isAbstract);

        references.stream().forEach(reference -> {
            referenceMap.put(reference.getReferenceTypeId(), reference);
        });
    }

    public void addReference(Reference reference) {
        referenceMap.put(reference.getReferenceTypeId(), reference);
    }

    @Override
    public List<Reference> getReferences() {
        synchronized (referenceMap) {
            return ImmutableList.copyOf(referenceMap.values());
        }
    }

    public List<Reference> getHasPropertyReferences() {
        synchronized (referenceMap) {
            return ImmutableList.copyOf(referenceMap.get(Identifiers.HasProperty));
        }
    }

    public List<Reference> getHasSubtypeReferences() {
        synchronized (referenceMap) {
            return ImmutableList.copyOf(referenceMap.get(Identifiers.HasSubtype));
        }
    }

    public List<Reference> getHasEncodingReferences() {
        synchronized (referenceMap) {
            return ImmutableList.copyOf(referenceMap.get(Identifiers.HasEncoding));
        }
    }

    public boolean removeReference(Reference reference) {
        return referenceMap.remove(reference.getReferenceTypeId(), reference);
    }

    @Override
    public Boolean getIsAbstract() {
        return isAbstract.get();
    }

    @Override
    public synchronized void setIsAbstract(boolean isAbstract) {
        this.isAbstract.set(isAbstract);

        fireAttributeChanged(AttributeIds.IsAbstract, isAbstract);
    }

}

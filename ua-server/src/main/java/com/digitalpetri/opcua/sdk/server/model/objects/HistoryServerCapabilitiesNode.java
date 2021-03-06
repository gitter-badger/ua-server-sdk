/*
 * digitalpetri OPC-UA SDK
 *
 * Copyright (C) 2015 Kevin Herron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.digitalpetri.opcua.sdk.server.model.objects;

import java.util.Optional;

import com.digitalpetri.opcua.sdk.core.model.objects.FolderType;
import com.digitalpetri.opcua.sdk.core.model.objects.HistoryServerCapabilitiesType;
import com.digitalpetri.opcua.sdk.core.nodes.ObjectNode;
import com.digitalpetri.opcua.sdk.server.api.UaNamespace;
import com.digitalpetri.opcua.sdk.server.util.UaObjectType;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;
import com.digitalpetri.opcua.stack.core.types.builtin.LocalizedText;
import com.digitalpetri.opcua.stack.core.types.builtin.NodeId;
import com.digitalpetri.opcua.stack.core.types.builtin.QualifiedName;
import com.digitalpetri.opcua.stack.core.types.builtin.Variant;
import com.digitalpetri.opcua.stack.core.types.builtin.unsigned.UByte;
import com.digitalpetri.opcua.stack.core.types.builtin.unsigned.UInteger;


@UaObjectType(name = "HistoryServerCapabilitiesType")
public class HistoryServerCapabilitiesNode extends BaseObjectNode implements HistoryServerCapabilitiesType {

    public HistoryServerCapabilitiesNode(
            UaNamespace namespace,
            NodeId nodeId,
            QualifiedName browseName,
            LocalizedText displayName,
            Optional<LocalizedText> description,
            Optional<UInteger> writeMask,
            Optional<UInteger> userWriteMask,
            UByte eventNotifier) {

        super(namespace, nodeId, browseName, displayName, description, writeMask, userWriteMask, eventNotifier);
    }

    public Boolean getAccessHistoryDataCapability() {
        Optional<Boolean> accessHistoryDataCapability = getProperty("AccessHistoryDataCapability");

        return accessHistoryDataCapability.orElse(null);
    }

    public Boolean getAccessHistoryEventsCapability() {
        Optional<Boolean> accessHistoryEventsCapability = getProperty("AccessHistoryEventsCapability");

        return accessHistoryEventsCapability.orElse(null);
    }

    public UInteger getMaxReturnDataValues() {
        Optional<UInteger> maxReturnDataValues = getProperty("MaxReturnDataValues");

        return maxReturnDataValues.orElse(null);
    }

    public UInteger getMaxReturnEventValues() {
        Optional<UInteger> maxReturnEventValues = getProperty("MaxReturnEventValues");

        return maxReturnEventValues.orElse(null);
    }

    public Boolean getInsertDataCapability() {
        Optional<Boolean> insertDataCapability = getProperty("InsertDataCapability");

        return insertDataCapability.orElse(null);
    }

    public Boolean getReplaceDataCapability() {
        Optional<Boolean> replaceDataCapability = getProperty("ReplaceDataCapability");

        return replaceDataCapability.orElse(null);
    }

    public Boolean getUpdateDataCapability() {
        Optional<Boolean> updateDataCapability = getProperty("UpdateDataCapability");

        return updateDataCapability.orElse(null);
    }

    public Boolean getDeleteRawCapability() {
        Optional<Boolean> deleteRawCapability = getProperty("DeleteRawCapability");

        return deleteRawCapability.orElse(null);
    }

    public Boolean getDeleteAtTimeCapability() {
        Optional<Boolean> deleteAtTimeCapability = getProperty("DeleteAtTimeCapability");

        return deleteAtTimeCapability.orElse(null);
    }

    public Boolean getInsertEventCapability() {
        Optional<Boolean> insertEventCapability = getProperty("InsertEventCapability");

        return insertEventCapability.orElse(null);
    }

    public Boolean getReplaceEventCapability() {
        Optional<Boolean> replaceEventCapability = getProperty("ReplaceEventCapability");

        return replaceEventCapability.orElse(null);
    }

    public Boolean getUpdateEventCapability() {
        Optional<Boolean> updateEventCapability = getProperty("UpdateEventCapability");

        return updateEventCapability.orElse(null);
    }

    public Boolean getDeleteEventCapability() {
        Optional<Boolean> deleteEventCapability = getProperty("DeleteEventCapability");

        return deleteEventCapability.orElse(null);
    }

    public Boolean getInsertAnnotationCapability() {
        Optional<Boolean> insertAnnotationCapability = getProperty("InsertAnnotationCapability");

        return insertAnnotationCapability.orElse(null);
    }

    public FolderType getAggregateFunctions() {
        Optional<ObjectNode> aggregateFunctions = getObjectComponent("AggregateFunctions");

        return aggregateFunctions.map(node -> (FolderType) node).orElse(null);
    }

    public synchronized void setAccessHistoryDataCapability(Boolean accessHistoryDataCapability) {
        getPropertyNode("AccessHistoryDataCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(accessHistoryDataCapability)));
        });
    }

    public synchronized void setAccessHistoryEventsCapability(Boolean accessHistoryEventsCapability) {
        getPropertyNode("AccessHistoryEventsCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(accessHistoryEventsCapability)));
        });
    }

    public synchronized void setMaxReturnDataValues(UInteger maxReturnDataValues) {
        getPropertyNode("MaxReturnDataValues").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(maxReturnDataValues)));
        });
    }

    public synchronized void setMaxReturnEventValues(UInteger maxReturnEventValues) {
        getPropertyNode("MaxReturnEventValues").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(maxReturnEventValues)));
        });
    }

    public synchronized void setInsertDataCapability(Boolean insertDataCapability) {
        getPropertyNode("InsertDataCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(insertDataCapability)));
        });
    }

    public synchronized void setReplaceDataCapability(Boolean replaceDataCapability) {
        getPropertyNode("ReplaceDataCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(replaceDataCapability)));
        });
    }

    public synchronized void setUpdateDataCapability(Boolean updateDataCapability) {
        getPropertyNode("UpdateDataCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(updateDataCapability)));
        });
    }

    public synchronized void setDeleteRawCapability(Boolean deleteRawCapability) {
        getPropertyNode("DeleteRawCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(deleteRawCapability)));
        });
    }

    public synchronized void setDeleteAtTimeCapability(Boolean deleteAtTimeCapability) {
        getPropertyNode("DeleteAtTimeCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(deleteAtTimeCapability)));
        });
    }

    public synchronized void setInsertEventCapability(Boolean insertEventCapability) {
        getPropertyNode("InsertEventCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(insertEventCapability)));
        });
    }

    public synchronized void setReplaceEventCapability(Boolean replaceEventCapability) {
        getPropertyNode("ReplaceEventCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(replaceEventCapability)));
        });
    }

    public synchronized void setUpdateEventCapability(Boolean updateEventCapability) {
        getPropertyNode("UpdateEventCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(updateEventCapability)));
        });
    }

    public synchronized void setDeleteEventCapability(Boolean deleteEventCapability) {
        getPropertyNode("DeleteEventCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(deleteEventCapability)));
        });
    }

    public synchronized void setInsertAnnotationCapability(Boolean insertAnnotationCapability) {
        getPropertyNode("InsertAnnotationCapability").ifPresent(n -> {
            n.setValue(new DataValue(new Variant(insertAnnotationCapability)));
        });
    }
}

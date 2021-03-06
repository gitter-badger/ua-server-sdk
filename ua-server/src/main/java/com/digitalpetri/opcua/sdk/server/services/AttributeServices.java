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

package com.digitalpetri.opcua.sdk.server.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.server.DiagnosticsContext;
import com.digitalpetri.opcua.sdk.server.OpcUaServer;
import com.digitalpetri.opcua.sdk.server.Session;
import com.digitalpetri.opcua.sdk.server.api.AttributeManager.ReadContext;
import com.digitalpetri.opcua.sdk.server.api.AttributeManager.WriteContext;
import com.digitalpetri.opcua.sdk.server.api.Namespace;
import com.digitalpetri.opcua.sdk.server.util.PendingRead;
import com.digitalpetri.opcua.sdk.server.util.PendingWrite;
import com.digitalpetri.opcua.stack.core.StatusCodes;
import com.digitalpetri.opcua.stack.core.application.services.AttributeServiceSet;
import com.digitalpetri.opcua.stack.core.application.services.ServiceRequest;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;
import com.digitalpetri.opcua.stack.core.types.builtin.DiagnosticInfo;
import com.digitalpetri.opcua.stack.core.types.builtin.StatusCode;
import com.digitalpetri.opcua.stack.core.types.builtin.unsigned.UShort;
import com.digitalpetri.opcua.stack.core.types.structured.ReadRequest;
import com.digitalpetri.opcua.stack.core.types.structured.ReadResponse;
import com.digitalpetri.opcua.stack.core.types.structured.ReadValueId;
import com.digitalpetri.opcua.stack.core.types.structured.ResponseHeader;
import com.digitalpetri.opcua.stack.core.types.structured.WriteRequest;
import com.digitalpetri.opcua.stack.core.types.structured.WriteResponse;
import com.digitalpetri.opcua.stack.core.types.structured.WriteValue;

import static com.digitalpetri.opcua.sdk.server.util.FutureUtils.sequence;
import static com.digitalpetri.opcua.stack.core.util.ConversionUtil.a;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class AttributeServices implements AttributeServiceSet {

    private final ServiceMetric readMetric = new ServiceMetric();
    private final ServiceMetric writeMetric = new ServiceMetric();

    @Override
    public void onRead(ServiceRequest<ReadRequest, ReadResponse> service) {
        readMetric.record(service);

        ReadRequest request = service.getRequest();

        DiagnosticsContext<ReadValueId> diagnosticsContext = new DiagnosticsContext<>();

        OpcUaServer server = service.attr(ServiceAttributes.SERVER_KEY).get();
        Session session = service.attr(ServiceAttributes.SESSION_KEY).get();

        if (request.getNodesToRead().length == 0) {
            service.setServiceFault(StatusCodes.Bad_NothingToDo);
            return;
        }

        if (request.getNodesToRead().length > server.getConfig().getLimits().getMaxNodesPerRead().longValue()) {
            service.setServiceFault(StatusCodes.Bad_TooManyOperations);
            return;
        }

        if (request.getMaxAge() < 0d) {
            service.setServiceFault(StatusCodes.Bad_MaxAgeInvalid);
            return;
        }

        if (request.getTimestampsToReturn() == null) {
            service.setServiceFault(StatusCodes.Bad_TimestampsToReturnInvalid);
            return;
        }

        ReadValueId[] nodesToRead = request.getNodesToRead();
        List<PendingRead> pendingReads = newArrayListWithCapacity(nodesToRead.length);
        List<CompletableFuture<DataValue>> futures = newArrayListWithCapacity(nodesToRead.length);

        for (ReadValueId id : nodesToRead) {
            PendingRead pending = new PendingRead(id);

            pendingReads.add(pending);
            futures.add(pending.getFuture());
        }

		/*
         * Group PendingReads by namespace and call read for each.
		 */

        Map<UShort, List<PendingRead>> byNamespace = pendingReads.stream()
                .collect(groupingBy(pending -> pending.getInput().getNodeId().getNamespaceIndex()));

        byNamespace.keySet().forEach(index -> {
            List<PendingRead> pending = byNamespace.get(index);

            CompletableFuture<List<DataValue>> future = new CompletableFuture<>();

            ReadContext context = new ReadContext(
                    server, session, future, diagnosticsContext);

            server.getExecutorService().execute(() -> {
                Namespace namespace = server.getNamespaceManager().getNamespace(index);

                List<ReadValueId> readValueIds = pending.stream()
                        .map(PendingRead::getInput)
                        .collect(toList());

                namespace.read(
                        context,
                        request.getMaxAge(),
                        request.getTimestampsToReturn(),
                        readValueIds);
            });

            future.thenAccept(values -> {
                for (int i = 0; i < values.size(); i++) {
                    pending.get(i).getFuture().complete(values.get(i));
                }
            });
        });

		/*
         * When all PendingReads have been completed send a ReadResponse with the values.
		 */

        sequence(futures).thenAcceptAsync(values -> {
            ResponseHeader header = service.createResponseHeader();

            DiagnosticInfo[] diagnosticInfos =
                    diagnosticsContext.getDiagnosticInfos(nodesToRead);

            ReadResponse response = new ReadResponse(
                    header, a(values, DataValue.class), diagnosticInfos);

            service.setResponse(response);
        }, server.getExecutorService());
    }

    @Override
    public void onWrite(ServiceRequest<WriteRequest, WriteResponse> service) {
        writeMetric.record(service);

        WriteRequest request = service.getRequest();

        DiagnosticsContext<WriteValue> diagnosticsContext = new DiagnosticsContext<>();

        OpcUaServer server = service.attr(ServiceAttributes.SERVER_KEY).get();
        Session session = service.attr(ServiceAttributes.SESSION_KEY).get();

        if (request.getNodesToWrite().length == 0) {
            service.setServiceFault(StatusCodes.Bad_NothingToDo);
            return;
        }

        if (request.getNodesToWrite().length > server.getConfig().getLimits().getMaxNodesPerWrite().intValue()) {
            service.setServiceFault(StatusCodes.Bad_TooManyOperations);
            return;
        }

        WriteValue[] nodesToWrite = request.getNodesToWrite();
        List<PendingWrite> pendingWrites = newArrayListWithCapacity(nodesToWrite.length);
        List<CompletableFuture<StatusCode>> futures = newArrayListWithCapacity(nodesToWrite.length);

        for (WriteValue value : nodesToWrite) {
            PendingWrite pending = new PendingWrite(value);

            pendingWrites.add(pending);
            futures.add(pending.getFuture());
        }

        Map<UShort, List<PendingWrite>> byNamespace = pendingWrites.stream()
                .collect(groupingBy(pending -> pending.getInput().getNodeId().getNamespaceIndex()));

        byNamespace.keySet().forEach(index -> {
            List<PendingWrite> pending = byNamespace.get(index);

            CompletableFuture<List<StatusCode>> future = new CompletableFuture<>();

            WriteContext context = new WriteContext(
                    server, session, future, diagnosticsContext);

            server.getExecutorService().execute(() -> {
                Namespace namespace = server.getNamespaceManager().getNamespace(index);

                List<WriteValue> writeValues = pending.stream()
                        .map(PendingWrite::getInput)
                        .collect(toList());

                namespace.write(context, writeValues);
            });

            future.thenAccept(statusCodes -> {
                for (int i = 0; i < statusCodes.size(); i++) {
                    pending.get(i).getFuture().complete(statusCodes.get(i));
                }
            });
        });

        sequence(futures).thenAcceptAsync(values -> {
            ResponseHeader header = service.createResponseHeader();

            DiagnosticInfo[] diagnosticInfos =
                    diagnosticsContext.getDiagnosticInfos(nodesToWrite);

            WriteResponse response = new WriteResponse(
                    header, a(values, StatusCode.class), diagnosticInfos);

            service.setResponse(response);
        }, server.getExecutorService());
    }

}

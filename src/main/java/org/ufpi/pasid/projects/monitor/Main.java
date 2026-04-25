package org.ufpi.pasid.projects.monitor;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        ProducerWithReplyTo producer = new ProducerWithReplyTo();

        ExecutionRequest request = buildNewEngineRequest();
        Map<ExecutionRequest, ExecutionResponse> responses = producer.sendRequestAndAwait(request);
        ExecutionResponse response = responses.get(request);

        if (response == null) {
            System.out.println("Nenhuma resposta recebida.");
            return;
        }

        if (response.getError() != null) {
            System.out.println("Erro retornado pela engine:");
            System.out.println(response.getError());
        }

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            System.out.println("Detalhes dos erros:");
            for (ExecutionResponse.ExecutionErrorPayload error : response.getErrors()) {
                System.out.println(error.getCode() + ": " + error.getMessage());
            }
        }

        if (response.getMetricValues() != null) {
            System.out.println("Metricas:");
            response.getMetricValues().forEach((metric, value) ->
                    System.out.println(metric + "=" + value)
            );
        }

        if (response.getTimeSeries() != null && !response.getTimeSeries().isEmpty()) {
            System.out.println("Serie temporal:");
            for (ExecutionResponse.TimeSeriesPoint point : response.getTimeSeries()) {
                System.out.println("t=" + point.getTime() + " -> " + point.getMetricValues());
            }
        }

        if (response.getMeta() != null) {
            System.out.println("Meta:");
            System.out.println(response.getMeta());
        }
    }

    private static ExecutionRequest buildNewEngineRequest() {
        ExecutionRequest request = new ExecutionRequest();
        request.setConfigId("legacy-performance1-file");
        request.setEngine(ExecutionRequest.Engine.NEW_ENGINE);
        request.setModelType(ExecutionRequest.ModelType.SPN);

        // Troque aqui para TRANSIENT_SIMULATION se quiser testar serie temporal.
        request.setExecutionType(ExecutionRequest.ExecutionType.STATIONARY_SIMULATION);

        request.setMetrics(Arrays.asList(
                "USED_PODS",
                "TP",
                "RT",
                "TOTAL_JOBS",
                "PROCESS_JOBS"
        ));

        request.setError(0.01);
        request.setConfidenceLevel(0.95);
        request.setSimulationTime(2000.0);
        request.setWarmupTime(100.0);
        request.setRepetitions(16);
        request.setMinSamples(16);
        request.setMaxSamples(16);
        request.setMaxEvents(200000);
        request.setSeed(42L);
        request.setParallelism(4);
        request.setDebug(false);

        request.setModelJson(JsonParser.parseString(readResource("/models/spn-legacy-performance1.json")));
        return request;
    }

    private static String readResource(String resourcePath) {
        try (InputStream input = Main.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Recurso nao encontrado: " + resourcePath);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException error) {
            throw new IllegalStateException("Falha ao ler recurso: " + resourcePath, error);
        }
    }
}

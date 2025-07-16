package org.ufpi.pasid.projects.monitor;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ProducerWithReplyTo producer = new ProducerWithReplyTo();


        String model = "src/main/resources/testModels/testSPN.xml";
        HashMap<String,Double> variables= new HashMap<>();
        variables.put("def1",1.0);
        variables.put("def2",50.0);
        ExecutionRequest executionRequest= new ExecutionRequest();
        executionRequest.setConfigId("configA");
        executionRequest.setModel(model);
        executionRequest.setVariables(variables);
        executionRequest.setExecutionType(ExecutionRequest.ExecutionType.STATIONARY_SIMULATION);
        executionRequest.setModelType(ExecutionRequest.ModelType.SPN);
        executionRequest.setError(0.1);
        executionRequest.setMaxExecutionTimeInMinutes(0);
        executionRequest.setMetrics(Arrays.asList("Metric0"));

        HashMap<String,Double> variables2= new HashMap<>();
        variables2.put("def1",30.0);
        variables2.put("def2",150.0);
        ExecutionRequest executionRequest2= new ExecutionRequest();
        executionRequest2.setConfigId("configB");
        executionRequest2.setModel(model);
        executionRequest2.setVariables(variables2);
        executionRequest2.setExecutionType(ExecutionRequest.ExecutionType.STATIONARY_SIMULATION);
        executionRequest2.setModelType(ExecutionRequest.ModelType.SPN);
        executionRequest2.setError(0.1);
        executionRequest2.setMaxExecutionTimeInMinutes(0);
        executionRequest2.setMetrics(Arrays.asList("Metric0"));


        List<ExecutionRequest> batch1 = Arrays.asList(executionRequest,executionRequest2);




        Map<ExecutionRequest,ExecutionResponse> responses =    producer.sendRequestsAndAwait(batch1,"fila.fast");
        for (ExecutionRequest executionRequest1: batch1) {
            ExecutionResponse result = responses.get(executionRequest1);
            System.out.println(executionRequest1.getConfigId()+" -- "+responses.get(executionRequest1).getMetricValues().get("Metric0"));
        }

        //assincrono
//        producer.executeBatchAsync(batch1, "batchA", "fila.fast");
//        producer.executeBatchAsync(batch2, "batchB", "fila.slow");

//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//            System.out.println("Status atual dos batches:");
//            producer.batchResults.forEach((id, result) -> {
//                System.out.println("Batch " + id + " - Duração: " + result.durationMs + "ms, Resultados: " + result.responses);
//            });
//        }, 3, 5, TimeUnit.SECONDS);

        System.out.println("finish");


    }
    public List<ExecutionRequest> generateSampleRequests(String baseName, int count) {
        List<ExecutionRequest> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ExecutionRequest request = new ExecutionRequest();
            request.setModel(baseName + i);

            Map<String, Double> variables = new HashMap<>();
            variables.put("tempo", 100.0 + i);
            variables.put("clientes", 40.0 + i);
            variables.put("taxa", 0.80 + i * 0.01);
            request.setVariables(variables);

            request.setExecutionType(ExecutionRequest.ExecutionType.STATIONARY_ANALYSIS);
            request.setModelType(ExecutionRequest.ModelType.SPN);
            request.setMetrics(Arrays.asList("throughput", "availability"));

            list.add(request);
        }
        return list;
    }
}
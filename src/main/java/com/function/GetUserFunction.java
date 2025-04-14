package com.function;

import com.function.GraphQL.GraphQLProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class GetUserFunction {

    @FunctionName("obtenerUsuariosGraphQL")
    public HttpResponseMessage run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS,
            route = "usuarioGraphQL/obtener"
        )
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context
    ) {
        try {

            String body = request.getBody().orElse("");
            context.getLogger().info("🔵 Body recibido: " + body);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);
            String query = jsonNode.get("query").asText(); // <-- Extrae el campo `query`
            context.getLogger().info("🔵 Query extraída: " + query);

            var executionResult = GraphQLProvider.getGraphQL().execute(query);

            if (!executionResult.getErrors().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body(executionResult.getErrors())
                        .build();
            }

            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(executionResult.toSpecification())
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error ejecutando GraphQL para obtener roles: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando la consulta GraphQL: " + e.getMessage())
                    .build();
        }
    }
    
}

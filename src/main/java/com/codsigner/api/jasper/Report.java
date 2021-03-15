package com.codsigner.api.jasper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.codsigner.factory.JasperReportFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/*
 * Root resource (exposed at "myresource" path)
 */
@Path("report")
public class Report {

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response report(@DefaultValue("{}") String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode parsed = mapper.readTree(json);

            if(parsed.get("jrxmlName") == null) {
                return Response.status(400).entity("Template de relatório não informado.").build();
            }

            String jasperName = parsed.get("jrxmlName").toString();
            Map<String, Object> params = mapper.convertValue(parsed.get("params"), new TypeReference<Map<String, Object>>(){});
            ArrayList<HashMap<String, Object>> data = mapper.convertValue(parsed.get("params"), new TypeReference<ArrayList<HashMap<String, Object>>>(){});
            
            byte[] report = JasperReportFactory.print(jasperName, params, data);

            return Response.ok().entity(report).build();
        } catch (Exception e) {            
            e.printStackTrace();
            return Response.serverError().build();
        }        
    }
}
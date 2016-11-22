package tests.resource.printResourceTests;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.mockito.Mock;
import org.testng.annotations.Test;
import sanger.PMBClient;
import sanger.PrintRequest;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;


/**
 * @author hc6
 */
public class WhenPrintingLabels {
    private ClientResponse response;
    @Mock
    private PMBClient mockPMBClient;

    @Test
    public void TestPrintLabels() throws IOException, JSONException {
        final String printerName = "d304bc";
        final String cellLine = "zogh";
        final String barcode = "200";

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("cell_line", cellLine);
        fieldMap.put("barcode", barcode);

        PrintRequest.Label label = new PrintRequest.Label(fieldMap);
        PrintRequest request = new PrintRequest(printerName, Collections.singletonList(label));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);

        response = getResource()
                .path("/print_jobs")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, jsonString);

        assertEquals(response.getStatus(), ClientResponse.Status.OK.getStatusCode());
        verify(mockPMBClient).print(request);
    }

    private WebResource getResource(){
        ClientConfig clientConfig = new DefaultClientConfig();
        Client client;
        client = ApacheHttpClient.create(clientConfig);
        return client.resource("http://localhost:3000/v1");
    }

}

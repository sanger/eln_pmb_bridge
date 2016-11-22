package tests.resource.printResourceTests;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.mockito.Mock;
import sanger.*;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;

/**
 * @author hc6
 */
abstract class GivenPrintRequest  {

    @Mock
    protected PMBClient mockPMBClient;
    protected ClientResponse response;

    protected void sendRequest(PrintRequest request) throws IOException, JSONException {

        PrintConfig.loadConfig();
        PMBClient pmbClient = new PMBClient(PrintConfig.getInstance());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        PrintRequest result = objectMapper.readValue(jsonString, PrintRequest.class);

        Map<PrinterLabelType, Integer> templateIds = new EnumMap<>(PrinterLabelType.class);
        templateIds.put(PrinterLabelType.Plate, 6);
        templateIds.put(PrinterLabelType.Tube, 0);
        templateIds.put(PrinterLabelType.Branded, 0);

        PrintConfig config = new PrintConfig("http://localhost:3000/v1", templateIds);
        mockPMBClient = new PMBClient(config);

        System.out.println(request);
        doReturn(makeJson()).when(mockPMBClient.buildJson(request));

        pmbClient.print(request);
//        when(config.getLocalLocation()).thenReturn("http://localhost:3000/v1");
//        when(pmbClient.buildJson(request)).thenReturn(makeJson());

        response = getResource()
                .path("/print_jobs")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, request);
    }

    protected abstract JSONObject makeJson() throws JSONException;

    private WebResource getResource(){
        ClientConfig clientConfig = new DefaultClientConfig();

        Client client;
        client = ApacheHttpClient.create(clientConfig);

        WebResource resource = client.resource("http://localhost:3000/v1");
        return resource;
    }

}

package sanger;

import org.codehaus.jettison.json.JSONException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author hc6
 */
@Path("http://localhost:3000/v1/print_jobs")
public class PrintResource {

    public PrintResource() {}

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postPrintJob(final PrintRequest request) throws IOException, JSONException {

        PrintConfig.loadConfig();
        PMBClient pmbClient = new PMBClient(PrintConfig.getInstance());
        pmbClient.print(request);

        return Response.ok().build();
    }

}

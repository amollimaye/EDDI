package ai.labs.callback.http;

import ai.labs.callback.dto.ConversationDataRequest;
import ai.labs.callback.dto.ConversationDataResponse;
import ai.labs.memory.IConversationMemory;
import ai.labs.memory.IData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author rpi
 */
public class CallbackHttpClient {

    private final static String AI_LABS_USER_AGENT = "Jetty 9.3/HTTP CLIENT - AI.LABS.CHATBOT";
    private final static String MIME_TYPE = "application/json";
    private final static long CONNECT_TIMEOUT = 2000;


    public final static int ERRORCODE_OK = 0;
    public final static int ERRORCODE_TIMEOUT = -1;
    public final static int ERRORCODE_SERVER_ERROR = -2;
    public final static int ERRORCODE_INTERRUPTED = -3;


    private HttpClient httpClient;
    private long timeoutMs;

    public CallbackHttpClient(long timeoutMs, int maxConnections, int maxRequests, int maxRedirects) throws Exception {
        SslContextFactory sslContextFactory = new SslContextFactory();

        httpClient = new HttpClient(sslContextFactory);
        httpClient.setFollowRedirects(true);
        httpClient.setMaxConnectionsPerDestination(maxConnections);
        httpClient.setConnectTimeout(CONNECT_TIMEOUT);
        httpClient.setMaxRequestsQueuedPerDestination(maxRequests);
        httpClient.setMaxRedirects(maxRedirects);
        this.timeoutMs = timeoutMs;

        httpClient.start();
    }

    public ConversationDataResponse send(String url, ConversationDataRequest request)  {
        ConversationDataResponse response = new ConversationDataResponse();

        Gson gson = new Gson();
        String jsonRequestBody = gson.toJson(request);

        try {
            ContentResponse httpResponse = httpClient.POST(url)
                                                    .agent(AI_LABS_USER_AGENT)
                                                    .timeout(timeoutMs, TimeUnit.MILLISECONDS)
                                                    .content(new StringContentProvider(jsonRequestBody, MIME_TYPE))
                                                    .send();
            Type listType = new TypeToken<ArrayList<IData>>(){}.getType();

            List<IData> conversationMemory = gson.fromJson(httpResponse.getContentAsString(), listType);
            response.setErrorcode(ERRORCODE_OK);
            response.setConversationMemory(conversationMemory);
        } catch (InterruptedException e) {
            response.setErrorcode(ERRORCODE_INTERRUPTED);
            response.setConversationMemory(null);
        } catch (TimeoutException e) {
            response.setErrorcode(ERRORCODE_TIMEOUT);
            response.setConversationMemory(null);
        } catch (ExecutionException e) {
            response.setErrorcode(ERRORCODE_SERVER_ERROR);
            response.setConversationMemory(null);

        }
        return response;
    }
}

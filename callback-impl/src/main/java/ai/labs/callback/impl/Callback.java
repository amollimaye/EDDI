package ai.labs.callback.impl;

import ai.labs.callback.ICallback;
import ai.labs.callback.dto.ConversationDataRequest;
import ai.labs.callback.dto.ConversationDataResponse;
import ai.labs.callback.http.CallbackHttpClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rpi
 */
@Slf4j
public class Callback implements ICallback {

    private CallbackHttpClient callbackHttpClient;

    @Override
    public void init(long timeoutMS, int maxConnections, int maxRequests, int maxRedirects) {
        try {
            callbackHttpClient = new CallbackHttpClient(timeoutMS, maxConnections, maxRequests, maxRedirects);
        } catch (Exception e) {
            log.error("Callback Module disabled!", e);
        }
    }

    @Override
    public ConversationDataResponse doExternalCall(String url, ConversationDataRequest request) {

        return callbackHttpClient.send(url, request);
    }
}

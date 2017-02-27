package ai.labs.callback;

import ai.labs.callback.dto.ConversationDataRequest;
import ai.labs.callback.dto.ConversationDataResponse;

/**
 * @author rpi
 */
public interface ICallback {

    void init(long timeoutMS, int maxConnections, int maxRequests, int maxRedirects);

    public ConversationDataResponse doExternalCall(String url, ConversationDataRequest request);

}

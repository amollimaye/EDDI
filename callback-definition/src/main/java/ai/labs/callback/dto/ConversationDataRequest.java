package ai.labs.callback.dto;

import ai.labs.memory.IConversationMemory;
import ai.labs.memory.IData;

import java.util.List;

/**
 * Created by rpi on 08.02.2017.
 */
public class ConversationDataRequest {

    private IConversationMemory conversationMemory;

    public IConversationMemory getConversationMemory() {
        return conversationMemory;
    }

    public void setConversationMemory(IConversationMemory conversationMemory) {
        this.conversationMemory = conversationMemory;
    }
}

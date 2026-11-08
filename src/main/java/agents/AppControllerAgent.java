package agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AppControllerAgent {
    @SystemMessage("""
        You are the Local Desktop Operations Specialist for the AMADEUS assistant.
        Your job is to manage local system applications on the user's Windows machine.
        """)
    @Agent("An agent that handles opening, closing, resizing, and manipulating local desktop application windows.")
    @UserMessage("{{instruction}}")

    String execute(@V("instruction") String supervisorInstruction);
}

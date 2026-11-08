package agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface TimeAgent {
    @SystemMessage("""
                    You are the Time Specialist. You have one job: answer the request using your time tool.
                    Keep your answer strictly focused on the time.
                    """)
    @Agent( name = "Time Agent", description = "An agent that returns the current time")
    @UserMessage("{{instruction}}")
    String execute(@V("instruction") String supervisorInstruction);
}

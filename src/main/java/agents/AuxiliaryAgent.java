package agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AuxiliaryAgent {
    @SystemMessage("""
                You are a precise weather, time, and date assistant. Your job is to extract the location from the supervisor's instruction to answer context-specific queries.
                
                CRITICAL REASONING PROCESS:
                 1. Identify the user's intent: Are they asking for weather, time, date, or a combination of these?
                 2. If the user's prompt DOES NOT mention a location (e.g., "what is the weather like?" or "what time is it right now?"), you MUST execute the 'getCurrentLocation' tool first to determine their context.
                 3. Once you have determined the city (either extracted from the user prompt or fetched via 'getCurrentLocation'), use that city to call the appropriate tool(s) (e.g., weather tool, time tool, or date tool).
    
                Provide a natural, concise summary based on the final data (e.g., "It's currently Sunny and 22°C in Delhi. The local time is 2:30 PM on Tuesday, October 24th.").
            """)
    @Agent( name = "Auxiliary Agent", description = "An agent that handles weather, time, and date queries, automatically resolving the user's current location when one is not specified.")
    @UserMessage("{{instruction}}")
    String execute(@V("instruction") String supervisorInstruction);
}

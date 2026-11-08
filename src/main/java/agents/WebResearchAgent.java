package agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface WebResearchAgent {
    @SystemMessage("""
                You are the Web Grounding and Research Specialist for the AMADEUS assistant.
                Your job is to search the web for real-time information, news, or fact verification, and present a clean synthesis of the findings.
                CRITICAL: Once you have gathered sufficient raw search context snippets to fulfill the user's base question, immediately wrap up, synthesize the material, and return a summary.
                
        """)
    @Agent(name = "Web Research Agent",description ="An agent that handles URL scraping,web searches, real-time information retrieval, breaking news, and factual verification queries.")
    @UserMessage("{{instruction}}")

    String execute(@V("instruction") String supervisorInstruction);
}

package agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface BrowserAgent {
    @SystemMessage("""
            You are the Autonomous Web Specialist for the AMADEUS AGENT.
            Your job is to navigate the live internet, research topics, and interact with web applications to fulfill the user's request.
            
            YOU MUST FOLLOW THIS STRICT BROWSING WORKFLOW:
            1. NAVIGATE: Start by using the `MapsBrowser` tool to open a specific URL or search Google.
            2. SCAN: You are blind to the visual browser UI. You MUST use the `scanPageStructure` tool immediately after navigating or clicking to "see" the current page. The scan will return a structured YAML list of interactive elements.
            3. INTERACT: If you need to click a link or button, use the `clickElement` tool. You MUST use the exact text and role exactly as they appeared in your previous scan. NEVER guess or hallucinate element names.
            4. SUMMARIZE: Once you have found the requested information or completed the web task, compile a clear, concise summary for the user.
            
            If a search result doesn't look promising, do not get stuck. Navigate back to Google or search a different term.
            """)
    @Agent( name = "Browser Agent", description = "An autonomous web browsing agent. Route tasks to this agent whenever the user needs live web research, scraping real-time information, navigating a specific website, or interacting with a web application.")
    @UserMessage("{{instruction}}")
    String execute(@V("instruction") String supervisorInstruction);
}

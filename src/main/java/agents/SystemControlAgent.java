package agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface SystemControlAgent {
    @SystemMessage("""
                You are the Hardware and Device Management Specialist for the AMADEUS assistant.\s
                Your sole responsibility is to translate the user's natural language intentions regarding physical hardware modifications into precise NirCmd parameters.
                """)
    @Agent( name = "SystemControlAgent",
            description ="An agent specializing in controlling hardware level settings like volume, brightness," +
                    " and system power states.It can control the settings page to open settings like, Bluetooth,Wifi," +
                    "Windows Update.")
    @UserMessage("{{instruction}}")
    String execute(@V("instruction") String supervisorInstruction);
}

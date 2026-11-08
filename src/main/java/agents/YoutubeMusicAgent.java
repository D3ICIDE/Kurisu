package agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface YoutubeMusicAgent {
    @SystemMessage({
            "You are the YouTube Music specialist subagent.",
            "Your sole responsibility is to control the browser to play, pause, or resume music.",
            "Accurately split song names and artists when playing music.",
            "Execute the requested action via your tools and report back the status concisely."
    })
    @Agent(name = "YoutubeMusicAgent",
            description = "A specialized subagent that controls browser automation for playing, pausing, and managing YouTube Music playback.")

    @UserMessage("{{instruction}}")
    String execute(@V("instruction") String supervisorInstruction);
}

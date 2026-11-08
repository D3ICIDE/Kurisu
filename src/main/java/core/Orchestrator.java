package core;
import agents.*;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import tools.*;
import utils.Config;
import utils.ModelFactory;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.Scanner;

public class Orchestrator {
        ChatMemoryProvider memory = memoryId -> MessageWindowChatMemory.withMaxMessages(25);
        ChatModel routingModel = ModelFactory.groq(Config.routingModel);
        ChatModel mainModel = ModelFactory.groq(Config.mainModel);
        ChatModel analysisModel = ModelFactory.gemini(Config.gemmaModel);
        ChatModel lookupModel = ModelFactory.gemini(Config.gemmaLiteModel);

    AppControllerAgent appControllerAgent = AgenticServices.agentBuilder(AppControllerAgent.class)
            .chatModel(mainModel)
            .tools(new AppControlTool())
            .build();

    WebResearchAgent webResearchAgentAgent = AgenticServices.agentBuilder(WebResearchAgent.class)
            .chatModel(analysisModel)
            .tools(new GroundSearchTool())
            //.maxToolCallingRoundTrips(5)
            .build();

    BrowserAgent browserAgent = AgenticServices.agentBuilder(BrowserAgent.class)
            .chatModel(analysisModel)
            .tools(new BrowserControlTool())
            .build();


    YoutubeMusicAgent musicAgent = AgenticServices.agentBuilder(YoutubeMusicAgent.class)
            .chatModel(analysisModel)
            .tools(new YoutubeMusicController())
            .build();



    AuxiliaryAgent auxillaryAgent = AgenticServices.agentBuilder(AuxiliaryAgent.class)
            .chatModel(analysisModel)
            .tools(new FetchWeatherTool(),
                    new FetchCurrentLocation())
            .build();

    SystemControlAgent systemControlAgent = AgenticServices.agentBuilder(SystemControlAgent.class)
            .chatModel(mainModel)
            .tools(new HardwareControlTool())
            .build();

        Supervisor supervisor = AgenticServices.supervisorBuilder(Supervisor.class)
                .chatModel(mainModel)
                .chatMemoryProvider(memory)
                .subAgents(appControllerAgent, browserAgent, systemControlAgent, webResearchAgentAgent,
                         auxillaryAgent, musicAgent)
                .outputKey("response")
                .responseStrategy(SupervisorResponseStrategy.SCORED)
                .build();

        public String run(String userMessage){
            String response = supervisor.invoke("user-1",userMessage);
            System.out.println(response);
            return  response;

    }
    /** Fast track: web search only. */
    public String webSearch(String instruction) {
        return webResearchAgentAgent.execute(instruction);
    }

    /** Slow track: full supervisor workflow. */
    public String runSupervisor(String instruction) {
        return supervisor.invoke("User-1", instruction);
    }

}

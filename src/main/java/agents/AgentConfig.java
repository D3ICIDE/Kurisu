package agents;

import utils.Config;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class AgentConfig {

    //Google Endpoint
    ChatModel Googlemodel = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GEMINI_API_KEY"))
            .modelName("gemma-4-31b-it") // Specify your chosen model
            .build();

    //Groq Endpoint
    ChatModel mainModel = OpenAiChatModel.builder()
            .modelName(Config.mainModel)
            .apiKey(Config.groqApi)
            .baseUrl(Config.API_URL)
            .build();


//Open Routers Endpoint
    ChatModel chatModel = OpenAiChatModel.builder()
            .apiKey(Config.openRouterApi)
            .modelName(Config.nVIDIAModel)
            .baseUrl(Config.OpenRouters)
            .build();

    //Nvidia Endpoint

    ChatModel nvdiaModel = OpenAiChatModel.builder()
            .modelName(Config.nVIDIAModel)
            .apiKey(Config.nvidiaApi)
            .baseUrl(Config.NVIDIA_URL)
            .build();




}

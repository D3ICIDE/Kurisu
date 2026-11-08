package utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.java_websocket.WebSocket;

public class Config {
    private static volatile WebSocket activeWebsocket;
    private static volatile String currentUserMessage;
    public static final String mainModel = "openai/gpt-oss-120b";
    public static final String routingModel = "qwen/qwen3.8-27b";
    public static final String gemmaModel = "gemma-4-31b-it";
    public static final String gemmaLiteModel = "gemma-4-26b-it";
    public static final String analysisModel = "llama-3.3-70b-versatile";
    public static final String backgroundCheckingModel= "openai/gpt-oss-20b";
    public static final String visualModel ="";
    //public static final double temperature =0.5;
    public static final String nVIDIAModel = "nvidia/nemotron-3-ultra-550b-a55b:free";

    public static final String exaUrl = "https://api.exa.ai/search";
   public final static String API_URL = "https://api.groq.com/openai/v1";
    public final static String OpenRouters = "https://openrouter.ai/api/v1";
    public final static String NVIDIA_URL = "https://integrate.api.nvidia.com/v1";
    public static final String TAVILY_API_URL = "https://api.tavily.com";
    public static final String Tavily_Extract_URL = "https://api.tavily.com/extract";
    public static final String Weather_Base_URL = "http://api.weatherapi.com/v1";
    public static final String GEO_API_URL = "http://ip-api.com/json/";

    static Dotenv dotenv = Dotenv.load();
    public static String mistralApi= dotenv.get("mistral_api");
    public static String tokenRouterApi= dotenv.get("token_router_api");
    public final static String nvidiaApi = dotenv.get("nvidia_api");
    public static final String exaApi = dotenv.get("exa_api");
    public final static String groqApi = dotenv.get("groq_api");
    public final static String openRouterApi = dotenv.get("open_router_api");
    public final static String weatherApi = dotenv.get("weather_api");
    public static final String tavilyApi = dotenv.get("tavily_api");
    public static final String geminiApi = dotenv.get("gemini_api");
    public static final String azureSpeechApi = dotenv.get("azure_speech_key");
    public static final String azureSpeechRegion = dotenv.get("azure_speech_region");

    public static void setActiveWebSocket(WebSocket webSocket){
        activeWebsocket = webSocket;

    }
    public static WebSocket getActiveSocket(){
        return activeWebsocket;
    }
    public static void setCurrentUserMessage(String userMessage){
        currentUserMessage = userMessage;
    }
    public static  String getCurrentUserMessage(){
        return  currentUserMessage;
    }



}

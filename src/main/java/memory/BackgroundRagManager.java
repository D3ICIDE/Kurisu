package amadeus.memory;

/*import com.abyss.amadeus.config.Config;
import com.abyss.amadeus.server.payLoadBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackgroundRagManager {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Gson gson = new Gson();

    private final MemoryHistory history;
    private final MemoryConfig memoryConfig;
    private final String groqApiKey;

    public BackgroundRagManager(MemoryHistory history, MemoryConfig memoryConfig, String groqApiKey) {
        this.history = history;
        this.memoryConfig = memoryConfig;
        this.groqApiKey = groqApiKey;
    }

    public void onStart() {
        System.out.println("[MemoryJanitor]: Core service active. Scanning thread active every 5 minutes.");
        scheduler.scheduleAtFixedRate(this::processHistoryQueue, 1, 5, TimeUnit.MINUTES);
    }

    public void onStop() {
        System.out.println("[MemoryJanitor]: Core service stopped.");
        scheduler.shutdown();
    }

    public void processHistoryQueue() {
        try {
            JsonArray recentHistory = history.getHistoryAsJson();
            if (recentHistory.isEmpty()) return;

            String conversationLog = recentHistory.toString();
            String extractedFacts = extractPermanentFacts(conversationLog);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public String extractPermanentFacts(String conversationLog) {
        JsonObject payload = payLoadBuilder.buildPayLoad(Config.backgroundCheckingModel,conversationLog,0.1,sysPrompt,null,
                 JsonArray new arr);

    }
    private final String sysPrompt = """
            You are a precise data-mining thread. Analyze the conversation history block.\s
            
            Extract ONLY permanent user preferences, facts, identity attributes, configurations, or settings.\s
            
            CRITICAL: Do NOT extract volatile hardware tweaks, real-time brightness modifications, or current volume levels.\s
            
            You MUST respond in valid JSON matching this exact schema specification:
            {
              "facts": [
                { "category": "TOPIC_NAME", "statement": "Pure statement containing the descriptive fact" }
              ]
            }
            
            Example Category Topics: USER_NAME, UI_THEME, USER_LOCATION, COMPILER_PREFERENCE.
            
            If absolutely no permanent structural facts exist in the text logs, return an empty array: { "facts": [] }
            
            """;
}*/


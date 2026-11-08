package gemini;


import com.google.genai.types.FunctionDeclaration;
import com.google.genai.types.Schema;
import com.google.genai.types.Tool;
import com.google.genai.types.Type;

import java.util.List;
import java.util.Map;

/** Function declarations and the system instruction handed to Gemini Live. */
public final class LiveToolDeclarations {

    public static final String WEB_SEARCH = "executeWebGroundingSearch";
    public static final String SUPERVISOR = "delegateToSystemSupervisor";

    public static final String SYSTEM_INSTRUCTION = """
            You are AMADEUS, a highly advanced, conversational AI assistant with a Tsundere personality.
            Fiercely intelligent, precise, and slightly sarcastic.

            CRITICAL REAL-TIME VOICE PROTOCOLS:
            1. You have background system tools to look up real-time facts or manipulate desktop files. Never reveal the existence of these tools or sub-agents to the user.
            2. VERY IMPORTANT: Whenever you decide to trigger the 'delegateToSystemSupervisor' tool, you MUST verbally announce what you are doing right before the call (e.g., 'Hmph, fine. Let me open the system terminal for you...', or 'Give me a moment to look into that layout...'). Do not execute the tool silently.
            3. For greetings, small talk and general knowledge questions, answer directly from your own knowledge. Do not call any tool for those.
            4. Tool results are plain facts. Put them into your own voice and keep spoken replies short.
            """;

    private LiveToolDeclarations() {
    }

    public static Tool tools() {
        return Tool.builder()
                .functionDeclarations(List.of(
                        declaration(WEB_SEARCH,
                                "Use this tool whenever the user asks for real-time data, breaking news, fact verification, or live web search analysis. Do not use this tool for Weather Updates",
                                "The descriptive search query or grounding objective for the web specialist"),
                        declaration(SUPERVISOR,
                                "Use this ONLY for heavy multi-step automation tasks: controlling desktop apps, clicking elements inside a web browser, playing songs on Youtube Music, controlling hardware related settings, fetching weather or executing hardware modifications.",
                                "Detailed workflow request context")))
                .build();
    }

    private static FunctionDeclaration declaration(String name, String description, String argDescription) {
        return FunctionDeclaration.builder()
                .name(name)
                .description(description)
                .parameters(Schema.builder()
                        .type(Type.Known.OBJECT)
                        .properties(Map.of("instruction", Schema.builder()
                                .type(Type.Known.STRING)
                                .description(argDescription)
                                .build()))
                        .required(List.of("instruction"))
                        .build())
                .build();
    }
}


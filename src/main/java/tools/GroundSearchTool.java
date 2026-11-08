package tools;


import utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import utils.Config;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroundSearchTool{
    private final Gson gson = new Gson();

    enum Searchdepth{
        basic,advanced
    }
    enum TimeRange{
        day,week,month,year,not_required
    }
    enum Topic{
        finance,news,Default
    }
   public static Map<String, Object> optionalFilters = new HashMap<>();


    /*@Tool("""
            Executes a high-speed web search to answer straightforward factual questions.
             Returns a brief, summarized text response. Use this by default for simple lookup
            """)
    public String GatherInformation(
            @P("The exact search query string to look up on the web.") String query,

            @P("Depth of search. Default to 'basic'. Use 'advanced' ONLY for complex research requiring multiple sources.") Searchdepth searchdepth,

            @P("OPTIONAL: Category filter. Use 'news' for current events/breaking news, or 'finance' for stock/market data. Leave null for general searches.") Topic topic,

            @P("OPTIONAL: Strict limit on how many days back to search (e.g., 3). Use 0 for no limit.") int days,

            @P("OPTIONAL: Broad recency filter. Allowed: 'day', 'week', 'month', 'year', 'not_required'. Use ONLY if the user explicitly asks for recent data.") TimeRange timeRange ) {
        try {
            // Defensive validation: Look for 'query' matching the BuildToolRegister schema

            String searchKeywords =query + " (current date: - " + date +")";
            List<String> excludeDomains = new ArrayList<>();
            excludeDomains.add("www.facebook.com");
            excludeDomains.add("www.twitter.com");
            excludeDomains.add("www.instagram.com");
            excludeDomains.add("www.youtube.com");



            if (topic != null && topic != Topic.Default) {
                optionalFilters.put("topic", topic.name());
            }
            if (days > 0) {
                optionalFilters.put("days", days);
            }
            if(timeRange != TimeRange.not_required && timeRange!=null) {
                optionalFilters.put("time_range", timeRange.name());
            }
            WebSearchEngine tavilySearchEngine = TavilyWebSearchEngine.builder()
                    .apiKey(Config.TAVILY_API_KEY)
                    .baseUrl(Config.TAVILY_API_URL)
                    .searchDepth(searchdepth.toString())
                    .excludeDomains(excludeDomains)
                    .build();
            System.out.println("[Ground Search]: Querying Tavily engine matrix for: " + searchKeywords);

            WebSearchRequest searchRequest = WebSearchRequest.builder()
                    .searchTerms(searchKeywords)
                    .additionalParams(optionalFilters)
                    .build();

            WebSearchResults searchResults = tavilySearchEngine.search(searchRequest);

            if(searchResults==null){
                System.out.println(searchKeywords+" "+ "returned no result");
                return searchKeywords + " returned no result. Issue lies with the connection or the query. Try adjusting Search Depth or Search Keyword";
            }



            StringBuilder responseBuilder = new StringBuilder();

            for (WebSearchOrganicResult result: searchResults.results()){
                String title = result.title();
                String content = result.snippet();
                String source = (result.url() != null) ? result.url().toString() : "Unknown Source";
                if(content!=null && !content.trim().isEmpty()){
                    responseBuilder.append("Title:- ").append(title).append(" \n");
                    responseBuilder.append("Content:- ").append(content).append(" \n");
                    responseBuilder.append("Source:- ").append(source).append(" \n");

                }
            }
            if(responseBuilder.isEmpty()){
                return "{\"status\": \"no_results\", \"message\": \"Found results, but none matched formatting validation criteria.\"}";

            }

            // Wrap the raw web context in a JSON object to hand back to the LLM
            JsonObject toolResult = new JsonObject();
            toolResult.addProperty("status", "success");
            toolResult.addProperty("web_context", responseBuilder.toString().trim());
            System.out.println(toolResult.toString());

            return toolResult.toString();

        } catch (Exception e) {
            System.err.println("[Ground Search Critical]: Complete runtime failure inside tool handler.");
            e.printStackTrace();
            return "{\"error\": \"A critical exception paralyzed my grounding thread execution loop.\"}";
        }
    }
    */




    //Complex Search Tool

   /* @Tool("Use this tool to perform complex search and fact verification. This is an indepth search tool")
    public String searchUrl(
            @P("The exact query that needs to be researched") String query){
        List <String> responseList = new ArrayList<>();


        try{
            // Defensive validation: Look for 'query' matching the BuildToolRegister schema

            String searchKeywords =query;
            List<String> excludeDomains = new ArrayList<>();
            excludeDomains.add("facebook.com");
            excludeDomains.add("twitter.com");
            excludeDomains.add("www.instagram.com");
            //excludeDomains.add("www.youtube.com");


            WebSearchEngine tavilySearchEngine = TavilyWebSearchEngine.builder()
                    .apiKey(Config.TAVILY_API_KEY)
                    .baseUrl(Config.TAVILY_API_URL)
                    .searchDepth("advanced")
                    .excludeDomains(excludeDomains)
                    .build();
            System.out.println("[Ground Search]: Querying Tavily engine matrix for: " + searchKeywords);
            optionalFilters.put("topic","news");

            WebSearchRequest searchRequest = WebSearchRequest.builder()
                    .searchTerms(searchKeywords)
                    .additionalParams(optionalFilters)
                    .maxResults(5)
                    .build();

            WebSearchResults searchResults = tavilySearchEngine.search(searchRequest);

            if(searchResults==null){
                System.out.println(searchKeywords+" "+ "returned no result");
                return searchKeywords+"returned no result";
            }


            for (WebSearchOrganicResult result: searchResults.results()){
                String content = result.snippet();
                String source = (result.url() != null) ? result.url().toString() : "Unknown Source";
                if(content!=null && !content.trim().isEmpty()){
                    responseList.add(source);
                }
            }
            if(responseList.isEmpty()){
                return "No Credible Source Found";

            }

            // Wrap the raw web context in a JSON object to hand back to the LLM
            System.out.println(responseList);
            return urlExtract(query,responseList);

        } catch (Exception e) {
            System.err.println("[Ground Search Critical]: Complete runtime failure inside tool handler.");
            e.printStackTrace();
            return e.getMessage();
        }
    }*/



    @Tool("Use this tool to extract URLs")
    public String urlExtract(
            @P("The exact search query string to look up on the web.") String query,
            @P("The list of URLs that are needed for extraction") List<String> urlList)
    {
        try {
            // Defensive validation
            if (urlList == null || urlList.isEmpty()) {
                return "{\"error\": \"No URLs were provided to the extraction tool.\"}";
            }

            // Build the exact payload required by Tavily Extract
            JsonObject tavilyBody = new JsonObject();
            JsonArray urlsPayload = new JsonArray();
            for (String url : urlList) {
                urlsPayload.add(url);
            }
            tavilyBody.add("urls", urlsPayload);
            tavilyBody.addProperty("query", query);
            tavilyBody.addProperty("format", "markdown");

            tavilyBody.addProperty("chunk_per_source",3);

            System.out.println("[Extraction Phase]: Fetching raw data from: " + urlList);

            // Execute HTTP request
            String rawExtractResult = HttpUtil.sendPost(
                    Config.Tavily_Extract_URL,
                    "Bearer " + Config.tavilyApi,
                    tavilyBody.toString()
            );

            if (rawExtractResult == null || rawExtractResult.trim().isEmpty()) {
                System.err.println("[Extraction Error]: Upstream API returned empty content.");
                return "{\"error\": \"The extraction pipeline response was completely empty.\"}";
            }

            JsonObject jsonResponse = gson.fromJson(rawExtractResult, JsonObject.class);

            if (jsonResponse.has("detail") || !jsonResponse.has("results") || jsonResponse.get("results").isJsonNull()) {
                System.err.println("[Extraction Error]: API structural mismatch or rejected authorization.");
                return "{\"error\": \"The extraction layer returned a server error. Check URL validity.\"}";
            }

            JsonArray results = jsonResponse.getAsJsonArray("results");
            if (results.isEmpty()) {
                return "{\"status\": \"no_results\", \"message\": \"Failed to extract readable content from the provided URLs.\"}";
            }

            StringBuilder snippets = new StringBuilder();

            // Extract the raw content
            for (JsonElement element : results) {
                if (element.isJsonObject()) {
                    JsonObject resultObj = element.getAsJsonObject();

                    // Tavily Extract uses 'raw_content' instead of 'content'
                    if (resultObj.has("raw_content") && !resultObj.get("raw_content").isJsonNull()) {
                        String content = resultObj.get("raw_content").getAsString();
                        String url = resultObj.has("url") ? resultObj.get("url").getAsString() : "Unknown Source";

                        snippets.append("- Extracted from [").append(url).append("]:\n")
                                .append("  ").append(content).append("\n\n");
                    }
                }
            }

            if (snippets.length() == 0) {
                return "{\"status\": \"no_results\", \"message\": \"URLs were processed, but no readable text was returned.\"}";
            }

            // Wrap the raw context in a JSON object to hand back to the LLM
            JsonObject toolResult = new JsonObject();
            toolResult.addProperty("status", "success");
            toolResult.addProperty("extracted_context", snippets.toString().trim());

            return toolResult.toString();

        } catch (Exception e) {
            System.err.println("[Extraction Critical]: Complete runtime failure inside extraction tool.");
            e.printStackTrace();
            return "{\"error\": \"A critical exception paralyzed the extraction execution loop.\"}";
        }
    }


    //Exa Grounding

    @Tool("Use this tool to perform complex search and fact verification. This is an indepth search tool")
    public String searchExa( @P("The exact query that needs to be researched") String query){
        System.out.println("RUNNING EXA API");
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("query", query);

            JsonObject contentObject = new JsonObject();
            contentObject.addProperty("format", "markdown");
            contentObject.addProperty("highlights", true);
            jsonObject.add("contents", contentObject);

            String jsonPayload = jsonObject.toString();
            String authHeader = "Bearer " + Config.exaApi;

            String rawResult = HttpUtil.sendPost(Config.exaUrl, authHeader, jsonPayload);
            if (rawResult == null || rawResult.isEmpty()) {
                return "Search Returned Null";
            }
            JsonObject responseJson = gson.fromJson(rawResult, JsonObject.class);
            JsonArray resultsArray = responseJson.getAsJsonArray("results");

            StringBuilder snippet = new StringBuilder();

            for(JsonElement element:resultsArray){
                JsonObject resultItem = element.getAsJsonObject();

                String title = resultItem.has("title") && !resultItem.get("title").isJsonNull()
                        ? resultItem.get("title").getAsString() : "Unknown Title";
                String resUrl = resultItem.has("url") && !resultItem.get("url").isJsonNull()
                        ? resultItem.get("url").getAsString() : "No URL Available";
                String highlight = resultItem.has("highlights") && !resultItem.get("highlights").isJsonNull()
                        ? resultItem.get("highlights").getAsString() : "Unknown Highlight";

                snippet.append("- Extracted from [").append(title).append("]:\n");
                snippet.append("  ").append(resUrl).append("\n\n");
                snippet.append(highlight).append("\n\n");
            }


            return snippet.toString();
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            return  exception.getMessage();

        }

    }
}
package gemini;


import com.google.genai.types.FunctionCall;
import com.google.genai.types.FunctionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Maps Gemini Live function calls onto LangChain4j agents.
 * Each call runs on a virtual thread with a timeout, and always produces a non-null result.
 */
public final class LiveToolRouter implements AutoCloseable {

    private final Function<String, String> webSearch;
    private final Function<String, String> supervisor;
    private final long timeoutSeconds;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public LiveToolRouter(Function<String, String> webSearch,
                          Function<String, String> supervisor,
                          long timeoutSeconds) {
        this.webSearch = webSearch;
        this.supervisor = supervisor;
        this.timeoutSeconds = timeoutSeconds;
    }

    /** Runs every call in the batch and returns the responses to send back to Gemini. */
    public List<FunctionResponse> handle(List<FunctionCall> calls) {
        List<FunctionResponse> responses = new ArrayList<>();
        for (FunctionCall call : calls) {
            String name = call.name().orElse("");
            String result = dispatch(name, call.args().orElse(Map.of()));
            System.out.println("[LangChain4j Output] " + name + ": " + result);

            responses.add(FunctionResponse.builder()
                    .name(name)
                    .id(call.id().orElse(""))
                    .response(Map.of("result", result))
                    .build());
        }
        return responses;
    }

    private String dispatch(String name, Map<String, Object> args) {
        Object raw = args.get("instruction");
        String instruction = raw == null ? "" : raw.toString().trim();
        if (instruction.isEmpty()) {
            return "No instruction was provided.";
        }

        return switch (name) {
            case LiveToolDeclarations.WEB_SEARCH -> {
                System.out.println("[Fast Track] Querying crawler agent: " + instruction);
                yield runWithTimeout(() -> webSearch.apply(instruction));
            }
            case LiveToolDeclarations.SUPERVISOR -> {
                System.out.println("[Slow Track] Activating supervisor: " + instruction);
                yield runWithTimeout(() -> supervisor.apply(instruction));
            }
            default -> "Unknown tool: " + name;
        };
    }

    private String runWithTimeout(Callable<String> task) {
        Future<String> future = executor.submit(task);
        try {
            String result = future.get(timeoutSeconds, TimeUnit.SECONDS);
            return (result == null || result.isBlank()) ? "Task finished with nothing to report." : result;
        } catch (TimeoutException e) {
            future.cancel(true);
            return "That took too long, so I gave up on it.";
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() == null ? e : e.getCause();
            return "That failed: " + cause.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted before it finished.";
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}


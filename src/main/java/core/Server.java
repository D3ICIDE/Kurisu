package core;
/*import amadeus.memory.Memorize;
import amadeus.memory.MemoryConfig;
import amadeus.memory.MemoryHistory;
import amadeus.memory.Total_Recall;*/
import utils.Config;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server extends WebSocketServer {
    private static final ExecutorService pipelineExecutor = Executors.newCachedThreadPool();
    public static final Orchestrator orchestrator = new Orchestrator();
   public Server(InetSocketAddress address) {
       super(address);

   }
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("websocket open, Listening for connection at " + webSocket.getRemoteSocketAddress());



    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
       System.out.println("websocket closed");

    }

    @Override
    public void onMessage(WebSocket webSocket, String userMessage) {
        utils.Config.setActiveWebSocket(webSocket);;
        String response = orchestrator.run(userMessage);
        System.out.println("Response recieved");
        System.out.println("This is the response"+response);
        webSocket.send(response);





    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
       e.printStackTrace();

    }

    @Override
    public void onStart() {
       System.out.println("Server started");

    }
}

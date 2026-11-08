import core.Orchestrator;
import tools.FetchDate;
import tools.FetchLocation;
import core.Server;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

import static tools.AppControlTool.index;

public class Main {
    //PlayWrightConfig browser  = new PlayWrightConfig();
    FetchDate fD = new FetchDate();
    public static String city = FetchLocation.getCurrentLocation();
    public String date = fD.getCurrentDate();
    public static void main(String[] args) {
       index.indexBuilder();
        String host = "0.0.0.0";
        int port = 8887;


        WebSocketServer server = new Server(new InetSocketAddress(host, port));
        server.start();
    }
}

package cs451;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        //write/flush output file if necessary
        System.out.println("Writing output.");
    }
    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void talk(Layer layer, Host destHost) {
        Scanner scanner = new Scanner(System.in);
        String data;
        while ( !(data = scanner.nextLine()).equals("") ) {
            layer.send(null, data);
        }
        scanner.close();
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID: " + pid + "\n");
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

        System.out.println("My ID: " + parser.myId() + "\n");
        System.out.println("List of resolved hosts is:");
        System.out.println("==========================");
        for (Host host: parser.hosts()) {
            System.out.println(host.getId());
            System.out.println("Human-readable IP: " + host.getIp());
            System.out.println("Human-readable Port: " + host.getPort());
            System.out.println();
        }
        System.out.println();

        System.out.println("Path to output:");
        System.out.println("===============");
        System.out.println(parser.output() + "\n");

        System.out.println("Path to config:");
        System.out.println("===============");
        System.out.println(parser.config() + "\n");

        System.out.println("Doing some initialization\n");

    System.out.println("Broadcasting messages...");
    // Retrieve own port for initialisation
    int localPort = -1;
    for ( Host host : parser.hosts()) {
        if (host.getId() == parser.myId())
            localPort = host.getPort();
    }
    HostList.populate(parser.hosts());
    GroundLayer.start(localPort);
    Layer appli = new BebLayer(parser.hosts());
    talk(appli, parser.hosts().get(0));

    while (true) {
        // Sleep for 1 hour
        Thread.sleep(60 * 60 * 1000);
    }
    }
}
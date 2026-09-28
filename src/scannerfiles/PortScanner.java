package scannerfiles;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author B
 * @version 09.28.26
 *
 * An admin utility to scan for open ports on the host network.
 */

public class PortScanner {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);

        String hostName = requestHostName(input);
        int[] ports = requestPortRange(input);
        boolean verbose = isVerbose(input);

        if (!hostName.isEmpty()) {
            for (int port = ports[0]; port <= ports[1]; port++) {
                try (Socket socket = new Socket();) {
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(hostName, port);
                    socket.connect(inetSocketAddress, 200); // times out after 200ms
                    log(hostName, port, true);
                }
                catch (IOException e) {
                    if (verbose) {
                        log(hostName, port, false);
                    }
                }
            }
        }
        else {
            System.out.println("Network could not be found.");
        }
    }

    /**
     * Requests the host name to search from the user. The default will be used if one is not provided.
     *
     * @param input [Scanner] - the scanner variable set up in main
     * @return [String] - user response or "localhost"
     */
    public static String requestHostName(Scanner input) throws UnknownHostException {
        System.out.println("Enter the host name: ");
        String res = input.nextLine().trim();
        String host = res.isEmpty() ? "localhost" : res;
        String hostName = "";

        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            hostName = inetAddress.getHostName();
        }
        catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }

        return hostName;
    }

    /**
     * Requests the port range the user wants to scan.
     *
     * @param input [Scanner] - the scanner variable set up in main (Default: 1-65535)
     * @return [int[]] - the port range provided by the user
     */
    public static int[] requestPortRange(Scanner input) {
        int[] ports = {1, 65535};

        System.out.println("Enter the starting port (1 to 65535): ");
        String start = input.nextLine().trim();

        System.out.println("Enter the ending port (1 to 65535): ");
        String stop = input.nextLine().trim();

        try {
            int port = 0;

            if (!start.isEmpty()) port = Integer.parseInt(start);

            if (port >= 1 && port <= 65535) {
                ports[0] = port;
            }
            else {
                System.out.println(port + " is not a valid port. Starting port will default to 1.");
            }
        }
        catch (NumberFormatException _) {
            System.out.println("NUMBER FORMAT EXCEPTION: Input could not be converted to an integer. Starting port " +
                    "will default to 1.");
        }

        try {
            int port = 0;

            if (!stop.isEmpty()) port = Integer.parseInt(stop);

            if (port > ports[0] && port < 65535) {
                ports[1] = port;
            }
            else {
                System.out.println(port + " is not a valid port. Stopping port will default to 65535.");
            }
        }
        catch (NumberFormatException _) {
            System.out.println("NUMBER FORMAT EXCEPTION: Input could not be converted to an integer. Stopping port " +
                    "will default to 65535.");
        }

        return ports;
    }

    /**
     * Allows the user to make the logs verbose by including closed ports.
     *
     * @param input [Scanner] - the scanner variable set up in main
     * @return [boolean] - if logs should be verbose
     */
    public static boolean isVerbose(Scanner input) {
        System.out.println("Log closed ports? (n): ");
        String verbose = input.nextLine().trim();

        return !verbose.isEmpty() && (verbose.equalsIgnoreCase("y") || verbose.equalsIgnoreCase("yes"));
    }

    /**
     * Prints to console a log for the port checked.
     *
     * @param hostName [String] - the hostname of the network being scanned
     * @param port [int] - the port being scanned
     * @param open [boolean] - if the port is open
     */
    public static void log(String hostName, int port, boolean open) {
        String res;

        if (open) {
            res = "%s:%d | OPEN%n";
        }
        else {
            res = "%s:%d | CLOSED%n";
        }

        System.out.printf(res, hostName, port);
    }
}

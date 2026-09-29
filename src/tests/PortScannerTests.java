package tests;

import scannerfiles.PortScanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Blake
 * @version 09.28.26
 * Last Passed: 09.28.26
 *
 * Test cases for the PortScanner class. User inputs, defaults, and exceptions are tested.
 */

@DisplayName("Port Scanner Tests")
public class PortScannerTests {

    public final PrintStream STANDARD_OUTPUT = System.out;
    public final Scanner STANDARD_SCANNER = new Scanner(System.in);
    private final ByteArrayOutputStream OUTPUT_CAPTURE = new ByteArrayOutputStream();

    /**
     * Creates a mocked scanner using ByteArrayInputStream to inject user inputs into test cases.
     *
     * @param input [String] - the information to be injected into the test case
     * @return [Scanner] - the mocked scanner
     */
    private Scanner mockScanner(String input) {
        ByteArrayInputStream inputBytes = new ByteArrayInputStream(input.getBytes());
        return new Scanner(inputBytes);
    }

    /**
     * Swaps the output to a controlled Byte Array Stream for testing and marks PortScanner as in testing.
     */
    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(OUTPUT_CAPTURE));
        PortScanner.testing = true;
    }

    /**
     * Tests retrieval of network host name based on user input.
     *
     * @throws UnknownHostException - when host name could not be found
     */
    @Test
    @Tag("success")
    @Tag("user_input")
    @DisplayName("Host Name From User Input")
    void testRequestHostNameUserInput() throws UnknownHostException {
        Scanner scanner = mockScanner("localhost");

        String expected = "localhost";
        String actual = PortScanner.requestHostName(scanner);

        assertEquals(expected, actual);
    }

    /**
     * Tests retrieval of network host name based on programmed default when there is no user input.
     *
     * @throws UnknownHostException - when host name could not be found
     */
    @Test
    @Tag("success")
    @Tag("default")
    @DisplayName("Host Name From Default")
    void testRequestHostNameDefault() throws UnknownHostException {
        Scanner scanner = mockScanner("\n");

        String expected = "localhost";
        String actual = PortScanner.requestHostName(scanner);

        assertEquals(expected, actual);
    }

    /**
     * Tests that UnknownHostException is working with invalid host name.
     */
    @Test
    @Tag("failure")
    @Tag("exception")
    @DisplayName("Host Name Throws Exception")
    void testRequestHostNameException() {
        UnknownHostException expected = assertThrows(UnknownHostException.class, () -> {
            throw new UnknownHostException("testhost is not a valid host name.");
        });

        UnknownHostException actual = assertThrows(UnknownHostException.class, () -> {
            Scanner scanner = mockScanner("testhost");
            PortScanner.requestHostName(scanner);
        });

        assertEquals(expected.getMessage(), actual.getMessage());
    }

    /**
     * Tests retrieval of port range based on user input.
     *
     * @throws NumberFormatException - when input cannot be converted to an int
     */
    @Test
    @Tag("success")
    @Tag("user_input")
    @DisplayName("Port Range From User Input")
    void testRequestPortRangeUserInput() throws NumberFormatException {
        Scanner scanner = mockScanner("5\n61234\n");

        int[] expected = {5, 61234};
        int[] actual = PortScanner.requestPortRange(scanner);

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

    /**
     * Tests retrieval of port range from defaults when there is no user input.
     *
     * @throws NumberFormatException - when input cannot be converted to an int
     */
    @Test
    @Tag("success")
    @Tag("default")
    @DisplayName("Port Range From Default")
    void testRequestPortRangeDefault() throws NumberFormatException {
        Scanner scanner = mockScanner("\n\n");

        int[] expected = {1, 65535};
        int[] actual = PortScanner.requestPortRange(scanner);

        assertEquals(expected[0], actual[0]);
        assertEquals(expected[1], actual[1]);
    }

    /**
     * Tests that NumberFormatException is working for invalid port number(s).
     *
     * @param input [String] - unique inputs for test cases
     */
    @ParameterizedTest
    @Tag("failure")
    @Tag("exception")
    @ValueSource(strings = {"@!#\n5", "abc\n10"})
    @DisplayName("Port Range Throws Exception")
    void testRequestPortException(String input) {
        NumberFormatException expected = assertThrows(NumberFormatException.class, () -> {
            throw new NumberFormatException("NUMBER FORMAT EXCEPTION: Input could not be converted to an integer. Starting port " +
                    "will default to 1.");
        });

        NumberFormatException actual = assertThrows(NumberFormatException.class, () -> {
            Scanner scanner = mockScanner(input);
            PortScanner.requestPortRange(scanner);
        });

        assertEquals(expected.getMessage(), actual.getMessage());
    }

    /**
     * Tests setting the verbosity flag based on user input.
     *
     * @param input [String] - unique inputs for test cases
     */
    @ParameterizedTest
    @Tag("success")
    @Tag("user_input")
    @ValueSource(strings = {"y", "Y", "yes", "YES", "n", "N", "no", "NO"})
    @DisplayName("Is Verbose From User Input")
    void testIsVerboseUserInput(String input) {
        Scanner scanner = mockScanner(input + "\n");

        boolean expected = !(input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no"));
        boolean actual = PortScanner.isVerbose(scanner);

        assertEquals(expected, actual);
    }

    /**
     * Test setting the verbosity flag to default when there is no user input or user input is not valid.
     *
     * @param input [String] - unique inputs for test cases
     */
    @ParameterizedTest
    @Tag("success")
    @Tag("default")
    @ValueSource(strings = {"ye", "ys", "yess", "noo", "appls", "lkajsdf", ""})
    @DisplayName("Is Verbose From Default")
    void testIsVerboseDefault(String input) {
        Scanner scanner = mockScanner(input + "\n");

        boolean expected = false;
        boolean actual = PortScanner.isVerbose(scanner);

        assertEquals(expected, actual);
    }

    /**
     * Tests console logging accuracy.
     *
     * @param input [String] - unique inputs for test cases
     */
    @ParameterizedTest
    @Tag("success")
    @ValueSource(booleans = {true, false, true, false})
    @DisplayName("Log")
    void testLog(boolean input) {
        PortScanner.log("localhost", 8080, input);

        String expected = "localhost:" + 8080 + " | " + (input ? "OPEN" : "CLOSED");
        String actual = OUTPUT_CAPTURE.toString().trim();

        assertEquals(expected, actual);
    }

    /**
     * Tests the port scanner's functionality once all methods have been integrated and with user input.
     *
     * @param input [String] - unique inputs for test cases
     * @throws Exception - informs the user that the network could not be found (not currently tested)
     */
    @ParameterizedTest
    @Tag("success")
    @Tag("user_input")
    @ValueSource(strings = {"localhost\n5\n9000\nNo\n", "localhost\n10\n20\ny\n"})
    @DisplayName("Port Scanner From User Input")
    void testPortScannerUserInput(String input) throws Exception {
        PortScanner.input = mockScanner(input);

        PortScanner.main(new String[]{""});
        String output = OUTPUT_CAPTURE.toString().trim();
        int index = output.indexOf("localhost");
        String discard = index > 0 ? output.substring(0, index) : output.substring(0);
        output = output.replace(discard, "");

        if (input.endsWith("No\n")) {
            assertTrue(output.startsWith("localhost:") && output.endsWith(" | OPEN"));
        }
        else {
            assertTrue(output.startsWith("localhost:") && (output.endsWith(" | OPEN") || output.endsWith(" | CLOSED")));
        }
    }

    /**
     * Tests the port scanner's functionality once all methods have been integrated and with various defaults.
     *
     * @param input [String] - unique inputs for test cases
     * @throws Exception - informs the user that the network could not be found (not currently tested)
     */
    @ParameterizedTest
    @Tag("success")
    @Tag("default")
    @ValueSource(strings = {
            "\n10\n1000\ny\n",
            "localhost\n\n20\ny\n",
            "localhost\n60000\n\ny\n",
            "localhost\n\n\ny\n",
            "localhost\n2\n50\n\n",
            "\n\n\n\n"
    })
    @DisplayName("Port Scanner From Default")
    void testPortScannerDefault(String input) throws Exception {
        PortScanner.input = mockScanner(input);

        PortScanner.main(new String[]{""});
        String output = OUTPUT_CAPTURE.toString().trim();
        int index = output.indexOf("localhost");
        String discard = index > 0 ? output.substring(0, index) : output.substring(0);
        output = output.replace(discard, "");

        if (input.endsWith("\n\n")) {
            assertTrue((output.startsWith("localhost:") && output.endsWith(" | OPEN")) || output.isEmpty());
        }
        else {
            assertTrue(output.startsWith("localhost:") && (output.endsWith(" | OPEN") || output.endsWith(" | CLOSED")));
        }
    }

    /**
     * Tears down the testing set up by returning console output put normal, setting testing back to false, and
     * setting input back to original scanner.
     */
    @AfterEach
    public void tearDown() {
        System.setOut(STANDARD_OUTPUT);
        PortScanner.testing = false;
        PortScanner.input = STANDARD_SCANNER;
    }

}

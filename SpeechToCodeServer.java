import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SpeechToCodeServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/convert", exchange -> {
            // CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                String speech = new String(exchange.getRequestBody().readAllBytes()).toLowerCase();
                System.out.println("Received speech: " + speech);
                String code = generateCode(speech);

                exchange.sendResponseHeaders(200, code.length());
                OutputStream os = exchange.getResponseBody();
                os.write(code.getBytes());
                os.close();
            }
        });

        server.start();
        System.out.println("✅ Server running at http://localhost:8080");
    }

    private static String generateCode(String speech) {

        // Hello World
        if (speech.contains("hello")) {
            return """
            public class Main {
                public static void main(String[] args) {
                    System.out.println("Hello World");
                }
            }
            """;
        }

        // For Loop
        if (speech.contains("for loop") || speech.contains("for")) {
            return """
            for (int i = 1; i <= 10; i++) {
                System.out.println(i);
            }
            """;
        }

        // While Loop
        if (speech.contains("while loop") || speech.contains("while")) {
            return """
            int i = 0;
            while(i < 5) {
                System.out.println(i);
                i++;
            }
            """;
        }

        // If Condition
        if (speech.contains("if")) {
            return """
            int x = 10;
            if (x > 5) {
                System.out.println("x is greater than 5");
            }
            """;
        }

        // Array example
        if (speech.contains("array")) {
            return """
            int[] arr = {1, 2, 3, 4, 5};
            for(int num : arr){
                System.out.println(num);
            }
            """;
        }

        // Function / Method
        if (speech.contains("function") || speech.contains("method")) {
            return """
            public static void greet(String name) {
                System.out.println("Hello, " + name + "!");
            }

            public static void main(String[] args) {
                greet("World");
            }
            """;
        }

        // Class example
        if (speech.contains("class")) {
            return """
            public class Person {
                String name;
                int age;

                public Person(String name, int age) {
                    this.name = name;
                    this.age = age;
                }

                public void printInfo() {
                    System.out.println(name + " is " + age + " years old.");
                }

                public static void main(String[] args) {
                    Person p = new Person("Alice", 25);
                    p.printInfo();
                }
            }
            """;
        }

        // Snippet Manager
        if (speech.contains("snippet")) {
            return """
            import java.util.HashMap;
            import java.util.Map;

            public class SnippetManager {
                private Map<String, String> snippets = new HashMap<>();

                public void saveSnippet(String name, String code) {
                    snippets.put(name, code);
                }

                public String getSnippet(String name) {
                    return snippets.getOrDefault(name, "// Snippet not found");
                }

                public void printAllSnippets() {
                    snippets.forEach((k, v) -> System.out.println(k + ":\\n" + v));
                }
            }
            """;
        }

        // Default fallback
        return "// Speech received but no rule matched";
    }
}

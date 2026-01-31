import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.awt.Desktop;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CodeToGitHubToLinkedIn {

    // 🔑 PUT YOUR GITHUB TOKEN HERE
    private static final String GITHUB_TOKEN = "YOUR_GITHUB_TOKEN";
    private static final String GITHUB_USERNAME = "YOUR_GITHUB_USERNAME";

    public static void main(String[] args) {
        try {
            // 1️⃣ Code input
            String repoName = "java-code-exporter";
            String fileName = "HelloWorld.java";

            String code = """
                    public class HelloWorld {
                        public static void main(String[] args) {
                            System.out.println("Hello LinkedIn!");
                        }
                    }
                    """;

            // 2️⃣ Create GitHub repo
            createRepo(repoName);

            // 3️⃣ Upload code file
            uploadFile(repoName, fileName, code);

            // 4️⃣ Repo URL
            String repoUrl = "https://github.com/" + GITHUB_USERNAME + "/" + repoName;

            // 5️⃣ Export to LinkedIn
            exportToLinkedIn(repoUrl);

            System.out.println("✅ Done! Repo created and LinkedIn opened.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- METHODS ----------------

    private static void createRepo(String repoName) throws Exception {
        String json = """
                {
                  "name": "%s",
                  "description": "Auto-created repo from Java program",
                  "private": false
                }
                """.formatted(repoName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/user/repos"))
                .header("Authorization", "token " + GITHUB_TOKEN)
                .header("Accept", "application/vnd.github+json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void uploadFile(String repoName, String fileName, String code) throws Exception {
        String encodedCode = Base64.getEncoder()
                .encodeToString(code.getBytes(StandardCharsets.UTF_8));

        String json = """
                {
                  "message": "Initial commit",
                  "content": "%s"
                }
                """.formatted(encodedCode);

        String url = "https://api.github.com/repos/" + GITHUB_USERNAME +
                "/" + repoName + "/contents/" + fileName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "token " + GITHUB_TOKEN)
                .header("Accept", "application/vnd.github+json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void exportToLinkedIn(String repoUrl) throws Exception {
        String text = """
                🚀 New Java Project Uploaded!

                I built a Java tool that automatically:
                • Creates a GitHub repository
                • Uploads code
                • Prepares a LinkedIn post

                🔗 GitHub Repo:
                %s

                #Java #GitHub #Automation #Projects
                """.formatted(repoUrl);

        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);

        String linkedInUrl =
                "https://www.linkedin.com/feed/?shareActive=true&text=" + encoded;

        Desktop.getDesktop().browse(new URI(linkedInUrl));
    }
}

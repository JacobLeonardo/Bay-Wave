

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.net.MalformedURLException;
import java.io.OutputStream;
import java.net.URI;
//import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

public class MusicClient {

    private final String baseUrl;

    public MusicClient() {
        this.baseUrl = "https://baywave.org:8080";
    }

    /**Downloads a song by filename and stores it in a temporary file.
     * Use the path to create media object for javafx*
     * @param trckid trackid
     * @return Path to the temporary downloaded music file
     * @throws Exception on download error
     */
    public Path downloadSong(String trckid) throws Exception
    {
        String songUrl = baseUrl + "/song?trckid=" + trckid;
        URI uri = new URI(songUrl);
        URL url = uri.toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream()) {
            Path tempFile = Files.createTempFile("music", "" + trckid);
            Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        }
    }

    public String downloadSongData(String trckid, String username, String password) {
        try {
            String songUrl = baseUrl + "/song/metadata";
            URL url = new URI(songUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Tell server it's JSON
            conn.setRequestProperty("Content-Type", "application/json");

            // Build JSON
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("trckid", trckid);
            jsonMap.put("username", username);
            jsonMap.put("password", password);
            Gson gson = new Gson();
            String json = gson.toJson(jsonMap);

            // Send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Success
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    return reader.lines().collect(Collectors.joining());
                }
            } else {
                // Failure (403, 404, etc) → just return null
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all chain IDs associated with the given playlist.
     */
    public String downloadPlaylistChainData(String playlistId) {
        try {
            String playlistUrl = baseUrl + "/playlist/chains?id=" + playlistId;
            URL url = new URI(playlistUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Success
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    return reader.lines().collect(Collectors.joining());
                }
            } else {
                // Failure (403, 404, etc) → just return null
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all song IDs associated with the given chain.
     */
    public String downloadChainSongData(String chainId) {
        try {
            String chainUrl = baseUrl + "/chain/songs?id=" + chainId;
            URL url = new URI(chainUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(false);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Success
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    return reader.lines().collect(Collectors.joining());
                }
            } else {
                // Failure (403, 404, etc) → just return null
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String searchDb(String searchstring, int limit, int offset) throws Exception
    {

        String songUrl = baseUrl + "/search?searchstring=" + URLEncoder.encode(searchstring, "UTF-8") + "&limit=" + URLEncoder.encode("" + limit, "UTF-8") + "&offset=" + URLEncoder.encode("" + offset, "UTF-8");
        URL url = new URI(songUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    public boolean createAccount(String username, String password) {
        boolean completed = false;
        try {
            URL url = new URI("https://baywave.org:8080/user/post").toURL(); // adjust URL
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = buildJson(username, password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201) {
                completed = true;
            } else {
                System.err.println("Server responded with code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return completed;
    }



    private static String buildJson(String username, String password) {
        return "{\"username\":\"" + escapeJson(username) + "\",\"password\":\"" + escapeJson(password) + "\"}";
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public boolean toggleSongLike(String trckid, String username, String password) {
        try {
            // adjust the path to match your handler mapping
            String toggleUrl = baseUrl + "/song/like";
            URL url = new URI(toggleUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // build JSON payload
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("username", username);
            jsonMap.put("password", password);
            jsonMap.put("trckid", trckid);
            String json = new Gson().toJson(jsonMap);

            // send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            conn.disconnect();
            // 200 OK means toggle succeeded
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends a POST to your AddSongPlaylistHandler to add a track to a playlist.
     * @return true if the server returned 200 OK, false otherwise
     */
    public boolean addSongToPlaylist(String trckid, String playlistName, String username, String password) {
        try {
            // adjust the path to match your handler mapping
            String addUrl = baseUrl + "/playlist/add";
            URL url = new URI(addUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // build JSON payload
            Map<String, String> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("password", password);
            payload.put("playlistname", playlistName);
            payload.put("trckid", trckid);
            String json = new Gson().toJson(payload);

            // send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends a POST to your DeleteSongPlaylistHandler to remove a track from a playlist.
     * @return true if the server returned 200 OK, false otherwise
     */
    public boolean deleteSongFromPlaylist(String trckid, String playlistName, String username, String password) {
        try {
            // adjust the path to match your handler mapping
            String delUrl = baseUrl + "/playlist/delete";
            URL url = new URI(delUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // build JSON payload
            Map<String, String> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("password", password);
            payload.put("playlistname", playlistName);
            payload.put("trckid", trckid);
            String json = new Gson().toJson(payload);

            // send request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            conn.disconnect();
            return code == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns true if (username, password) are valid according to your AuthHandler.
     */
    public boolean authenticate(String username, String password) {
        try {
            String authUrl = baseUrl + "/user/auth";
            URL url = new URI(authUrl).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // build JSON payload
            Map<String, String> payload = new HashMap<>();
            payload.put("username", username);
            payload.put("password", password);
            String json = new Gson().toJson(payload);

            // send JSON body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                os.write(bytes, 0, bytes.length);
            }

            int code = conn.getResponseCode();
            // consume response so connection can close cleanly
            try (InputStream in = (code == 200
                    ? conn.getInputStream()
                    : conn.getErrorStream())) {
                // no-op
            }
            conn.disconnect();

            return code == HttpsURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }






    //for testing
     public static void main(String[] args)
     {
         MusicClient mc = new MusicClient();
         try
         {
             System.out.println(mc.downloadSongData("1","emersonTest2","passwordTest2"));
             System.out.println(mc.downloadSongData("1","emersonTest2","passwordTest3"));
         }
         catch (Exception e)
         {
             throw new RuntimeException(e);
         }
     }

}
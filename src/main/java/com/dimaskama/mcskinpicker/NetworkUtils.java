package com.dimaskama.mcskinpicker;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkUtils {

    public static void updateProfileInfo() {
        if (Main.getAuth() == null || Main.getAuth().getUser().isEmpty()) return;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet("https://api.minecraftservices.com/minecraft/profile");
            get.addHeader(getAuthHeader());
            Gson gson = new Gson();
            JsonObject response = client.execute(get, r -> gson.fromJson(new InputStreamReader(r.getEntity().getContent()), JsonObject.class));
            Main.currentSkin = ImageIO.read(new URL(response.getAsJsonArray("skins").get(0).getAsJsonObject().get("url").getAsString()));
            JsonArray capes = response.getAsJsonArray("capes");
            Main.capes.clear();
            for (JsonObject cape : capes.asList().stream().map(JsonElement::getAsJsonObject).toList())
                Main.capes.add(new Main.Cape(
                        cape.getAsJsonPrimitive("alias").getAsString(),
                        cape.getAsJsonPrimitive("id").getAsString(),
                        cape.getAsJsonPrimitive("state").getAsString().equalsIgnoreCase("ACTIVE")
                ));
        } catch (IOException | NullPointerException e) {
            Main.LOGGER.error(e);
        }
    }

    public static int changeSkin(BufferedImage src, boolean slim) {
        if (Main.getAuth() == null || Main.getAuth().getUser().isEmpty()) return 1;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");
            post.addHeader(getAuthHeader());
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("variant", slim ? "slim" : "classic");
            byte[] bytes;
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(src, "png", out);
                bytes = out.toByteArray();
            }
            builder.addBinaryBody("file", bytes, ContentType.APPLICATION_OCTET_STREAM, "src.png");
            post.setEntity(builder.build());
            return client.execute(post, response -> response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
        return 3;
    }

    public static int changeCape(String id) {
        if (Main.getAuth() == null || Main.getAuth().getUser().isEmpty()) return 1;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut put = new HttpPut("https://api.minecraftservices.com/minecraft/profile/capes/active");
            put.addHeader(getAuthHeader());
            put.addHeader("Content-Type", "application/json");
            put.setEntity(new StringEntity(String.format("{\"capeId\": \"%s\"}", id)));
            return client.execute(put, response -> response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
        return 3;
    }

    public static int deleteCape() {
        if (Main.getAuth() == null || Main.getAuth().getUser().isEmpty()) return 1;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpDelete delete = new HttpDelete("https://api.minecraftservices.com/minecraft/profile/capes/active");
            delete.addHeader(getAuthHeader());
            return client.execute(delete, response -> response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            Main.LOGGER.error(e);
        }
        return 3;
    }

    private static Header getAuthHeader() {
        return new BasicHeader("Authorization", "Bearer " + (Main.getAuth().getUser().isPresent() ? Main.getAuth().getUser().get().accessToken() : "0"));
    }
}

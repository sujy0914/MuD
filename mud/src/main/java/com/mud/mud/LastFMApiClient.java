package com.mud.mud;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LastFMApiClient {

    private static final String API_KEY = "3f185d4a574d2703f6e9a828aeaa2897"; // 발급받은 API 키로 대체
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonNode getTopTracksByTag(String tag, int limit) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("ws.audioscrobbler.com")
                .addPathSegment("2.0")
                .addQueryParameter("method", "tag.gettoptracks")
                .addQueryParameter("tag", tag)
                .addQueryParameter("limit", String.valueOf(limit))
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("format", "json")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Last.fm API 호출 실패 - 상태코드: " + response.code());
            }

            String json = response.body() != null ? response.body().string() : "{}";

            // 응답 로그 출력
            System.out.println("Last.fm 응답: " + json);

            JsonNode rootNode = mapper.readTree(json);

            // toptracks.track 배열이 비어 있는지 확인
            JsonNode tracks = rootNode.path("toptracks").path("track");
            if (tracks.isArray() && tracks.size() == 0) {
                System.out.println("태그 '" + tag + "'에 대한 트랙이 없습니다.");
            }

            return rootNode;
        }
    }
}

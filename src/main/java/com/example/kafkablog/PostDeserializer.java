package com.example.kafkablog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class PostDeserializer implements Deserializer<Post> {
    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public Post deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        Post post = null;
        try {
            post = mapper.readValue(arg1, Post.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() {
    }
}

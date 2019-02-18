package com.example.kafkablog;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostsListener {
    public static List<Post> findAll() {
        final StreamsBuilder builder = new StreamsBuilder();

//        final KStream<String, Post> postsJsonStream = builder.stream("posts", Consumed.with(Serdes.String(), postSerde));
//        postsJsonStream.peek(((key, value) -> System.out.println(value)));

        final Serde<Post> postSerde = Serdes.serdeFrom(new PostSerializer(), new PostDeserializer());
        final GlobalKTable<String, Post> postsTable = builder.globalTable("posts",
                Consumed.with(Serdes.String(), postSerde), Materialized.as("queryablePosts"));

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "posts-listener");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        System.out.println(postsTable.queryableStoreName());

        ReadOnlyKeyValueStore<String, Post> view = streams.store(postsTable.queryableStoreName(), QueryableStoreTypes.keyValueStore());
        List<Post> posts = new ArrayList<>();
        KeyValueIterator<String, Post> iterator = view.all();
        while (iterator.hasNext()) {
            KeyValue<String, Post> next = iterator.next();
            posts.add(next.value);
        }
        streams.close();
        return posts;
    }

    public static void main(final String[] args) throws Exception {
        List<Post> iterator = findAll();
        iterator.forEach((post) ->
                System.out.println("Found post: " + post.getTitle())
        );
    }
}

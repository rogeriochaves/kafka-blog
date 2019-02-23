package com.example.kafkablog;

import java.util.*;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

public class PostsStream {

    private static PostsStream singleton = new PostsStream();

    private static KafkaTemplate<String, Post> kafkaTemplate;
    private static KafkaStreams streams;

    private PostsStream() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PostSerializer.class);
        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));

        final StreamsBuilder builder = new StreamsBuilder();
        final Serde<Post> postSerde = Serdes.serdeFrom(new PostSerializer(), new PostDeserializer());
        final GlobalKTable<String, Post> postsTable = builder.globalTable("posts",
                Consumed.with(Serdes.String(), postSerde), Materialized.as("queryablePosts"));

        Properties streamProps = new Properties();
        streamProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "posts-listener");
        streamProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        KafkaStreams streams = new KafkaStreams(builder.build(), streamProps);
        streams.start();
        this.streams = streams;
    }

    public static PostsStream getInstance() {
        return singleton;
    }

    public void produce(Post post) {
        kafkaTemplate.send("posts", post.getId(), post);
    }

    public List<Post> findAll() {
        ReadOnlyKeyValueStore<String, Post> view = this.streams.store("queryablePosts", QueryableStoreTypes.keyValueStore());
        List<Post> posts = new ArrayList<>();
        KeyValueIterator<String, Post> iterator = view.all();
        while (iterator.hasNext()) {
            KeyValue<String, Post> next = iterator.next();
            posts.add(next.value);
        }
        return posts;
    }

    public Post find(String id) {
        ReadOnlyKeyValueStore<String, Post> view = this.streams.store("queryablePosts", QueryableStoreTypes.keyValueStore());
        List<Post> posts = new ArrayList<>();
        return view.get(id);
    }
}

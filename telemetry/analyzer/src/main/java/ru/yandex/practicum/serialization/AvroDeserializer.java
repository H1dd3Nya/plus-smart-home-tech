package ru.yandex.practicum.serialization;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

public class AvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final DecoderFactory decoderFactory;
    private final DatumReader<T> datumReader;

    public AvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public AvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        datumReader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) return null;
        try {
            BinaryDecoder binaryDecoder = decoderFactory.binaryDecoder(data, null);
            return datumReader.read(null, binaryDecoder);
        } catch (Exception e) {
            throw new RuntimeException("Невозможно десериальзовать данные", e);
        }
    }
}
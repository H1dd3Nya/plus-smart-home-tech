package ru.yandex.practicum.serialize;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.exception.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HexFormat;

public class AvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {
    private static final Logger log = LoggerFactory.getLogger(AvroSerializer.class);
    private static final HexFormat hexFormat = HexFormat.ofDelimiter(":");

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<T> datumWriter = new SpecificDatumWriter<>(data.getSchema());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            datumWriter.write(data, encoder);

            encoder.flush();

            byte[] bytes = outputStream.toByteArray();

            log.info("Данные сериализованы в формат Avro:\n{}", hexFormat.formatHex(bytes));

            return bytes;

        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации", e);
        }
    }
}
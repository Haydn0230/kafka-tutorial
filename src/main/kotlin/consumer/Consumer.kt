package consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import model.Person
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import producer.Producer
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Properties

class Consumer(brokers: String) {
    private val consumer = createConsumer(brokers)
    private val producer = Producer(brokers).createProducer(brokers)
    private val jsonMapper = jacksonObjectMapper()
//        ObjectMapper().apply {
//        registerKotlinModule()
//        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//        setDateFormat(StdDateFormat())
//    }
    private fun createConsumer(brokers: String):KafkaConsumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["group.id"] = "person-processor"
        props["key.deserializer"] = StringDeserializer::class.java
        props["value.deserializer"] = StringDeserializer::class.java
        return KafkaConsumer<String, String>(props)
    }

    fun consume(topic: String) {
        consumer.subscribe(listOf(topic))

        while (true) {
            val records = consumer.poll(Duration.ofSeconds(1))
            records.iterator().forEach { record ->
                val person = jsonMapper.readValue(record.value(), Person::class.java)
                // use instant
                val birthDate = person.birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                val age = Period.between(birthDate, LocalDate.now()).years
                val future = producer.send(ProducerRecord("ages", "${person.firstName}-${person.lastName}", age.toString()))
                future.get()
                println(person)
            }

        }
    }

}

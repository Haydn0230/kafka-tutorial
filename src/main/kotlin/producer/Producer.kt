package producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.javafaker.Faker
import model.Person
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

class Producer(brokers: String) {
    private val producer = createProducer(brokers)
    private val jsonMapper = jacksonObjectMapper()
    fun createProducer(brokers: String): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["key.serializer"] = StringSerializer::class.java.canonicalName
        props["value.serializer"] = StringSerializer::class.java.canonicalName
        return KafkaProducer(props)
    }


    fun produce(number: Int, topic: String) {
        for (i in 0 until number) {
            val faker = Faker()
            val fakePerson = Person(
                firstName = faker.name().firstName(),
                lastName = faker.name().lastName(),
                birthDate = faker.date().birthday()
            )

            val fakePersonJson = jsonMapper.writeValueAsString(fakePerson)
            val futureResult = producer.send(ProducerRecord(topic,"12345", fakePersonJson))
            futureResult.get()
            Thread.sleep(2000)
        }
    }

}

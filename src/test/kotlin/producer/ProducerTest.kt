package producer

import org.apache.kafka.clients.producer.MockProducer
import org.junit.jupiter.api.Assertions.*

internal class ProducerTest {
    val mockProducer : MockProducer<String, String> = MockProducer<String, String>()
    val producer = Producer("localhost:test")


    fun testProducer() {
        val brokers = ("localhost:test")
        producer.produce(2)
//        assertEquals()
    }
}

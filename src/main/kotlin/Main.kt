import consumer.Consumer
import producer.Producer

fun main(args: Array<String>) {
    val brokers = System.getenv("BROKERS")
    val numberToProduce = System.getenv("PRODUCE_NUMBER")
    val topic = System.getenv("TOPIC")

    Producer(brokers).produce(numberToProduce.toInt(), topic)
    Consumer(brokers).consume(topic)
}

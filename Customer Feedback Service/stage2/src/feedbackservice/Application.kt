package feedbackservice

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.testcontainers.containers.MongoDBContainer

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Component
class MongoContainerProvider {
    private val container = MongoDBContainer("mongo:5")
        .apply {
            addEnv("MONGO_INITDB_DATABASE", "feedback_db")
            portBindings = listOf("27017:27017")
        }

    @PostConstruct
    fun init() = container.start()

    @PreDestroy
    fun tearDown() = container.stop()
}

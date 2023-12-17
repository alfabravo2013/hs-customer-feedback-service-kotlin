package feedbackservice.feedback

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "feedback")
data class FeedbackDocument(
    @Id
    val id: String? = null,
    val rating: Int,
    val feedback: String?,
    val customer: String?,
    val product: String,
    val vendor: String
)

package feedbackservice.feedback

data class FeedbackUploadRequest(
    val rating: Int,
    val feedback: String?,
    val customer: String?,
    val product: String,
    val vendor: String
)

package feedbackservice.feedback

import com.fasterxml.jackson.annotation.JsonProperty

data class FeedbackPageResponse(
    @JsonProperty("total_documents")
    val totalDocuments: Long,

    @JsonProperty("is_first_page")
    val firstPage: Boolean,

    @JsonProperty("is_last_page")
    val lastPage: Boolean,

    val documents: List<FeedbackDocument>
)

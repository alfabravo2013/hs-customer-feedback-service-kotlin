package feedbackservice.feedback

import org.springframework.data.mongodb.repository.MongoRepository

interface FeedbackRepository : MongoRepository<FeedbackDocument, String>

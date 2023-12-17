import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

public class FeedbackServiceTests extends SpringTest {
    private static final String mongoUrl = "mongodb://localhost:27017";
    private static final String mongoDatabase = "feedback_db";
    private static final String mongoCollection = "feedback";
    private final Gson gson = new Gson();
    private final FeedbackItem[] feedbackItems = FeedbackItemMother.createRandomInRange(10, 20);

    CheckResult testPostFeedback(FeedbackItem[] data) {
        for (var item : data) {
            var payload = gson.toJson(item);
            HttpResponse response = post("/feedback", payload).send();

            checkStatusCode(response, 201);

            var location = response.getHeaders().get("Location");
            if (location == null || !location.matches("/feedback/[a-f0-9]{24}")) {
                return CheckResult.wrong("""
                        Request: POST /feedback
                        Expected to find the 'Location' header with a document URL '/feedback/<ObjectId>',
                        but found: %s
                        """.formatted(location));
            }
        }
        return CheckResult.correct();
    }

    CheckResult testMongoCollection(FeedbackItem[] data) {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUrl))
                .serverApi(serverApi)
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase(mongoDatabase);
            var count = database.getCollection(mongoCollection).countDocuments();
            if (count != data.length) {
                return CheckResult.wrong("""
                        Wrong number of documents in the '%s' collection in the '%s' database.
                        Expected: %d documents but found: %d")
                        """.formatted(mongoCollection, mongoDatabase, feedbackItems.length, count));
            }
        } catch (MongoException e) {
            return CheckResult.wrong("Failed to connect the 'feedback_db' database");
        }
        return CheckResult.correct();
    }

    private void checkStatusCode(HttpResponse response, int expected) {
        var method = response.getRequest().getMethod();
        var endpoint = response.getRequest().getEndpoint();
        var body = response.getRequest().getContent();
        var actual = response.getStatusCode();

        if (response.getStatusCode() != expected) {
            throw new WrongAnswer("""
                        Request: %s %s
                        Request body: %s
                        Expected response status code %d but received %d
                        """.formatted(method, endpoint, body, expected, actual));
        }
    }

    @DynamicTest
    DynamicTesting[] dt = {
            () -> testPostFeedback(feedbackItems),
            () -> testMongoCollection(feedbackItems),
    };
}

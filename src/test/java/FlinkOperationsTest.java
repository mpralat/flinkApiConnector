import io.flinkspector.core.collection.ExpectedRecords;
import io.flinkspector.datastream.DataStreamTestBase;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.log4j.BasicConfigurator;
import org.flinkproject.StreamTransformer;
import org.junit.Before;

/**
 * Created by marta on 04.09.17.
 *
 */

public class FlinkOperationsTest extends DataStreamTestBase {

    private DataStream<String> testStream;
//  Total: 10 + 40 = 50
    private static final String EXAMPLE_JSON1 =
            "{\"products\": [" +
                    "       {\"amount\": \"1\"," +
                    "       \"name\": \"product\"," +
                    "       \"pricePerUnit\": \"10\"}," +
                    "       {\"amount\": \"2\"," +
                    "       \"name\": \"product\"," +
                    "       \"pricePerUnit\": \"20\"}" +
            "],\"client\": {" +
                    "       \"age\": \"number\"," +
                    "       \"sex\": \"F\"" +
                    "}}";
//  Total: 70 + 30 = 100
    private static final String EXAMPLE_JSON2 = "{\"products\": [" +
                    "       {\"amount\": \"1\"," +
                    "       \"name\": \"product\"," +
                    "       \"pricePerUnit\": \"70\"}," +
                    "       {\"amount\": \"1\"," +
                    "       \"name\": \"product\"," +
                    "       \"pricePerUnit\": \"30\"}" +
            "],\"client\": {" +
            "       \"age\": \"number\"," +
            "       \"sex\": \"M\"" +
            "}}";
//  Total: 2 + 18 = 20
    private static final String EXAMPLE_JSON3 = "{\"products\": [" +
                "       {\"amount\": \"2\"," +
                "       \"name\": \"product\"," +
                "       \"pricePerUnit\": \"1\"}," +
                "       {\"amount\": \"2\"," +
                "       \"name\": \"product\"," +
                "       \"pricePerUnit\": \"9\"}" +
            "],\"client\": {" +
            "       \"age\": \"number\"," +
            "       \"sex\": \"F\"" +
            "}}";

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
        this.testStream = createTestStreamWith(EXAMPLE_JSON1)
                        .emit(EXAMPLE_JSON2)
                        .emit(EXAMPLE_JSON3)
                        .close();
    }

    @org.junit.Test
    public void testPriceCalculatorShouldSumAllProductsAndMultiplyByAmount() {
        ExpectedRecords<Tuple2<String, Integer>> expectedRecords = new ExpectedRecords<Tuple2<String, Integer>>()
                .expect(Tuple2.of("Sum", 50))
                .expect(Tuple2.of("Sum", 150))
                .expect(Tuple2.of("Sum", 170));

        assertStream(StreamTransformer.countTotalPrice(this.testStream), expectedRecords);
    }

    @org.junit.Test
    public void testClientSexCounter() {
        ExpectedRecords<Tuple2<String, Integer>> expectedRecords = new ExpectedRecords<Tuple2<String, Integer>>()
                .expect(Tuple2.of("F", 1))
                .expect(Tuple2.of("M", 1))
                .expect(Tuple2.of("F", 2));
        assertStream(StreamTransformer.countClientSex(this.testStream), expectedRecords);
    }
}

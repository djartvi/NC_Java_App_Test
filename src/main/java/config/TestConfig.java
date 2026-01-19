package config;


public final class TestConfig {
    public static final String BASE_URL =
            System.getProperty("base.url", "http://localhost:8080");

    public static final String API_KEY =
            System.getProperty("api.key");

    public static final boolean EXTERNAL_MOCK =
            Boolean.parseBoolean(System.getProperty("external.mock", "true"));

    public static final String MOCK_ADDRESS =
            System.getProperty("mock.address", "localhost");

    public static final int MOCK_PORT =
            Integer.parseInt(System.getProperty("mock.port", "8888"));
}

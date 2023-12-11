package RateControlTests.Models;

import RateControl.Models.ApiRequest.RequestDataType;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class RequestDataTypeTest {
    @Test
    public void testFromDataId() {
        for (int i = 0; i < 5; i++) {
            UUID id = UUID.randomUUID();

            RequestDataType out = RequestDataType.from(id.toString());

            assertEquals(RequestDataType.id, out);
        }
    }

    @ParameterizedTest
    @MethodSource("ipData")
    public void testFromDataIp(String data) {
        RequestDataType requestDataType = RequestDataType.from(data);
        assertEquals(RequestDataType.ip, requestDataType);
    }

    @ParameterizedTest
    @MethodSource("roleData")
    public void testFromDataRole(String data) {
        RequestDataType requestDataType = RequestDataType.from(data);
        assertEquals(RequestDataType.role, requestDataType);
    }

    private static Stream<Arguments> ipData() {
        return Stream.of(
                Arguments.of("192.168.0.1"),
                Arguments.of("144.11.44.11"),
                Arguments.of("141.183.12.144"),
                Arguments.of("114.1.5.2"),
                Arguments.of("127.0.0.1")

        );
    }

    private static Stream<Arguments> roleData() {
        return Stream.of(
                Arguments.of("role"),
                Arguments.of("admin"),
                Arguments.of("user"),
                Arguments.of("test")
        );
    }
}

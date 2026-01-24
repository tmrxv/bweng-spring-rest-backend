package at.technikum.springrestbackend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void tokenRoundTripSucceeds() {
        String secret = "012345678901234567890123456789012345"; // 36 chars btw
        JwtService service = new JwtService(secret, 60_000);

        String token = service.generateToken("user@example.com");

        assertTrue(service.isTokenValid(token, "user@example.com"));
        assertFalse(service.isTokenValid(token, "other@example.com"));
        assertEquals("user@example.com", service.extractSubject(token));
    }
}

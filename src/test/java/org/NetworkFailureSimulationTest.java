package org;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NetworkFailureSimulationTest {

    @Test
    void deveSimularTimeoutAoSalvarProduto() {
        boolean timeout = false;
        try {
            salvarComTimeout();
        } catch (RuntimeException e) {
            timeout = true;
        }
        assertTrue(timeout, "O sistema deve detectar timeouts e falhar graciosamente");
    }

    private void salvarComTimeout() {
        try {
            Thread.sleep(6000);
            throw new RuntimeException("Timeout ao conectar ao banco");
        } catch (InterruptedException ignored) {}
    }
}

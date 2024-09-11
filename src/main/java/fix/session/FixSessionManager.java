package fix.session;

import java.util.HashMap;
import java.util.Map;

public class FixSessionManager {
    private Map<String, FixSession> sessions = new HashMap<>();

    public FixSession createSession(String sessionId) {
        FixSession session = new FixSession(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public FixSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void handleHeartbeat(String sessionId) {
        FixSession session = sessions.get(sessionId);
        if (session != null) {
            session.updateLastHeartbeat();
        }
    }
}

class FixSession {
    private String sessionId;
    private long lastHeartbeat;

    public FixSession(String sessionId) {
        this.sessionId = sessionId;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public void updateLastHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() - lastHeartbeat) < 60000;
    }
}

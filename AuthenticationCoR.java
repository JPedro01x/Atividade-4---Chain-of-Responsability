public class AuthenticationCoR {
    public static void main(String[] args) {
        AuthHandler login = new LoginHandler();
        AuthHandler session = new SessionValidationHandler();
        AuthHandler permission = new PermissionHandler();

        login.setNext(session).setNext(permission);

        AuthenticationRequest request1 = new AuthenticationRequest("alice", "password123", "ADMIN", "token-123");
        AuthenticationRequest request2 = new AuthenticationRequest("bob", "wrongpass", "USER", "token-123");
        AuthenticationRequest request3 = new AuthenticationRequest("carol", "password123", "ADMIN", "invalid-token");
        AuthenticationRequest request4 = new AuthenticationRequest("dave", "password123", "USER", "token-123");

        System.out.println("--- Request 1 ---");
        login.handle(request1);
        System.out.println();

        System.out.println("--- Request 2 ---");
        login.handle(request2);
        System.out.println();

        System.out.println("--- Request 3 ---");
        login.handle(request3);
        System.out.println();

        System.out.println("--- Request 4 ---");
        login.handle(request4);
    }
}

class AuthenticationRequest {
    private final String username;
    private final String password;
    private final String requiredPermission;
    private final String sessionToken;

    AuthenticationRequest(String username, String password, String requiredPermission, String sessionToken) {
        this.username = username;
        this.password = password;
        this.requiredPermission = requiredPermission;
        this.sessionToken = sessionToken;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public String getSessionToken() {
        return sessionToken;
    }
}

abstract class AuthHandler {
    private AuthHandler nextHandler;

    public AuthHandler setNext(AuthHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public void handle(AuthenticationRequest request) {
        if (process(request)) {
            if (nextHandler != null) {
                nextHandler.handle(request);
            } else {
                System.out.println("Autenticação concluída com sucesso.");
            }
        }
    }

    protected abstract boolean process(AuthenticationRequest request);
}

class LoginHandler extends AuthHandler {
    @Override
    protected boolean process(AuthenticationRequest request) {
        System.out.printf("[LoginHandler] Verificando usuário '%s'...\n", request.getUsername());
        if ("password123" .equals(request.getPassword())) {
            System.out.println("[LoginHandler] Login bem-sucedido.");
            return true;
        }
        System.out.println("[LoginHandler] Falha no login: senha incorreta.");
        return false;
    }
}

class SessionValidationHandler extends AuthHandler {
    @Override
    protected boolean process(AuthenticationRequest request) {
        System.out.println("[SessionValidationHandler] Verificando token de sessão...");
        if ("token-123".equals(request.getSessionToken())) {
            System.out.println("[SessionValidationHandler] Sessão válida.");
            return true;
        }
        System.out.println("[SessionValidationHandler] Sessão inválida ou expirada.");
        return false;
    }
}

class PermissionHandler extends AuthHandler {
    @Override
    protected boolean process(AuthenticationRequest request) {
        System.out.printf("[PermissionHandler] Verificando permissão '%s'...\n", request.getRequiredPermission());
        if ("ADMIN".equals(request.getRequiredPermission()) || "USER".equals(request.getRequiredPermission())) {
            System.out.println("[PermissionHandler] Permissão concedida.");
            return true;
        }
        System.out.println("[PermissionHandler] Permissão negada.");
        return false;
    }
}

# Security (Quick Notes)

Summary
- Security is cross-cutting: authentication, authorization, transport, storage, and runtime controls.

Key areas
- AuthN/AuthZ: OAuth2/OpenID Connect for users, mTLS or JWT for services.
- Secrets: use vaults (HashiCorp Vault, AWS Secrets Manager) and avoid committing secrets to repo.
- Transport: TLS everywhere, HSTS, secure cookies.
- Data protection: encryption at rest, field-level encryption for sensitive data.

Operational
- Rotate keys and secrets, enforce least privilege, audit logs, and run periodic security scans.

Interview points
- Explain token flows, refresh tokens vs access tokens, and how to secure inter-service communication.

## Example: JWT validation (Java Servlet filter using JJWT)

```java
// Maven deps: io.jsonwebtoken:jjwt-api, jjwt-impl, jjwt-jackson
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;

public class JwtAuthFilter implements Filter {
    private final Key key = Keys.hmacShaKeyFor(System.getenv("JWT_SECRET").getBytes());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = auth.substring("Bearer ".length());
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            req.setAttribute("user", claims);
            chain.doFilter(request, response);
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
```

## Note: mTLS for service-to-service auth (Jetty snippet)

```java
// Maven deps: org.eclipse.jetty:jetty-server
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Server;

SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
sslContextFactory.setKeyStorePath("/path/to/keystore.jks");
sslContextFactory.setKeyStorePassword("changeit");
sslContextFactory.setTrustStorePath("/path/to/truststore.jks");
sslContextFactory.setTrustStorePassword("changeit");
sslContextFactory.setNeedClientAuth(true); // require client certs (mTLS)

Server server = new Server();
ServerConnector sslConnector = new ServerConnector(server, sslContextFactory);
sslConnector.setPort(8443);
server.addConnector(sslConnector);
```

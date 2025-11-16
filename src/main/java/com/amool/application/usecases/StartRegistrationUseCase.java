package com.amool.application.usecases;

import com.amool.application.port.out.EmailPort;
import com.amool.application.port.out.UserAccountPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.time.LocalDateTime;

public class StartRegistrationUseCase {

    private final UserAccountPort userAccountPort;
    private final EmailPort emailPort;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom rnd = new SecureRandom();

    public StartRegistrationUseCase(UserAccountPort userAccountPort,
                                    EmailPort emailPort) {
        this.userAccountPort = userAccountPort;
        this.emailPort = emailPort;
    }

    public void execute(String name, String surname, String username, String email, String password, String confirmPassword) {
        if (name == null || surname == null || username == null || email == null || password == null || confirmPassword == null)
            throw new IllegalArgumentException("Missing required fields");
        if (!password.equals(confirmPassword))
            throw new IllegalArgumentException("Passwords do not match");
        if (userAccountPort.emailExists(email))
            throw new IllegalArgumentException("Email already exists");
        if (userAccountPort.usernameExists(username))
            throw new IllegalArgumentException("Username already exists");

        String code = String.format("%06d", rnd.nextInt(1_000_000));
        String hash = encoder.encode(password);
        LocalDateTime expires = LocalDateTime.now().plusMinutes(15);
        userAccountPort.upsertPendingUser(name, surname, username, email, hash, code, expires);

        String subject = "Looma - Código de verificación";
        String template = """
                <!doctype html>
                <html>
                  <head>
                    <meta charset=\"UTF-8\" />
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
                    <title>Looma - Verificación</title>
                  </head>
                  <body style=\"margin:0;background:#f6f7fb;font-family:Arial,Helvetica,sans-serif;\">
                    <table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"background:#f6f7fb;\">
                      <tr>
                        <td align=\"center\" style=\"padding:24px;\">
                          <table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"max-width:560px;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 6px 20px rgba(0,0,0,0.08);\">
                            <tr>
                              <td align=\"center\" style=\"background:#E6E0F8;padding:16px 24px;\">
                                <img src=\"cid:loomaLogo\" alt=\"Looma\" style=\"display:block;height:32px;max-width:160px;width:auto;\" />
                              </td>
                            </tr>
                            <tr>
                              <td style=\"padding:24px 24px 8px;color:#111827;font-size:18px;font-weight:600;\">
                                Hola {{NAME}},
                              </td>
                            </tr>
                            <tr>
                              <td style=\"padding:8px 24px 0;color:#4b5563;font-size:14px;line-height:1.6;\">
                                Para continuar con tu registro, usá este código de verificación:
                              </td>
                            </tr>
                            <tr>
                              <td align=\"center\" style=\"padding:20px 24px 8px;\">
                                <div style=\"display:inline-block;padding:14px 22px;border-radius:10px;background:#f1e9fb;border:1px solid #5C17A6;color:#5C17A6;font-weight:700;font-size:24px;letter-spacing:3px;\">
                                  {{CODE}}
                                </div>
                              </td>
                            </tr>
                            <tr>
                              <td style=\"padding:8px 24px 24px;color:#6b7280;font-size:12px;line-height:1.6;\">
                                El código vence en 15 minutos. Si no fuiste vos, ignorá este mensaje.
                              </td>
                            </tr>
                          </table>
                          <div style=\"padding:16px;color:#9ca3af;font-size:11px;\">
                            © {{YEAR}} Looma
                          </div>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """;
        String body = template
                .replace("{{NAME}}", name)
                .replace("{{CODE}}", code)
                .replace("{{YEAR}}", String.valueOf(java.time.LocalDateTime.now().getYear()));
        emailPort.send(email, subject, body);
    }
}



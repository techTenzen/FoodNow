

import com.foodnow.model.User;
import com.foodnow.repository.UserRepository;
import com.foodnow.security.JwtTokenProvider;
import com.foodnow.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginData.getEmail(), loginData.getPassword()
        ));

        User user = userService.getUserByEmail(loginData.getEmail());
        String token = tokenProvider.generateToken(user);
        return ResponseEntity.ok().body(token);
    }
}
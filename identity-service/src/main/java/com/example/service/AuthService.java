package com.example.service;

import com.example.Mapper.UserMapper;
import com.example.dto.AuthRequest;
import com.example.dto.RegisterUserDto;
import com.example.dto.SignInStatus;
import com.example.dto.TokenDto;
import com.example.entity.DriveUser;
import com.example.repository.DriveUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final DriveUserRepository driveUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final UserMapper userMapper;

    public SignInStatus saveUser(RegisterUserDto registerUserDto) {

        String email = registerUserDto.getEmail();

        if(!validateEmail(email)){
            return SignInStatus.builder()
                    .sucess(false)
                    .errorMessage("Email : " + email + " is not valid!" )
                    .build();
        }

        if(!validateName(registerUserDto.getFirstName())){
            return SignInStatus.builder()
                    .sucess(false)
                    .errorMessage("FirstName : " + registerUserDto.getFirstName() + " is not a vaild name!")
                    .build();
        }

        if(!validateName(registerUserDto.getLastName())){
            return SignInStatus.builder()
                    .sucess(false)
                    .errorMessage("LastName : " + registerUserDto.getLastName() + " is not a vaild name!")
                    .build();
        }

        if(!isStrongPass(registerUserDto.getPassword())){
            return SignInStatus.builder()
                    .sucess(false)
                    .errorMessage("Password must contain one digit, one lowercase, one uppercase, contains " +
                            "atleast one char within a set of special chars @#$%^&+= and must be atleast 8 characters long"
                            )
                    .build();
        }

        boolean userExists = driveUserRepository.existsByEmail(registerUserDto.getEmail()) ;

        if(userExists)
            return SignInStatus.builder()
                    .errorMessage("user with email : " + registerUserDto.getEmail() + " already exists")
                    .sucess(false)
                    .build();


        DriveUser user = userMapper.newUser(registerUserDto);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        driveUserRepository.save(user);

        return SignInStatus.builder()
                .sucess(true)
                .errorMessage(null)
                .build();

    }

    public TokenDto login(AuthRequest authRequest){

        TokenDto error = TokenDto.builder()
                .sucess(false)
                .token(null)
                .build();

        boolean userExists = driveUserRepository.existsByEmail(authRequest.getUsername());

        if(!userExists){
            return error;
        }

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authenticate.isAuthenticated()) {
            return
            TokenDto.builder()
                    .sucess(true)
                    .token(generateToken(authRequest.getUsername()))
                    .build();
        } else {
            return error;
        }
    }

    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    private boolean validateEmail(String emailStr) {
        Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailRegex.matcher(emailStr);
        return matcher.matches();
    }

    private boolean isStrongPass(String pass){
        Pattern strongPass = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
        Matcher matcher = strongPass.matcher(pass);
        return matcher.matches();
    }

    private boolean validateName(String name){
        Pattern validName = Pattern.compile("^[^- '](?=(?!\\p{Lu}?\\p{Lu}))(?=(?!\\p{Ll}+\\p{Lu}))(?=(?!.*\\p{Lu}\\p{Lu}))(?=(?!.*[- '][- '.]))(?=(?!.*[.][-'.]))(\\p{L}|[- '.]){2,}$");
        Matcher matcher = validName.matcher(name);
        return  matcher.matches();
    }

}

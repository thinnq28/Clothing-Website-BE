package com.datn.shop_app.controller.user;

import com.datn.shop_app.DTO.ChangePasswordDTO;
import com.datn.shop_app.DTO.ResetPasswordDTO;
import com.datn.shop_app.DTO.UserDTO;
import com.datn.shop_app.DTO.UserLoginDTO;
import com.datn.shop_app.entity.Token;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.repository.UserRepository;
import com.datn.shop_app.response.user.LoginResponse;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.user.UpdateUserDTO;
import com.datn.shop_app.response.user.UserResponse;
import com.datn.shop_app.service.RedisTokenService;
import com.datn.shop_app.service.TokenService;
import com.datn.shop_app.service.UserService;
import com.datn.shop_app.utils.JwtTokenUtils;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/client/users")
@RequiredArgsConstructor
public class ClientController {

    private final UserService userService;

    private final TokenService tokenService;

    private final RedisTokenService redisTokenService;

    private final LocalizationUtils localizationUtils;

    private final UserRepository userRepository;

    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@Valid @RequestBody UserDTO userDTO,
                                                   BindingResult result) throws DataNotFoundException {

        List<String> errors = userService.validUser(result, userDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_USER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        UserResponse userResponse = userService.save(userDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_USER_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(userResponse)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@Valid @RequestBody UserLoginDTO userLoginDTO,
                                                BindingResult result,
                                                HttpServletRequest request) throws Exception {
        //valid data login
        List<String> errors = userService.validLogin(result, userLoginDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors).build());
        }

        // Kiểm tra thông tin đăng nhập và sinh token
        String token = userService.login(userLoginDTO);
        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) //method reference
                .id(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(loginResponse).build());
    }

    @PostMapping("/details")
    public ResponseEntity<ResponseObject> getUserDetails(
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
        User user = userService.getUserDetailsFromToken(extractedToken);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_USER_SUCCESSFULLY))
                        .data(UserResponse.fromUser(user))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateUser(@PathVariable("id") Integer userId,
                                                     @Valid @RequestBody UpdateUserDTO updateUserDTO,
                                                     BindingResult result,
                                                     @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractedToken);
        // Ensure that the user making the request matches the user being updated
        if (user.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<String> errors = userService.validUpdateUser(result, updateUserDTO, userId);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_USER_FAILED))
                            .data(errors)
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        User updatedUser = userService.updateUser(userId, updateUserDTO);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_USER_SUCCESSFULLY))
                        .data(UserResponse.fromUser(updatedUser))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO,
            BindingResult result,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);

            List<String> errors;
            errors = userService.validateChangePassword(user, result, changePasswordDTO);

            if(!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.CHANGE_PASSWORD_FAILED))
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors).build());
            }

            User newUser = userService.updateUserPassword(user, changePasswordDTO);

            UserResponse userResponse = UserResponse.fromUser(newUser);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CHANGE_PASSWORD_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(userResponse).build());
        }catch (Exception ex){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CHANGE_PASSWORD_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(ex.getMessage()).build());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseObject> forgetPassword(@RequestParam String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Tài khoản của bạn không tồn tại!!!")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        userService.sendLinkResetPassword(email);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Gửi mail thành công, vui lòng kiểm tra mail của bạn!")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO,
                                                        BindingResult bindingResult) {
        List<String> errors = userService.validatePassword(resetPasswordDTO, bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.TOKEN_IS_INVALID))
                            .status(HttpStatus.UNAUTHORIZED)
                            .data(errors)
                            .build());
        }

        String token = resetPasswordDTO.getToken();
        if (!jwtTokenUtils.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.TOKEN_IS_INVALID))
                            .status(HttpStatus.UNAUTHORIZED)
                            .build());
        }

        if(redisTokenService.isUsedToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.TOKEN_IS_USED))
                            .status(HttpStatus.UNAUTHORIZED)
                            .build());
        }

        if(redisTokenService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .message("Link của bạn đã hết hạn")
                            .status(HttpStatus.UNAUTHORIZED)
                            .build());
        }

        String email = jwtTokenUtils.extractEmail(token);
        // Cập nhật mật khẩu cho user có email này (hash mật khẩu trước khi lưu)
        try {
            userService.resetPassword(email, resetPasswordDTO.getPassword(), token);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Reset mật khẩu thành công")
                    .status(HttpStatus.OK)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_IS_NOT_EXIST))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
    }
}

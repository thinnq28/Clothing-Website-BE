package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.ChangePasswordDTO;
import com.datn.shop_app.DTO.ResetPasswordDTO;
import com.datn.shop_app.DTO.UserDTO;
import com.datn.shop_app.DTO.UserLoginDTO;
import com.datn.shop_app.entity.Role;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.UserRole;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.exception.ExpiredTokenException;
import com.datn.shop_app.exception.InvalidParamException;
import com.datn.shop_app.repository.RoleRepository;
import com.datn.shop_app.repository.UserRepository;
import com.datn.shop_app.repository.UserRoleRepository;
import com.datn.shop_app.response.user.UpdateUserDTO;
import com.datn.shop_app.response.user.UserResponse;
import com.datn.shop_app.service.EmailService;
import com.datn.shop_app.service.RedisTokenService;
import com.datn.shop_app.service.UserService;
import com.datn.shop_app.utils.DateUtils;
import com.datn.shop_app.utils.JwtTokenUtils;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.datn.shop_app.utils.ValidationUtils.isValidEmail;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final LocalizationUtils localizationUtils;

    private final RoleRepository roleRepository;

    private final UserRoleRepository userRoleRepository;

    private final DateUtils dateUtils;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtils jwtTokenUtils;

    @Value("${domain.front-end}")
    private String domainFrontEnd;

    private final RedisTokenService redisTokenService;

    private final EmailService emailService;


    @Override
    public UserResponse save(UserDTO userDTO) throws DataNotFoundException {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setActive(true);
        user.setDateOfBirth(dateUtils.convertDateToLocalDate(userDTO.getDateOfBirth()));
        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setEmail(userDTO.getEmail());

        user = userRepository.save(user);

        Optional<Role> role = Optional.of(roleRepository.findById(userDTO.getRoleId()))
                .orElseThrow(() -> new DataNotFoundException("Data not found!"));

        UserRole userRole = new UserRole();
        userRole.setRole(role.get());
        userRole.setUser(user);
        userRole = userRoleRepository.save(userRole);

        user.setUserRoles(List.of(userRole));

        UserResponse userResponse = UserResponse.fromUser(user);

        return userResponse;
    }

    @Override
    public List<String> validUser(BindingResult result, UserDTO userDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError error : fieldErrors) {
                errorMessages.add(error.getDefaultMessage());
            }
        }

        Optional<User> existingUser = userRepository.findByEmailOrPhoneNumber(userDTO.getEmail(), userDTO.getPhoneNumber());
        if (existingUser.isPresent())
            errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_EXIST));

        if (!userDTO.getPassword().equals(userDTO.getRetypePassword()))
            errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));

        return errorMessages;
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws InvalidParamException {

        Optional<User> user = userRepository.findByPhoneNumberAndActive(userLoginDTO.getPhoneNumber(), true);
        String subject = userLoginDTO.getPhoneNumber();
        // Create authentication token using the found subject and granted authorities
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject,
                userLoginDTO.isPasswordBlank() ? "" : userLoginDTO.getPassword(),
                user.get().getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(user.get());
    }

    @Override
    public List<String> validLogin(BindingResult result, UserLoginDTO userLoginDTO) {
        List<String> errorMessages = new ArrayList<>();
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError error : fieldErrors) {
                errorMessages.add(error.getDefaultMessage());
            }

            return errorMessages;
        }

        Optional<User> user = userRepository.findByPhoneNumberAndActive(userLoginDTO.getPhoneNumber(), true);
        if (user.isEmpty()) {
            errorMessages.add("Tài khoản hoặc mật khẩu không chính xác");
            return errorMessages;
        }

        List<UserRole> userRoles = user.get().getUserRoles();

        for (UserRole userRole : userRoles) {
            if (userRole.getRole().getActive() && userRole.getRole().getRoleName().equals("ROLE_GUEST")) {
                errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_NOT_ALLOWED_LOGIN));
                return errorMessages;
            }

        }

        if (!user.get().getActive()) {
            errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
            return errorMessages;
        }

        boolean matches = passwordEncoder.matches(userLoginDTO.getPassword(), user.get().getPassword());
        if(!matches){
            errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.CURRENT_PASSWORD_WRONG));
        }

        return errorMessages;
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtils.getSubject(token);
        Optional<User> user = userRepository.findByPhoneNumberAndActive(subject, true);
        if (user.isEmpty() && isValidEmail(subject)) {
            user = userRepository.findByEmailAndActive(subject, true);
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }

    @Override
    public Page<UserResponse> getAllUsers(String name, String phoneNumber, String email, Boolean active, Pageable pageable) {
        Page<User> userPage = userRepository.findAllUsers(name, phoneNumber, email, active, pageable);
        return userPage.map(UserResponse::fromUser);
    }

    @Override
    public Page<UserResponse> getAllUsers(String name, String phoneNumber, String email, Boolean active, Integer userId, Pageable pageable) {
        Page<User> userPage = userRepository.findAllUsers(name, phoneNumber, email, active, userId, pageable);
        return userPage.map(UserResponse::fromUser);
    }

    @Override
    public User updateUser(Integer userId, UpdateUserDTO updateUserDTO) throws DataNotFoundException {
        // Find the existing user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        existingUser.setFullName(updateUserDTO.getFullName());
        existingUser.setAddress(updateUserDTO.getAddress());
        existingUser.setEmail(updateUserDTO.getEmail());
        if (updateUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(dateUtils.convertDateToLocalDate(updateUserDTO.getDateOfBirth()));
        }

        // Save the updated user
        return userRepository.save(existingUser);
    }

    @Override
    public List<String> validUpdateUser(BindingResult result, UpdateUserDTO updateUserDTO, Integer userId) throws DataNotFoundException {
        List<String> errorMessages = new ArrayList<>();
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError error : fieldErrors) {
                errorMessages.add(error.getDefaultMessage());
            }
            return errorMessages;
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (!updateUserDTO.getEmail().equals(existingUser.getEmail())) {
            Optional<User> userByEmail = userRepository.findByEmail(updateUserDTO.getEmail());
            if (userByEmail.isPresent())
                errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_ALREADY_IS_USED));
        }

        return errorMessages;
    }

    @Override
    public void deleteUser(Integer userId) throws DataNotFoundException {
        Optional<User> user = userRepository.findByIdAndActive(userId, true);
        if (user.isPresent()) {
            user.get().setActive(false);
            userRepository.save(user.get());
        } else {
            throw new DataNotFoundException("User is not exist");
        }
    }

    @Override
    public void unLockUser(Integer userId) throws DataNotFoundException {
        Optional<User> user = userRepository.findByIdAndActive(userId, false);
        if (user.isPresent()) {
            user.get().setActive(true);
            userRepository.save(user.get());
        } else {
            throw new DataNotFoundException("User is not exist");
        }
    }

    @Override
    public User updateUserPassword(User user, ChangePasswordDTO changePasswordDTO) {
        String newPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public List<String> validateChangePassword(User user, BindingResult result, ChangePasswordDTO changePasswordDTO) {
        List<String> errorMessages = new ArrayList<>();

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError error : fieldErrors) {
                errorMessages.add(error.getDefaultMessage());
            }

            return errorMessages;
        }

        boolean matches = passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword());
        if (!matches) {
            errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.CURRENT_PASSWORD_WRONG));
        }

        if (!Objects.equals(changePasswordDTO.getConfirmPassword(), changePasswordDTO.getNewPassword())) {
            errorMessages.add(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH));
        }

        return errorMessages;
    }

    @Override
    public void sendLinkResetPassword(String email) {
        String token = jwtTokenUtils.generateTokenWithEmail(email);
        String resetUrl = domainFrontEnd + "haiha/reset-password?token=" + token;

        // Dòng tiêu đề được nâng cấp
        String subject = "🔐 Hệ Thống Quản Lý Website bán quần áo Hải Hà- Đặt Lại Mật Khẩu Ngay!";

        // Nội dung email HTML
        String content = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; padding: 20px; background-color: #f9f9f9; border-radius: 10px;">
                    <h2 style="color: #333;">Yêu Cầu Đặt Lại Mật Khẩu</h2>
                    <p>Xin chào,</p>
                    <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu của bạn. Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>
                    <p>Để đặt lại mật khẩu, vui lòng nhấn vào nút bên dưới:</p>
                    <a href="%s" style="display: inline-block; padding: 12px 20px; font-size: 16px; color: white; background-color: #007bff; text-decoration: none; border-radius: 5px;">Đặt Lại Mật Khẩu</a>
                    <p>Hoặc sao chép và dán liên kết này vào trình duyệt của bạn:</p>
                    <p style="word-break: break-all;">%s</p>
                    <p><strong>Lưu ý:</strong> Liên kết này chỉ có hiệu lực trong vòng 24 giờ.</p>
                    <p>Trân trọng,</p>
                    <p><strong>Đội Ngũ Hỗ Trợ Hệ Thống Quản Lý Website bán quần áo Hải Hà</strong></p>
                </div>
                """.formatted(resetUrl, resetUrl);


        try {
            redisTokenService.saveToken(token, email);
            emailService.sendMail(email, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> validatePassword(ResetPasswordDTO resetPasswordDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errors.add(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return errors;
        }

        if (!Objects.equals(resetPasswordDTO.getPassword(), resetPasswordDTO.getRetypePassword())) {
            errors.add("Mật khẩu không khớp");
        }

        return errors;
    }

    @Override
    public void resetPassword(String email, String password, String token) throws DataNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy tài khoản");
        }

        String hashCodePassword = passwordEncoder.encode(password);
        user.get().setPassword(hashCodePassword);
        redisTokenService.deleteToken(token);
        userRepository.save(user.get());
    }
}

package com.datn.shop_app.controller.user;

import com.datn.shop_app.DTO.CommentRateDTO;
import com.datn.shop_app.entity.CommentRate;
import com.datn.shop_app.entity.Commodity;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.exception.DataNotFoundException;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.comment.CommentRateResponse;
import com.datn.shop_app.response.comment.ListCommentRateResponse;
import com.datn.shop_app.response.commodity.CommodityResponse;
import com.datn.shop_app.response.commodity.ListCommodityResponse;
import com.datn.shop_app.service.CommentRateService;
import com.datn.shop_app.service.UserService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/client/comment-rate")
public class CommentRateController {

    private final CommentRateService commentRateService;

    private final LocalizationUtils localizationUtils;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseObject> rateComment(@Valid @RequestBody CommentRateDTO commentRateDTO,
                                                      BindingResult result,
                                                      @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        if(result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for(FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_COMMENT_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errors).build());
        }

        String extractedToken = authorizationHeader.substring(7);
        User user = userService.getUserDetailsFromToken(extractedToken);

        CommentRate commentRate = commentRateService.save(commentRateDTO, user);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_COMMODITY_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(commentRate).build());

    }

    @GetMapping("{productId}")
    public ResponseEntity<ResponseObject> getCommentRate(@PathVariable Integer productId,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<CommentRateResponse> commentRates = commentRateService.getCommentRates(productId, pageRequest);
        totalPages = commentRates.getTotalPages();
        List<CommentRateResponse> commentResponses = commentRates.getContent();

        ListCommentRateResponse listCommentRateResponse = ListCommentRateResponse.builder()
                .commentRates(commentResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Lấy các bình luận thành công")
                .status(HttpStatus.OK)
                .data(listCommentRateResponse)
                .build());
    }
}

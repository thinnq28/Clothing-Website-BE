package com.datn.shop_app.utils;

public class MessageKeys {
    public static final String LOGIN_SUCCESSFULLY =  "user.login.login_successfully";
    public static final String REGISTER_SUCCESSFULLY =  "user.login.register_successfully";
    public static final String LOGIN_FAILED =  "user.login.login_failed";
    public static final String PASSWORD_NOT_MATCH =  "user.register.password_not_match";
    public static final String USER_IS_LOCKED = "user.login.user_is_locked";
    public static final String USER_IS_EXIST = "user.register.user_is_exist";
    public static final String USER_IS_NOT_FOUND = "user.user_is_not_found";
    public static final String USER_IS_NOT_ALLOWED_LOGIN = "user.login.user_is_not_allowed_login";
    public static final String CURRENT_PASSWORD_WRONG = "user.login.wrong_current_password";

    public static final String FILE_IMAGE_CANNOT_BE_EMPTY = "file_image_cannot_be_empty";
    public static final String FILE_IMAGE_LARGE = "file_image_large";
    public static final String FILE_MUST_BE_IMAGE = "file_must_be_image";
    public static final String UPLOAD_IMAGE_SUCCESSFULLY = "upload_image_successfully";
    public static final String UPLOAD_IMAGE_FAILED = "upload_image_failed";
    public static final String READ_FILE_SUCCESSFULLY = "read_file_successfully";
    public static final String READ_FILE_FAILED = "read_file_failed";
    public static final String MAXIMUM_FILE = "maximum_file";
    public static final String MAXIMUM_FILE_WITH_CONDITION = "maximum_file_with_condition";


    public static final String WRONG_PHONE_PASSWORD = "user.login.wrong_phone_password";

    public static final String INSERT_COMMODITY_SUCCESSFULLY = "commodity.create_commodity_successfully";
    public static final String INSERT_COMMODITY_FAILED = "commodity.create_commodity_failed";
    public static final String UPDATE_COMMODITY_FAILED = "commodity.update_commodity_failed";
    public static final String UPDATE_COMMODITY_SUCCESSFULLY = "commodity.update_commodity_successfully";
    public static final String DELETE_COMMODITY_SUCCESSFULLY = "commodity.delete_commodity_successfully";
    public static final String GET_COMMODITY_SUCCESSFULLY = "commodity.get_commodity_successfully";
    public static final String GET_COMMODITY_FAILED = "commodity.get_commodity_failed";
    public static final String COMMODITY_IS_EXISTS = "commodity.commodity_is_exists";
    public static final String COMMODITY_IS_NOT_FOUND = "commodity.commodity_is_not_found";

    public static final String SUPPLIER_IS_NOT_EXISTS = "supplier.supplier_is_not_exists";
    public static final String INSERT_SUPPLIER_SUCCESSFULLY = "supplier.insert_supplier_successfully";
    public static final String INSERT_SUPPLIER_FAILED = "supplier.insert_supplier_failed";
    public static final String UPDATE_SUPPLIER_SUCCESSFULLY = "supplier.update_supplier_successfully";
    public static final String UPDATE_SUPPLIER_FAILED = "supplier.update_supplier_failed";
    public static final String GET_SUPPLIER_SUCCESSFULLY = "supplier.get_supplier_successfully";
    public static final String GET_SUPPLIER_FAILED = "supplier.get_supplier_failed";
    public static final String DELETE_SUPPLIER_SUCCESSFULLY = "supplier.delete_supplier_successfully";


    public static final String INSERT_OPTION_SUCCESSFUL = "option.create_option_successfully";
    public static final String INSERT_OPTION_FAILED = "option.create_option_failed";
    public static final String GET_OPTION_FAILED = "option.get_option_failed";
    public static final String GET_OPTION_SUCCESSFUL = "option.get_option_successfully";
    public static final String OPTION_IS_NOT_EXISTS = "option.option_is_not_exists";
    public static final String OPTION_IS_NOT_FOUND = "option.option_is_not_found";
    public static final String OPTION_IS_NOT_CHOOSE = "option.option_is_not_choose";
    public static final String UPDATE_OPTION_SUCCESSFUL = "option.update_option_successfully";
    public static final String DELETE_OPTION_SUCCESSFULLY = "option.delete_option_successfully";

    public static final String INSERT_OPTION_VALUE_SUCCESSFULLY = "option.insert_option_value_successfully";
    public static final String INSERT_OPTION_VALUE_FAILED = "option.insert_option_value_failed";
    public static final String UPDATE_OPTION_VALUE_SUCCESSFULLY = "option.update_option_value_successfully";
    public static final String UPDATE_OPTION_VALUE_FAILED = "option.update_option_value_failed";
    public static final String GET_OPTION_VALUE_SUCCESSFULLY = "option.get_option_value_successfully";
    public static final String OPTION_VALUE_IS_NOT_EXISTS = "option.option_value_is_not_exists";
    public static final String OPTION_VALUE_OF_OPTION_IS_NOT_EXISTS_WITH_PARAM = "option.option_value_of_option_is_not_exists_with";
    public static final String DELETE_OPTION_VALUE_SUCCESSFULLY = "option.delete_option_value_successfully";
    public static final String OPTION_NAME_CANNOT_EMPTY = "option.option_name_cannot_empty";
    public static final String OPTION_VALUE_NAME_CANNOT_EMPTY = "option.option_value_name_cannot_empty";
    public static final String OPTION_VALUE_CANNOT_EMPTY = "option.option_value_cannot_empty";
    public static final String OPTION_CANNOT_NULL = "option.option_cannot_null";
    public static final String OPTION_NAME_LENGTH = "option.option_name_length";

    public static final String ORDER_IS_NOT_FOUND = "order.order_is_not_found";
    public static final String ORDER_IS_NOT_MODIFY = "order.order_is_not_modify";
    public static final String CANCELING_ORDER_SUCCESSFULLY = "order.cancel_is_successful";
    public static final String UPDATE_STATUS_FOR_ORDER_FAILED = "order.update_status_for_order_failed";
    public static final String UPDATE_STATUS_FOR_ORDER_SUCCESSFULLY = "order.update_status_for_order_successfully";
    public static final String STATUS_MUST_FOLLOW = "order.status_must_follow";
    public static final String GET_ORDER_DETAIL_SUCCESSFULLY = "order.get_order_detail_successfully";

    public static final String INSERT_PRODUCT_SUCCESSFULLY = "product.insert_product_successfully";
    public static final String INSERT_PRODUCT_FAILED = "product.insert_product_failed";
    public static final String UPDATE_PRODUCT_SUCCESSFULLY = "product.update_product_successfully";
    public static final String UPDATE_PRODUCT_FAILED = "product.update_product_failed";
    public static final String DELETE_PRODUCT_SUCCESSFULLY = "product.delete_product_successfully";
    public static final String DELETE_PRODUCT_FAILED = "product.delete_product_failed";
    public static final String GET_PRODUCT_SUCCESSFULLY = "product.get_product_successfully";
    public static final String GET_PRODUCT_FAILED = "product.get_product_failed";
    public static final String PRODUCT_IS_NOT_EXISTS = "product.product_is_not_exists";

    public static final String GET_PROMOTION_SUCCESSFULLY = "promotion.get_promotion_successfully";
    public static final String DELETE_PROMOTION_SUCCESSFULLY = "promotion.delete_promotion_successfully";
    public static final String UPDATE_PROMOTION_SUCCESSFULLY = "promotion.update_promotion_successfully";
    public static final String UPDATE_PROMOTION_FAILED = "promotion.update_promotion_failed";
    public static final String INSERT_PROMOTION_SUCCESSFULLY = "promotion.insert_promotion_successfully";
    public static final String INSERT_PROMOTION_FAILED = "promotion.insert_promotion_failed";
    public static final String PROMOTION_IS_NOT_EXISTS = "promotion.promotion_is_not_exists";
    public static final String PROMOTION_IS_NOT_FOUND = "promotion.promotion_is_not_found";
    public static final String ADD_PROMOTION_FOR_VARIANT_SUCCESSFULLY = "promotion.add_promotion_for_variable_successfully";
    public static final String ADD_PROMOTION_FOR_VARIANT_FAILED = "promotion.add_promotion_for_variable_failed";
    public static final String DELETE_PROMOTION_FOR_VARIANT_FAILED = "promotion.delete_promotion_for_variable_failed";
    public static final String DELETE_PROMOTION_FOR_VARIANT_SUCCESSFULLY = "promotion.delete_promotion_for_variable_successfully";

    public static final String INSERT_PURCHASE_ORDER_SUCCESSFULLY = "purchase.order.insert_purchase_order_successfully";
    public static final String INSERT_PURCHASE_ORDER_FAILED = "purchase.order.insert_purchase_order_failed";
    public static final String GET_PURCHASE_ORDER_SUCCESSFULLY = "purchase.order.get_purchase_order_successfully";

    public static final String GET_ROLES_SUCCESSFULLY = "role.get_roles_successfully";
    public static final String ADD_ROLE_FOR_USER_SUCCESSFULLY = "role.add_role_for_user_successfully";
    public static final String ADD_ROLE_FOR_USER_FAILED = "role.add_role_for_user_failed";
    public static final String DELETE_ROLE_OF_USER_SUCCESSFULLY = "role.delete_role_of_user_successfully";
    public static final String DELETE_ROLE_OF_USER_FAILED = "role.delete_role_of_user_failed";

    public static final String REGISTER_USER_FAILED = "user.register_user_failed";
    public static final String REGISTER_USER_SUCCESSFULLY = "user.register_user_successfully";
    public static final String GET_USER_SUCCESSFULLY = "user.get_user_successfully";
    public static final String UPDATE_USER_FAILED = "user.update_user_failed";
    public static final String UPDATE_USER_SUCCESSFULLY = "user.update_user_successfully";
    public static final String DELETE_USER_SUCCESSFULLY = "user.delete_user_successfully";
    public static final String UNLOCK_USER_SUCCESSFULLY = "user.unlock_user_successfully";
    public static final String CHANGE_PASSWORD_SUCCESSFULLY = "user.change_password_successfully";
    public static final String CHANGE_PASSWORD_FAILED = "user.change_password_failed";

    public static final String GET_VARIANT_SUCCESSFULLY = "variant.get_variant_successfully";
    public static final String GET_VARIANT_FAILED = "variant.get_variant_failed";
    public static final String UPDATE_VARIANT_SUCCESSFULLY = "variant.update_variant_successfully";
    public static final String UPDATE_VARIANT_FAILED = "variant.update_variant_failed";
    public static final String UPDATE_QUANTITY_OF_VARIANT_SUCCESSFULLY = "variant.update_quantity_of_variant_successfully";
    public static final String UPDATE_QUANTITY_OF_VARIANT_FAILED = "variant.update_quantity_of_variant_failed";
    public static final String INSERT_VARIANT_SUCCESSFULLY = "variant.insert_variant_successfully";
    public static final String INSERT_VARIANT_FAILED = "variant.insert_variant_failed";
    public static final String DELETE_VARIANT_SUCCESSFULLY = "variant.delete_variant_successfully";
    public static final String DELETE_VARIANT_FAILED = "variant.delete_variant_failed";
    public static final String VARIANT_IS_NOT_EXISTS = "variant.variant_is_not_exists";
    public static final String VARIANT_CANNOT_BE_EMPTY = "variant.variant_cannot_be_empty";


    public static final String INSERT_VOUCHER_SUCCESSFULLY = "voucher.insert_voucher_successfully";
    public static final String INSERT_VOUCHER_FAILED = "voucher.insert_voucher_failed";
    public static final String UPDATE_VOUCHER_SUCCESSFULLY = "voucher.update_voucher_successfully";
    public static final String UPDATE_VOUCHER_FAILED = "voucher.update_voucher_failed";
    public static final String GET_VOUCHER_SUCCESSFULLY = "variant.get_voucher_successfully";
    public static final String GET_VOUCHER_FAILED = "variant.get_voucher_failed";
    public static final String VOUCHER_IS_NOT_FOUND = "variant.voucher_is_not_found";
    public static final String VOUCHER_IS_NOT_EXISTS = "variant.voucher_is_not_exists";
    public static final String VOUCHER_IS_EXISTS = "variant.voucher_is_exists";
    public static final String DELETE_VOUCHER_SUCCESSFULLY = "variant.delete_voucher_successfully";
    public static final String VOUCHER_CODE_IS_REQUIRED = "variant.voucher_code_is_required";
    public static final String VOUCHER_HAS_NOT_APPLIED = "variant.voucher_has_not_applied";
    public static final String VOUCHER_IS_EXPIRED = "variant.voucher_is_expired";
    public static final String VOUCHER_IS_MAX_USAGE = "variant.voucher_is_max_usage";
    public static final String CONDITION_USING_VOUCHER = "variant.condition_using_voucher";
    public static final String DISCOUNT_TYPE = "discount_type";
    public static final String DISCOUNT_TYPE_PERCENTAGE = "discount_type_percentage";

    public static final String PATTERN_EMAIL = "pattern.email";
    public static final String CART_ITEM_IS_NOT_EMPTY = "cart_item.is_not_empty";
    public static final String UNKNOWN_VARIANT_IS_NOT_EXISTS = "unknown.variant.is_not_exists";
    public static final String UNKNOWN_VARIANT_IS_OUT_OF_STOCK = "unknown.variant.is_out_of_stock";
    public static final String VOUCHER_NAME_HAS_NOT_APPLIED = "voucher.name_has_not_applied";
    public static final String VOUCHER_NAME_IS_EXPIRED = "voucher.name_is_expired";
    public static final String VOUCHER_NAME_IS_MAX_USAGE = "voucher.name_is_max_usage";
    public static final String VOUCHER_NAME_CONDITION_USING_VOUCHER = "voucher.name_condition_using_voucher";
    public static final String PAYMENT_METHOD_IS_NOT_SUPPORTED = "payment_method.is_not_supported";
    public static final String STATUS_ORDER_IS_NOT_SUPPORTED = "status_order.is_not_supported";


    public static final String START_DATE_AFTER_END_DATE = "start_date.after_end_date";
    public static final String PHONE_NUMBER_ALREADY_IS_USED = "phone_number_already_is_used";
    public static final String EMAIL_ALREADY_IS_USED = "email_already_is_used";
    public static final String TOTAL_OF_OPTION_IS_NOT_EXISTS = "total_of_option_is_not_exists";
    public static final String SKU_ID_IS_POSITIVE = "sku_id_is_positive";
    public static final String SKU_ID_IS_NOT_FOUND = "sku_id_is_not_found";
    public static final String PROPERTIES_IS_NOT_EMPTY = "properties.is_not_empty";
    public static final String PROPERTIES_IS_NOT_EXISTS = "properties.is_not_exists";

    public static final String CREATE_COMMENT_SUCCESSFULLY = "create_comment_successfully";
    public static final String CREATE_COMMENT_FAILED = "create_comment_failed";

    //Token
    public static final String TOKEN_IS_EXPIRED = "token_is_expired";
    public static final String TOKEN_IS_INVALID = "token_is_invalid";
    public static final String TOKEN_IS_USED = "token_is_used";

    //common
    public static final String EMAIL_IS_EXIST = "email_is_exist";
    public static final String EMAIL_IS_NOT_EXIST = "email_is_not_exist";
    public static final String PHONE_NUMBER_IS_EXIST = "phone_number_is_exist";
    public static final String DOB_IS_INVALID = "dob_is_invalid";
    public static final String PASSWORD_IS_INCORRECT = "password_is_incorrect";
    public static final String USERNAME_IS_REQUIRED = "username_is_required";
    public static final String PASSWORD_IS_REQUIRED = "password_is_required";
    public static final String ACCESS_DENIED = "access_denied";




}

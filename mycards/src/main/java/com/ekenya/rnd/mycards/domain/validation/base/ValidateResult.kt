package com.ekenya.rnd.mycards.domain.validation.base

//model validation result
data class ValidateResult(
    val isSuccess: Boolean,
    val message: Int
)
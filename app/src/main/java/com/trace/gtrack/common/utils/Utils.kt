package com.trace.gtrack.common.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * validate your email address format. Ex-akhi@mani.com
 */
fun String.isEmailValid(): Boolean {
    val pattern: Pattern
    val emailPattern =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    pattern = Pattern.compile(emailPattern)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

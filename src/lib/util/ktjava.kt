package com.yoloroy.lib.util

import kotlinx.datetime.toKotlinLocalDateTime
import java.sql.Timestamp

fun Timestamp.toKotlinLocalDateTime() = toLocalDateTime().toKotlinLocalDateTime()

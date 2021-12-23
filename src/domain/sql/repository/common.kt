package com.yoloroy.domain.sql.repository

import com.yoloroy.lib.util.ResultOf

fun <T> daoResult(value: T?) = ResultOf(value, "something went wrong in sql dao", Exception())

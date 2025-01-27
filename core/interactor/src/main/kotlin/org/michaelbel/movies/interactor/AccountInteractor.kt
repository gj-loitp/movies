package org.michaelbel.movies.interactor

import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.persistence.database.entity.AccountDb

interface AccountInteractor {

    val account: Flow<AccountDb?>

    suspend fun accountId(): Int?

    suspend fun accountExpireTime(): Long?

    suspend fun accountDetails()
}
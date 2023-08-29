package com.derra.taskyapp

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.derra.taskyapp.data.TaskyRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    @Assisted private val repository: TaskyRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val inputData = inputData.getString(TOKEN_KEY) ?: return Result.failure()

        return try {
            withContext(Dispatchers.IO) {
               repository.syncAgenda(inputData)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val TOKEN_KEY = "token_key"
    }
}
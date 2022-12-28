package com.kaeonx.nymandroidport.services

import androidx.work.multiprocess.RemoteWorkerService

// This subclass is defined in case we want to override things in RemoteWorkerService
class NymRunService : RemoteWorkerService()
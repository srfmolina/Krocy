package com.srfmolina.krocy.data.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/** Directory holding the config file; platform-specific (filesDir / ~/.krocy). */
internal expect fun serverConfigStoreDir(): String

internal fun createServerConfigDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { "${serverConfigStoreDir()}/server_config.preferences_pb".toPath() }
    )

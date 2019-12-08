package com.example.taskapplication

// From https://github.com/square/picasso/blob/174ffe22e2d2d034afe03f398eb0ab3e3e1f4916/picasso-sample/src/main/java/com/example/picasso/provider/PicassoProvider.kt

import com.squareup.picasso.Picasso

/**
 * This instance is automatically initialized with defaults that are suitable to most
 * implementations.
 *
 *  * LRU memory cache of 15% the available application RAM
 *  * Disk cache of 2% storage space up to 50MB but no less than 5MB. (Note: this is only
 * available on API 14+ *or* if you are using a standalone library that provides a disk
 * cache on all API levels like OkHttp)
 *  * Three download threads for disk and network access.
 *
 * If these settings do not meet the requirements of your application, you can construct your own
 * with full control over the configuration by using [Picasso.Builder] to create a
 * customized [Picasso] instance.
 *
 * Note: Production apps could instead use dependency injection to provide their Picasso instances.
 */
object PicassoProvider {
    private val instance: Picasso by lazy {
        Picasso
            .Builder(PicassoContentProvider.autoContext!!)
            .build()
    }

    @JvmStatic
    fun get() = instance
}
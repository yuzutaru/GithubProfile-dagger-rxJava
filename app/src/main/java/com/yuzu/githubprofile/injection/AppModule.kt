package com.yuzu.githubprofile.injection

import android.annotation.SuppressLint
import android.app.Application
import androidx.room.Room
import com.yuzu.githubprofile.repository.model.contract.*
import com.yuzu.githubprofile.repository.remote.api.ProfileApi
import com.yuzu.githubprofile.repository.model.local.*
import com.yuzu.githubprofile.repository.remote.contract.ProfileRepository
import com.yuzu.githubprofile.repository.remote.contract.ProfileRepositoryImpl
import com.yuzu.githubprofile.utils.BASE_URL
import com.yuzu.githubprofile.utils.TIMEOUT_HTTP
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by Yustar Pramudana on 18/02/2021
 */

@Module
open class AppModule(private val app: Application) {
    @Provides
    fun app(): Application {
        return app
    }

    private fun provideOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains

            val trustAllCerts =
                arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
                )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            var builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

            var loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            return builder.addInterceptor(loggingInterceptor)
                .readTimeout(TIMEOUT_HTTP.toLong(), TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_HTTP.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_HTTP.toLong(), TimeUnit.SECONDS)
                .build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    //Profile API
    @Provides
    @Singleton
    open fun profileApi(): ProfileApi {
        return Retrofit.Builder()
            .client(provideOkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
            .create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    open fun profileRepository(api: ProfileApi): ProfileRepository {
        return ProfileRepositoryImpl(api)
    }

    //User ROOM DATA
    @Provides
    @Singleton
    open fun userDBRepository(dao: UserDAO, exec: Executor): UserDBRepository {
        return UserDBRepositoryImpl(dao, exec)
    }

    @Provides
    @Singleton
    fun userDB(): UserDB {
        return Room.databaseBuilder(app, UserDB::class.java, "user.db").build()
    }

    @Provides
    @Singleton
    fun userDAO(db: UserDB): UserDAO {
        return db.userDAO()
    }

    //Profile ROOM DATA
    @Provides
    @Singleton
    open fun profileDBRepository(dao: ProfileDAO, exec: Executor): ProfileDBRepository {
        return ProfileDBRepositoryImpl(dao, exec)
    }

    @Provides
    @Singleton
    fun profileDB(): ProfileDB {
        return Room.databaseBuilder(app, ProfileDB::class.java, "profile.db").build()
    }

    @Provides
    @Singleton
    fun profileDAO(db: ProfileDB): ProfileDAO {
        return db.profileDAO()
    }

    @Provides
    @Singleton
    open fun notesDBRepository(dao: NotesDAO, exec: Executor): NotesDBRepository {
        return NotesDBRepositoryImpl(dao, exec)
    }

    @Provides
    @Singleton
    fun notesDB(): NotesDB {
        return Room.databaseBuilder(app, NotesDB::class.java, "notes.db").build()
    }

    @Provides
    @Singleton
    fun notesDAO(db: NotesDB): NotesDAO {
        return db.notesDAO()
    }


    @Singleton
    @Provides
    fun getExecutor(): Executor {
        return Executors.newFixedThreadPool(2)
    }
}
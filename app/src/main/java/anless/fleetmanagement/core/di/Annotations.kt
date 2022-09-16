package anless.fleetmanagement.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RetrofitBaseUrl

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RetrofitCarParamsUrl
package anless.fleetmanagement.core.di

import anless.fleetmanagement.car_module.data.data_source.action.ActionDataSource
import anless.fleetmanagement.car_module.data.data_source.car.CarDataSource
import anless.fleetmanagement.car_module.data.data_source.car_options.CarOptionsDataSource
import anless.fleetmanagement.car_module.data.data_source.car_params.CarParamsDataSource
import anless.fleetmanagement.car_module.data.data_source.documents.DocumentsDataSource
import anless.fleetmanagement.car_module.data.repository.*
import anless.fleetmanagement.car_module.domain.repository.*
import anless.fleetmanagement.station_module.data.data_source.StationDataSource
import anless.fleetmanagement.station_module.data.repository.StationRepositoryImpl
import anless.fleetmanagement.station_module.domain.repository.StationRepository
import anless.fleetmanagement.user_module.data.data_source.session.SessionDataSource
import anless.fleetmanagement.user_module.data.data_source.shift.ShiftDataSource
import anless.fleetmanagement.user_module.data.data_source.user.UserDataSource
import anless.fleetmanagement.user_module.data.repository.SessionRepositoryImpl
import anless.fleetmanagement.user_module.data.repository.ShiftRepositoryImpl
import anless.fleetmanagement.user_module.data.repository.UserRepositoryImpl
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.user_module.domain.repository.SessionRepository
import anless.fleetmanagement.user_module.domain.repository.ShiftRepository
import anless.fleetmanagement.user_module.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userDataSource: UserDataSource,
        authorizationManager: AuthorizationManager
    ): UserRepository {
        return UserRepositoryImpl(userDataSource, authorizationManager)
    }

    @Provides
    @Singleton
    fun provideShiftRepository(
        shiftDataSource: ShiftDataSource,
        authorizationManager: AuthorizationManager
    ): ShiftRepository {
        return ShiftRepositoryImpl(shiftDataSource, authorizationManager)
    }

    @Provides
    @Singleton
    fun provideSessionRepository(sessionDataSource: SessionDataSource): SessionRepository {
        return SessionRepositoryImpl(sessionDataSource)
    }

    @Provides
    @Singleton
    fun provideStationRepository(
        stationDataSource: StationDataSource,
        authorizationManager: AuthorizationManager
    ): StationRepository {
        return StationRepositoryImpl(stationDataSource, authorizationManager)
    }

    @Provides
    @Singleton
    fun provideCarRepository(
        authorizationManager: AuthorizationManager,
        carDataSource: CarDataSource
    ): CarRepository {
        return CarRepositoryImpl(authorizationManager, carDataSource)
    }

    @Provides
    @Singleton
    fun provideActionRepository(
        authorizationManager: AuthorizationManager,
        actionDataSource: ActionDataSource
    ): ActionRepository {
        return ActionRepositoryImpl(authorizationManager, actionDataSource)
    }

    @Provides
    @Singleton
    fun provideCarOptionsRepository(
        authorizationManager: AuthorizationManager,
        carOptionsDataSource: CarOptionsDataSource
    ): CarOptionsRepository {
        return CarOptionsRepositoryImpl(authorizationManager, carOptionsDataSource)
    }

    @Provides
    @Singleton
    fun provideDocumentsRepository(
        authorizationManager: AuthorizationManager,
        documentsDataSource: DocumentsDataSource
    ): DocumentsRepository {
        return DocumentsRepositoryImpl(authorizationManager, documentsDataSource)
    }

    @Provides
    @Singleton
    fun provideCarParamsRepository(
        authorizationManager: AuthorizationManager,
        carParamsDataSource: CarParamsDataSource
    ): CarParamsRepository {
        return CarParamsRepositoryImpl(authorizationManager, carParamsDataSource)
    }
}
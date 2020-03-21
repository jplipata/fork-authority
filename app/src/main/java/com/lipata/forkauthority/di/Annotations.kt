package com.lipata.forkauthority.di

import javax.inject.Qualifier
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class JustAteHerePref

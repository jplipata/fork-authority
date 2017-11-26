package com.lipata.forkauthority.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by jlipata on 11/24/17.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerApp { }


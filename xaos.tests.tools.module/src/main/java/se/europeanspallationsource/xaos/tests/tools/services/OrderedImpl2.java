/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.tests.tools.services;


import se.europeanspallationsource.xaos.tools.annotation.ServiceProvider;


/**
 * @author claudio.rosati@esss.se
 */
@ServiceProvider( service = OrderedInterface.class, order = 200 )
@SuppressWarnings( "ClassMayBeInterface" )
public class OrderedImpl2 implements OrderedInterface {

}

/*
 *    Copyright 2017 LMAX Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lmax.exchange.broker.service.ems.position;

@org.checkerframework.framework.qual.SubtypeOf({org.checkerframework.checker.units.qual.UnknownUnits.class})
public @interface StdUnits
{
}

@org.checkerframework.framework.qual.SubtypeOf({org.checkerframework.checker.units.qual.UnknownUnits.class})
public @interface Standardised
{
}

public class PricePoint
{
    @StdUnits
    private final long quantity;
    @Standardised
    private final long price;

    public PricePoint(@StdUnits final long quantity, @Standardised final long price)
    {
        this.quantity = quantity;
        this.price = price;
    }

    @StdUnits
    public long getQuantity()
    {
        return quantity;
    }

    @Standardised
    public long getPrice()
    {
        return price;
    }
}

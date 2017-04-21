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

@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public abstract class Foo {
    public void setToBar()
    {
        final java.util.OptionalLong bar = emitsOptionalFoo();
    }

    private abstract  @foo java.util.OptionalLong emitsOptionalFoo();
    }
}

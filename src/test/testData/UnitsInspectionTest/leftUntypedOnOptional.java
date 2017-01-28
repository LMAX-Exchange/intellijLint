@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    public void setToBar()
    {
        final java.util.OptionalLong bar = emitsOptionalFoo();
    }

    private @foo java.util.OptionalLong emitsOptionalFoo()
    {
        return null;
    }
}

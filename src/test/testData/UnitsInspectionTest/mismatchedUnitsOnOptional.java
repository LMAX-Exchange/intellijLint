@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

@org.checkerframework.framework.qual.SubtypeOf
private @interface bar {}

public class Foo {
    public void setToBar()
    {
        @foo final java.util.OptionalLong bar = emitsBar();
    }

    private @bar java.util.OptionalLong emitsBar()
    {
        return 2;
    }
}

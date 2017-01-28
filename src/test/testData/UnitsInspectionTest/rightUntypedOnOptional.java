@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    public void setToBar()
    {
        @foo final java.util.OptionalLong bar = java.util.OptionalLong.empty();
    }
}

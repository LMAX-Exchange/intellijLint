@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long emitsFoo(long baz)
    {
        return baz;
    }
}
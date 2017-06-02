@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long emitsFoo(@foo long baz)
    {
        return baz;
    }
}
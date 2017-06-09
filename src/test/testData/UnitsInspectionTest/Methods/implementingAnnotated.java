@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public @interface FooInterface
{
    void takesFoo(@foo long test)
}

public class Foo implements FooInterface {
    private void takesFoo(long test)
    {
        return;
    }
}
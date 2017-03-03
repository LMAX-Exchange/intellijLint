@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private enum Whatever {
        A,
        B
    }

    private void baz()
    {
        if (Whatever.A == Whatever.B)
        {
        }
    }
}

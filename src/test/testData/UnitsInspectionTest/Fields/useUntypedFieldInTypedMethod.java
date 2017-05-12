@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    final long bar;

    public static void useBar()
    {
        expectsFoo(bar);
    }

    public void expectsFoo(@foo long baz)
    {
        expectsFoo(baz);
    }
}

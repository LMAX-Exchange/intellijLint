@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long baz = (@foo long) 2;

    private void takesFoo(@foo long test)
    {
        baz += test;
    }

    private void useTakesFooIncorrectly()
    {
        takesFoo(0);
    }
}
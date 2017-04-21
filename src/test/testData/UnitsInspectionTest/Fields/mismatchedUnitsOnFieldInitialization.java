@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

@org.checkerframework.framework.qual.SubtypeOf
private @interface bar {}

public class Foo {
    @foo final long bar = emitsBar();

    private @bar long emitsBar()
    {
        return (@bar long) 2;
    }
}

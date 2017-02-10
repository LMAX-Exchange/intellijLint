@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

@org.checkerframework.framework.qual.SubtypeOf
private @interface bar {}

public class Foo {
    public void setToBar()
    {
        @foo final long bar;
        bar = emitsBar();
    }

    private @bar long emitsBar()
    {
        return (@bar long) 2;
    }
}

import java.util.OptionalLong;

@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

@org.checkerframework.framework.qual.SubtypeOf
private @interface bar {}

public class Foo {
    public void setToBar()
    {
        @bar long = (@bar long) 2L;
        @foo final OptionalLong baz = OptionalLong.of(bar);
    }
}

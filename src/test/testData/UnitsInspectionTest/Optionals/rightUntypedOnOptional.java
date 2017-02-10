import java.util.OptionalLong;

@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    public void setToBar()
    {
        @foo final OptionalLong baz = returnsOptionalLong();
    }

    private OptionalLong returnsOptionalLong()
    {
        return new OptionalLong(2L);
    }
}
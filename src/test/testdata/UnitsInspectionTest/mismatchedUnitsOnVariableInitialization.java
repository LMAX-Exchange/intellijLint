import java.lang.annotation.Documented;

@Documented
public @interface Unit {

}

public class Foo {
    public void setToNull()
    {
        @Unit final long bar = 1;
    }
}

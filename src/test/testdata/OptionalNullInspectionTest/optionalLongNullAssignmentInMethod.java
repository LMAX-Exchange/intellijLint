public class HasOptionalNulls {
    java.util.OptionalLong optionalLong = java.util.OptionalLong.empty();

    public void setToNull()
    {
        optionalLong = null;
    }
}

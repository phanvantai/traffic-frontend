/**
 * The Class MyCookie get data from server
 */
public class MyCookie {

    /**
     * The name of cookie.
     */
    private String name;

    /**
     * The value of cookie.
     */
    private String value;

    /**
     * Instantiates a new my cookie.
     *
     * @param name  of cookie
     * @param value of cookie
     */
    public MyCookie(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
